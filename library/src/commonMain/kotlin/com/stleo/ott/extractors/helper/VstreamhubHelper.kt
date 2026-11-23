package com.stleo.ott.extractors.helper

import com.stleo.ott.SubtitleFile
import com.stleo.ott.app
import com.stleo.ott.utils.ExtractorLink
import com.stleo.ott.utils.ExtractorLinkType
import com.stleo.ott.utils.Qualities
import com.stleo.ott.utils.loadExtractor
import com.stleo.ott.utils.newExtractorLink

class VstreamhubHelper {
    companion object {
        private val baseUrl: String = "https://vstreamhub.com"
        private val baseName: String = "Vstreamhub"

        suspend fun getUrls(
            url: String,
            subtitleCallback: (SubtitleFile) -> Unit,
            callback: (ExtractorLink) -> Unit
        ) {
            if (url.startsWith(baseUrl)) {
                // Fetch links
                val doc = app.get(url).document.select("script")
                doc.forEach {
                    val innerText = it.toString()
                    if (!innerText.isNullOrEmpty()) {
                        if (innerText.contains("file:")) {
                            val startString = "file: "
                            val aa = innerText.substring(innerText.indexOf(startString))
                            val linkUrl =
                                aa.substring(startString.length + 1, aa.indexOf("\",")).trim()
                            //Log.i(baseName, "Result => (linkUrl) ${linkUrl}")
                            val exlink = newExtractorLink(
                                name = "$baseName m3u8",
                                source = baseName,
                                url = linkUrl,
                                type = ExtractorLinkType.M3U8
                            ) {
                                this.quality = Qualities.Unknown.value
                                this.referer = url
                            }
                            callback.invoke(exlink)
                        }
                        if (innerText.contains("playerInstance")) {
                            val aa =
                                innerText.substring(innerText.indexOf("playerInstance.addButton"))
                            val startString = "window.open(["
                            val bb = aa.substring(aa.indexOf(startString))
                            val datavid = bb.substring(startString.length, bb.indexOf("]"))
                                .removeSurrounding("\"")
                            if (datavid.isNotBlank()) {
                                loadExtractor(datavid, url, subtitleCallback, callback)
                                //Log.i(baseName, "Result => (datavid) ${datavid}")
                            }
                        }
                    }
                }
            }
        }
    }
}