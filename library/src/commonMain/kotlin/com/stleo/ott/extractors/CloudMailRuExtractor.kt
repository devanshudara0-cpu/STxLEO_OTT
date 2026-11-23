// ! Bu araç @Kraptor123 tarafından | @kekikanime için yazılmıştır.
package com.stleo.ott.extractors

import com.stleo.ott.SubtitleFile
import com.stleo.ott.USER_AGENT
import com.stleo.ott.app
import com.stleo.ott.base64Encode
import com.stleo.ott.utils.ExtractorApi
import com.stleo.ott.utils.ExtractorLink
import com.stleo.ott.utils.INFER_TYPE
import com.stleo.ott.utils.Qualities
import com.stleo.ott.utils.newExtractorLink

open class CloudMailRu : ExtractorApi() {
    override val name            = "CloudMailRu"
    override val mainUrl         = "https://cloud.mail.ru"
    override val requiresReferer = false

    override suspend fun getUrl(url: String, referer: String?, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit) {
//        Log.d("kraptor_${this.name}","url = $url")
        val headers = mapOf(
            "Accept" to "*/*",
            "Connection" to "keep-alive",
            "Sec-Fetch-Dest" to "empty",
            "Sec-Fetch-Mode" to "cors",
            "Sec-Fetch-Site" to "cross-site",
            "Origin" to mainUrl,
            "User-Agent" to USER_AGENT,
        )
        val vidId      = url.substringAfter("public/").encodeToByteArray()
        val vidIdEnc   = base64Encode(vidId)
        val videoReq   = app.get(url, headers=headers).text
        val regex      = Regex(pattern = "videowl_view\":\\{\"count\":\"1\",\"url\":\"([^\"]*)\"\\}", options = setOf(RegexOption.IGNORE_CASE))
        val videoMatch = regex.find(videoReq)?.groupValues?.get(1).toString()
        val videoUrl   = "$videoMatch/0p/$vidIdEnc.m3u8?double_encode=1"
//        Log.d("kraptor_${this.name}","videoMatch = $videoMatch hani base64 = $vidIdEnc vidurl = $videoUrl")


        callback.invoke(
            newExtractorLink(
                source  = this.name,
                name    = this.name,
                url     = videoUrl,
                type    = INFER_TYPE
            ) {
                this.referer = "$mainUrl/"
                this.quality = Qualities.Unknown.value
                this.headers = headers
            }
        )
    }
}