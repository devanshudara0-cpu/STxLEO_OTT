package com.stleo.ott.extractors

import com.stleo.ott.SubtitleFile
import com.stleo.ott.app
import com.stleo.ott.extractors.helper.JwPlayerHelper
import com.stleo.ott.utils.ExtractorApi
import com.stleo.ott.utils.ExtractorLink
import com.stleo.ott.utils.JsUnpacker


open class Vtbe : ExtractorApi() {
    override var name = "Vtbe"
    override var mainUrl = "https://vtbe.to"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val response = app.get(url,referer=mainUrl).document
        val extractedpack = response.selectFirst("script:containsData(function(p,a,c,k,e,d))")?.data().toString()
        JsUnpacker(extractedpack).unpack()?.let { unPacked ->
            JwPlayerHelper.extractStreamLinks(unPacked, name, mainUrl, callback, subtitleCallback)
        }
    }
}
