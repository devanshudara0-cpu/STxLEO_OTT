package com.stleo.ott.telemetry

import android.content.Context
import com.stleo.ott.BuildConfig
import com.stleo.ott.utils.DataStore.getKey
import com.stleo.ott.utils.DataStore.setKey
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Timer
import java.util.TimerTask
import java.util.UUID
import kotlin.concurrent.thread

object LeoTelemetry {
    private const val TELEMETRY_URL = "https://leovonix.vercel.app/api/heartbeat"
    private const val PREF_DEVICE_ID = "leovonix_telemetry_device_id"

    private var deviceId: String? = null
    private var watchTimer: Timer? = null
    private var currentTitle: String? = null

    fun init(context: Context) {
        try {
            var id = context.getKey<String>(PREF_DEVICE_ID)
            if (id.isNullOrBlank()) {
                id = UUID.randomUUID().toString()
                context.setKey(PREF_DEVICE_ID, id)
            }
            deviceId = id
        } catch (_: Throwable) {
            deviceId = "anon_" + UUID.randomUUID().toString().take(8)
        }
    }

    fun onAppOpen() {
        sendPing(event = "open", title = null, seconds = 0)
    }

    fun startWatching(title: String?) {
        stopWatching()
        currentTitle = title
        sendPing(event = "watching", title = title, seconds = 0)

        watchTimer = Timer("LeoTelemetryWatchTimer", true).apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    sendPing(event = "watching", title = currentTitle, seconds = 30)
                }
            }, 30000L, 30000L)
        }
    }

    fun stopWatching() {
        try {
            watchTimer?.cancel()
            watchTimer = null
        } catch (_: Throwable) {}
    }

    private fun sendPing(event: String, title: String?, seconds: Int) {
        thread(start = true, isDaemon = true, name = "LeoTelemetryPing") {
            try {
                val url = URL(TELEMETRY_URL)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 6000
                    readTimeout = 6000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    setRequestProperty("User-Agent", "LEOVONIX/${BuildConfig.VERSION_NAME}")
                }

                val json = JSONObject().apply {
                    put("deviceId", deviceId ?: "unknown")
                    put("event", event)
                    put("title", title ?: "Browsing Catalog")
                    put("seconds", seconds)
                    put("version", BuildConfig.VERSION_NAME)
                }

                OutputStreamWriter(conn.outputStream, "UTF-8").use {
                    it.write(json.toString())
                    it.flush()
                }

                conn.responseCode
                conn.disconnect()
            } catch (_: Throwable) {
                // Completely silent, never block or crash
            }
        }
    }
}
