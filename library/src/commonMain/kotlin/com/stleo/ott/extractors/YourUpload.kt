package com.stleo.ott.extractors

import com.fasterxml.jackson.annotation.JsonProperty
import com.stleo.ott.app
import com.stleo.ott.utils.AppUtils.tryParseJson
import com.stleo.ott.utils.ExtractorApi
import com.stleo.ott.utils.ExtractorLink
import com.stleo.ott.utils.getQualityFromName
import com.stleo.ott.utils.newExtractorLink
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

open class YourUpload : ExtractorApi() {
    override val name = "Yourupload"
    override val mainUrl = "https://www.yourupload.com"
    override val requiresReferer = false

    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink> {
        val sources = mutableListOf<ExtractorLink>()
        with(app.get(url).document) {
            val quality = Regex("\\d{3,4}p").find(this.select("title").text())?.groupValues?.get(0)
            this.select("script").map { script ->
                if (script.data().contains("var jwplayerOptions = {")) {
                    val data = script.data().substringAfter("var jwplayerOptions = {").substringBefore(",\n")
                    val link = tryParseJson<ResponseSource>(
                        "{${data.replace("file", "\"file\"").replace("'", "\"")}}"
                    )

                    sources.add(
                        newExtractorLink(
                            source = name,
                            name = name,
                            url = link!!.file,
                        ) {
                            this.referer = url
                            this.quality = getQualityFromName(quality)
                        }
                    )
                }
            }
        }

        return sources
    }

    @Serializable
    private data class ResponseSource(
        @JsonProperty("file") @SerialName("file") val file: String,
    )
}
