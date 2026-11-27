package com.stleo.ott.network

import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * Based on https://github.com/tachiyomiorg/tachiyomi/blob/master/app/src/main/java/eu/kanade/tachiyomi/network/DohProviders.kt
 */

fun OkHttpClient.Builder.addGenericDns(url: String, ips: List<String>) = dns(
    DnsOverHttps
        .Builder()
        .client(build())
        .url(url.toHttpUrl())
        .bootstrapDnsHosts(
            ips.map { InetAddress.getByName(it) }
        )
        .build()
)

fun OkHttpClient.Builder.addResilientSystemWithDohFallback(): OkHttpClient.Builder {
    val bootstrapClient = build()
    val cloudflareDoh = DnsOverHttps.Builder()
        .client(bootstrapClient)
        .url("https://cloudflare-dns.com/dns-query".toHttpUrl())
        .bootstrapDnsHosts(
            listOf(
                InetAddress.getByName("1.1.1.1"),
                InetAddress.getByName("1.0.0.1"),
            )
        )
        .build()
    val googleDoh = DnsOverHttps.Builder()
        .client(bootstrapClient)
        .url("https://dns.google/dns-query".toHttpUrl())
        .bootstrapDnsHosts(
            listOf(
                InetAddress.getByName("8.8.8.8"),
                InetAddress.getByName("8.8.4.4"),
            )
        )
        .build()

    return dns(object : Dns {
        override fun lookup(hostname: String): List<InetAddress> {
            val sysResult = runCatching { Dns.SYSTEM.lookup(hostname) }.getOrNull()
            val isLocalHost = hostname.equals("localhost", ignoreCase = true) ||
                hostname.startsWith("127.") || hostname.startsWith("192.168.") || hostname.startsWith("10.")
            val isSinkhole = sysResult == null || sysResult.isEmpty() || (!isLocalHost && sysResult.all { addr ->
                addr.isAnyLocalAddress || addr.isLoopbackAddress
            })
            if (!isSinkhole) return sysResult!!

            val cfResult = runCatching { cloudflareDoh.lookup(hostname) }.getOrNull()
            if (!cfResult.isNullOrEmpty()) return cfResult

            val gResult = runCatching { googleDoh.lookup(hostname) }.getOrNull()
            if (!gResult.isNullOrEmpty()) return gResult

            if (!sysResult.isNullOrEmpty()) return sysResult
            throw UnknownHostException("Failed to resolve $hostname via system DNS and DoH fallback")
        }
    })
}

fun OkHttpClient.Builder.addGoogleDns() = (
        addGenericDns(
            "https://dns.google/dns-query",
            listOf(
                "8.8.4.4",
                "8.8.8.8"
            )
        ))

fun OkHttpClient.Builder.addCloudFlareDns() = (
        addGenericDns(
            "https://cloudflare-dns.com/dns-query",
            // https://www.cloudflare.com/ips/
            listOf(
                "1.1.1.1",
                "1.0.0.1",
                "2606:4700:4700::1111",
                "2606:4700:4700::1001"
            )
        ))

// Commented out as it doesn't work
//fun OkHttpClient.Builder.addOpenDns() = (
//        addGenericDns(
//            "https://doh.opendns.com/dns-query",
//            // https://support.opendns.com/hc/en-us/articles/360038086532-Using-DNS-over-HTTPS-DoH-with-OpenDNS
//            listOf(
//                "208.67.222.222",
//                "208.67.220.220",
//                "2620:119:35::35",
//                "2620:119:53::53",
//            )
//        ))


fun OkHttpClient.Builder.addAdGuardDns() = (
        addGenericDns(
            "https://dns.adguard.com/dns-query",
            // https://github.com/AdguardTeam/AdGuardDNS
            listOf(
                // "Non-filtering"
                "94.140.14.140",
                "94.140.14.141",
            )
        ))

fun OkHttpClient.Builder.addDNSWatchDns() = (
    addGenericDns(
        "https://resolver2.dns.watch/dns-query",
        // https://dns.watch/
        listOf(
            "84.200.69.80",
            "84.200.70.40",
        )
    ))

fun OkHttpClient.Builder.addQuad9Dns() = (
    addGenericDns(
        "https://dns.quad9.net/dns-query",
        // https://www.quad9.net/service/service-addresses-and-features
        listOf(
            "9.9.9.9",
            "149.112.112.112",
        )
    ))

fun OkHttpClient.Builder.addDnsSbDns() = (
        addGenericDns(
            "https://doh.dns.sb/dns-query",
            //https://dns.sb/guide/
            listOf(
                "185.222.222.222",
                "45.11.45.11",
            )
        ))

fun OkHttpClient.Builder.addCanadianShieldDns() = (
        addGenericDns(
            "https://private.canadianshield.cira.ca/dns-query",
            //https://www.cira.ca/en/canadian-shield/configure/summary-cira-canadian-shield-dns-resolver-addresses/
            listOf(
                "149.112.121.10",
                "149.112.122.10",
            )
        ))
