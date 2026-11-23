package com.stleo.ott.extractors

import com.stleo.ott.SubtitleFile
import com.stleo.ott.app
import com.stleo.ott.utils.ExtractorApi
import com.stleo.ott.utils.ExtractorLink
import com.stleo.ott.utils.newExtractorLink

open class HubuCloud: ExtractorApi() {
    override val name: String = "Hubu"
    override val mainUrl: String = "https://hubu.cloud"
    override val requiresReferer: Boolean = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val doc = app.get(url).document

        val streamUrl = doc.select("source").attr("src")
        callback.invoke(newExtractorLink(source = name, name = name, url = streamUrl))
    }
}