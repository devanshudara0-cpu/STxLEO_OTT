package com.stleo.ott.ui.player

import android.util.Log
import com.stleo.ott.APIHolder
import com.stleo.ott.AnimeLoadResponse
import com.stleo.ott.LoadResponse
import com.stleo.ott.MainAPI
import com.stleo.ott.MovieLoadResponse
import com.stleo.ott.SearchResponse
import com.stleo.ott.TvSeriesLoadResponse
import com.stleo.ott.TvType
import com.stleo.ott.isMovieType
import com.stleo.ott.mvvm.Resource
import com.stleo.ott.ui.APIRepository
import com.stleo.ott.ui.result.ResultEpisode
import com.stleo.ott.ui.result.buildResultEpisode
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull

object ServerFallbackHelper {
    private const val TAG = "ServerFallback"

    private val PREFERRED_ORDER = listOf(
        "RogMovies", "Movies4u", "ZinkMovies", "4KHDHub", "HDhub4u",
        "MoviesDrive", "VegaMovies", "BollyFlix", "HindMoviez",
        "HDMovie2", "MoviesMod", "UHDMovies", "MultiMovies"
    )

    data class FallbackResult(
        val episode: ResultEpisode,
        val response: LoadResponse,
        val providerName: String,
    )

    private fun cleanSearchTitle(raw: String): String {
        return raw
            .replace(Regex("""\[[^\]]*]|\([^)]*\)"""), " ")
            .replace(Regex("""(?i)\b(?:480p|720p|1080p|2160p|4k|uhd|hdr|hevc|x264|x265|10bit|web-?dl|webrip|bluray|hdrip|hdcam|dual\s*audio|multi\s*audio|hindi|english|dubbed|full\s*movie|download|season\s*\d+)\b"""), " ")
            .replace(Regex("""\b(?:19\d\d|20[0-3]\d)\b"""), " ")
            .replace(Regex("""[|:\-_]+"""), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun scoreMatch(targetClean: String, targetYear: Int?, candidate: SearchResponse): Int {
        val candClean = cleanSearchTitle(candidate.name).lowercase()
        val tgt = targetClean.lowercase()
        if (candClean.isBlank() || tgt.isBlank()) return 0

        var score = when {
            candClean == tgt -> 100
            candClean.startsWith(tgt) || tgt.startsWith(candClean) -> 80
            candClean.contains(tgt) || tgt.contains(candClean) -> 60
            else -> {
                val tgtWords = tgt.split(" ").filter { it.length > 2 }.toSet()
                val candWords = candClean.split(" ").filter { it.length > 2 }.toSet()
                if (tgtWords.isEmpty()) 0
                else {
                    val overlap = tgtWords.intersect(candWords).size
                    (overlap * 50) / tgtWords.size
                }
            }
        }
        if (score < 40) return 0
        if (targetYear != null && candidate.name.contains(targetYear.toString())) {
            score += 20
        }
        return score
    }

    suspend fun findFallbackEpisode(
        failedEpisode: ResultEpisode,
        failedResponse: LoadResponse?,
    ): FallbackResult? {
        val rawTitle = failedResponse?.name?.takeIf { it.isNotBlank() } ?: failedEpisode.headerName
        val cleanTitle = cleanSearchTitle(rawTitle)
        if (cleanTitle.length < 2) return null

        val targetYear = failedResponse?.year
            ?: Regex("""\b(19\d\d|20[0-3]\d)\b""").find(rawTitle)?.groupValues?.get(1)?.toIntOrNull()
        val isSeries = !failedEpisode.tvType.isMovieType()

        val providers: List<MainAPI> = APIHolder.allProviders.withLock {
            APIHolder.allProviders.toList()
        }.filter { api: MainAPI ->
            api.name != failedEpisode.apiName &&
                !api.name.contains("youtube", ignoreCase = true) &&
                api.hasMainPage
        }.sortedBy { api: MainAPI ->
            val idx = PREFERRED_ORDER.indexOfFirst { it.equals(api.name, ignoreCase = true) }
            if (idx == -1) 999 else idx
        }

        if (providers.isEmpty()) return null

        for (batch in providers.chunked(3)) {
            val found = findInBatch(batch, cleanTitle, targetYear, isSeries, failedEpisode)
            if (found != null) return found
        }
        return null
    }

    private suspend fun findInBatch(
        batch: List<MainAPI>,
        cleanTitle: String,
        targetYear: Int?,
        isSeries: Boolean,
        failedEpisode: ResultEpisode,
    ): FallbackResult? = coroutineScope {
        val candidates = batch.map { api ->
            async {
                withTimeoutOrNull(7_000L) {
                    runCatching {
                        val repo = APIRepository(api)
                        val searchRes = repo.search(cleanTitle, 1)
                        val items = (searchRes as? Resource.Success)?.value?.items.orEmpty()
                        val best = items
                            .map { it to scoreMatch(cleanTitle, targetYear, it) }
                            .filter { it.second >= 50 }
                            .maxByOrNull { it.second }
                            ?.first ?: return@runCatching null

                        val loadRes = repo.load(best.url)
                        val loaded = (loadRes as? Resource.Success)?.value ?: return@runCatching null
                        val dataStr = extractDataForEpisode(loaded, isSeries, failedEpisode)
                            ?: return@runCatching null

                        val ep = buildResultEpisode(
                            headerName = loaded.name,
                            name = failedEpisode.name ?: loaded.name,
                            poster = loaded.posterUrl ?: failedEpisode.poster,
                            episode = failedEpisode.episode,
                            seasonIndex = failedEpisode.seasonIndex,
                            season = failedEpisode.season,
                            data = dataStr,
                            apiName = api.name,
                            id = dataStr.hashCode(),
                            index = 0,
                            tvType = loaded.type,
                            parentId = loaded.url.hashCode(),
                        )
                        FallbackResult(ep, loaded, api.name)
                    }.onFailure {
                        Log.w(TAG, "Fallback probe failed for ${api.name}: ${it.message}")
                    }.getOrNull()
                }
            }
        }.awaitAll()

        candidates.firstOrNull { it != null }
    }

    private fun extractDataForEpisode(
        loaded: LoadResponse,
        isSeries: Boolean,
        failedEpisode: ResultEpisode,
    ): String? {
        return when (loaded) {
            is MovieLoadResponse -> {
                if (isSeries) null else loaded.dataUrl.takeIf { it.isNotBlank() && it != "[]" }
            }
            is TvSeriesLoadResponse -> {
                val targetEp = failedEpisode.episode
                val targetSeason = failedEpisode.season ?: failedEpisode.seasonIndex
                val match = loaded.episodes.firstOrNull { ep ->
                    (ep.episode ?: -1) == targetEp &&
                        (targetSeason == null || ep.season == null || ep.season == targetSeason)
                } ?: loaded.episodes.firstOrNull { ep ->
                    (ep.episode ?: -1) == targetEp
                } ?: if (!isSeries) loaded.episodes.firstOrNull() else null
                match?.data?.takeIf { it.isNotBlank() && it != "[]" }
            }
            is AnimeLoadResponse -> {
                val allEps = loaded.episodes.values.flatten()
                val targetEp = failedEpisode.episode
                val match = allEps.firstOrNull { (it.episode ?: -1) == targetEp }
                    ?: if (!isSeries) allEps.firstOrNull() else null
                match?.data?.takeIf { it.isNotBlank() && it != "[]" }
            }
            else -> null
        }
    }
}
