package com.stleo.ott.utils

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fleeksoft.ksoup.Ksoup
import com.stleo.ott.AudioFile
import com.stleo.ott.IDownloadableMinimum
import com.stleo.ott.SubtitleFile
import com.stleo.ott.USER_AGENT
import com.stleo.ott.app
import com.stleo.ott.extractors.Acefile
import com.stleo.ott.extractors.Ahvsh
import com.stleo.ott.extractors.Aico
import com.stleo.ott.extractors.Antarcticadocs
import com.stleo.ott.extractors.Asnwish
import com.stleo.ott.extractors.Auvexiug
import com.stleo.ott.extractors.Awish
import com.stleo.ott.extractors.BgwpCC
import com.stleo.ott.extractors.BigwarpArt
import com.stleo.ott.extractors.BigwarpIO
import com.stleo.ott.extractors.Blogger
import com.stleo.ott.extractors.ByseSX
import com.stleo.ott.extractors.Bysezejataos
import com.stleo.ott.extractors.ByseBuho
import com.stleo.ott.extractors.ByseVepoin
import com.stleo.ott.extractors.ByseQekaho
import com.stleo.ott.extractors.Cavanhabg
import com.stleo.ott.extractors.Cda
import com.stleo.ott.extractors.Cdnplayer
import com.stleo.ott.extractors.CdnwishCom
import com.stleo.ott.extractors.CloudMailRu
import com.stleo.ott.extractors.ContentX
import com.stleo.ott.extractors.CsstOnline
import com.stleo.ott.extractors.D0000d
import com.stleo.ott.extractors.D000dCom
import com.stleo.ott.extractors.DBfilm
import com.stleo.ott.extractors.Dailymotion
import com.stleo.ott.extractors.DatabaseGdrive
import com.stleo.ott.extractors.DatabaseGdrive2
import com.stleo.ott.extractors.DesuArcg
import com.stleo.ott.extractors.DesuDrive
import com.stleo.ott.extractors.DesuOdchan
import com.stleo.ott.extractors.DesuOdvip
import com.stleo.ott.extractors.Dhcplay
import com.stleo.ott.extractors.Dhtpre
import com.stleo.ott.extractors.Dokicloud
import com.stleo.ott.extractors.DoodCxExtractor
import com.stleo.ott.extractors.DoodLaExtractor
import com.stleo.ott.extractors.DoodPmExtractor
import com.stleo.ott.extractors.DoodShExtractor
import com.stleo.ott.extractors.DoodSoExtractor
import com.stleo.ott.extractors.DoodToExtractor
import com.stleo.ott.extractors.DoodWatchExtractor
import com.stleo.ott.extractors.DoodWfExtractor
import com.stleo.ott.extractors.DoodWsExtractor
import com.stleo.ott.extractors.DoodYtExtractor
import com.stleo.ott.extractors.Doodspro
import com.stleo.ott.extractors.Dsvplay
import com.stleo.ott.extractors.Doodporn
import com.stleo.ott.extractors.DoodstreamCom
import com.stleo.ott.extractors.Dooood
import com.stleo.ott.extractors.Ds2play
import com.stleo.ott.extractors.Ds2video
import com.stleo.ott.extractors.DsstOnline
import com.stleo.ott.extractors.Dumbalag
import com.stleo.ott.extractors.Dwish
import com.stleo.ott.extractors.Embedgram
import com.stleo.ott.extractors.EmturbovidExtractor
import com.stleo.ott.extractors.Evoload
import com.stleo.ott.extractors.Evoload1
import com.stleo.ott.extractors.Ewish
import com.stleo.ott.extractors.FEmbed
import com.stleo.ott.extractors.FEnet
import com.stleo.ott.extractors.Fastream
import com.stleo.ott.extractors.FeHD
import com.stleo.ott.extractors.Fembed9hd
import com.stleo.ott.extractors.FileMoon
import com.stleo.ott.extractors.FileMoonIn
import com.stleo.ott.extractors.FileMoonSx
import com.stleo.ott.extractors.FilemoonV2
import com.stleo.ott.extractors.Filesim
import com.stleo.ott.extractors.Firestream
import com.stleo.ott.extractors.FirestreamSite
import com.stleo.ott.extractors.Multimoviesshg
import com.stleo.ott.extractors.FlaswishCom
import com.stleo.ott.extractors.Flyfile
import com.stleo.ott.extractors.FourCX
import com.stleo.ott.extractors.FourPichive
import com.stleo.ott.extractors.FourPlayRu
import com.stleo.ott.extractors.Fplayer
import com.stleo.ott.extractors.FsstOnline
import com.stleo.ott.extractors.GDMirrorbot
import com.stleo.ott.extractors.GUpload
import com.stleo.ott.extractors.GamoVideo
import com.stleo.ott.extractors.Gdriveplayer
import com.stleo.ott.extractors.Gdriveplayerapi
import com.stleo.ott.extractors.Gdriveplayerapp
import com.stleo.ott.extractors.Gdriveplayerbiz
import com.stleo.ott.extractors.Gdriveplayerco
import com.stleo.ott.extractors.Gdriveplayerfun
import com.stleo.ott.extractors.Gdriveplayerio
import com.stleo.ott.extractors.Gdriveplayerme
import com.stleo.ott.extractors.Gdriveplayerorg
import com.stleo.ott.extractors.Gdriveplayerus
import com.stleo.ott.extractors.Geodailymotion
import com.stleo.ott.extractors.Gofile
import com.stleo.ott.extractors.GoodstreamExtractor
import com.stleo.ott.extractors.Guccihide
import com.stleo.ott.extractors.Guxhag
import com.stleo.ott.extractors.HDMomPlayer
import com.stleo.ott.extractors.HDPlayerSystem
import com.stleo.ott.extractors.HDStreamAble
import com.stleo.ott.extractors.Habetar
import com.stleo.ott.extractors.Handfacesnap
import com.stleo.ott.extractors.Haxloppd
import com.stleo.ott.extractors.Hexload
import com.stleo.ott.extractors.Hgcloudto
import com.stleo.ott.extractors.HglinkTo
import com.stleo.ott.extractors.HgplayCDN
import com.stleo.ott.extractors.Hotlinger
import com.stleo.ott.extractors.HubCloud
import com.stleo.ott.extractors.Hxfile
import com.stleo.ott.extractors.HlsWish
import com.stleo.ott.extractors.HubuCloud
import com.stleo.ott.extractors.InternetArchive
import com.stleo.ott.extractors.JWPlayer
import com.stleo.ott.extractors.Jeniusplay
import com.stleo.ott.extractors.Jodwish
import com.stleo.ott.extractors.Keephealth
import com.stleo.ott.extractors.KotakAnimeid
import com.stleo.ott.extractors.Kotakajair
import com.stleo.ott.extractors.Krakenfiles
import com.stleo.ott.extractors.Kswplayer
import com.stleo.ott.extractors.LayarKaca
import com.stleo.ott.extractors.Linkbox
import com.stleo.ott.extractors.LuluStream
import com.stleo.ott.extractors.Lulustream1
import com.stleo.ott.extractors.Lulustream2
import com.stleo.ott.extractors.Luluvdoo
import com.stleo.ott.extractors.Luxubu
import com.stleo.ott.extractors.Lvturbo
import com.stleo.ott.extractors.MailRu
import com.stleo.ott.extractors.Maxstream
import com.stleo.ott.extractors.Mediafire
import com.stleo.ott.extractors.Megacloud
import com.stleo.ott.extractors.Meownime
import com.stleo.ott.extractors.MetaGnathTuggers
import com.stleo.ott.extractors.MixDrop
import com.stleo.ott.extractors.MixDropAg
import com.stleo.ott.extractors.MixDropBz
import com.stleo.ott.extractors.MixDropCh
import com.stleo.ott.extractors.MixDropTo
import com.stleo.ott.extractors.MixDropPs
import com.stleo.ott.extractors.Mdy
import com.stleo.ott.extractors.MixDropSi
import com.stleo.ott.extractors.MxDropTo
import com.stleo.ott.extractors.Movhide
import com.stleo.ott.extractors.Moviehab
import com.stleo.ott.extractors.MoviehabNet
import com.stleo.ott.extractors.Moviesm4u
import com.stleo.ott.extractors.Mp4Upload
import com.stleo.ott.extractors.Multimovies
import com.stleo.ott.extractors.Mvidoo
import com.stleo.ott.extractors.MyVidPlay
import com.stleo.ott.extractors.Mwish
import com.stleo.ott.extractors.Namefacesnap
import com.stleo.ott.extractors.Nameitweb
import com.stleo.ott.extractors.NathanFromSubject
import com.stleo.ott.extractors.Nekostream
import com.stleo.ott.extractors.Nekowish
import com.stleo.ott.extractors.Neonime7n
import com.stleo.ott.extractors.Neonime8n
import com.stleo.ott.extractors.Obeywish
import com.stleo.ott.extractors.Odnoklassniki
import com.stleo.ott.extractors.Odysseusa
import com.stleo.ott.extractors.OkRuHTTP
import com.stleo.ott.extractors.OkRuHTTPMobile
import com.stleo.ott.extractors.OkRuSSL
import com.stleo.ott.extractors.OkRuSSLMobile
import com.stleo.ott.extractors.PeaceMakerst
import com.stleo.ott.extractors.Peytonepre
import com.stleo.ott.extractors.Pichive
import com.stleo.ott.extractors.PixelDrain
import com.stleo.ott.extractors.PixelDrainDev
import com.stleo.ott.extractors.PlayLtXyz
import com.stleo.ott.extractors.PlayRu
import com.stleo.ott.extractors.PlayerVoxzer
import com.stleo.ott.extractors.Playerwish
import com.stleo.ott.extractors.Playmate
import com.stleo.ott.extractors.Playmogo
import com.stleo.ott.extractors.Rabbitstream
import com.stleo.ott.extractors.RapidVid
import com.stleo.ott.extractors.Rasacintaku
import com.stleo.ott.extractors.SBfull
import com.stleo.ott.extractors.Sbasian
import com.stleo.ott.extractors.Sbface
import com.stleo.ott.extractors.Sbflix
import com.stleo.ott.extractors.Sblona
import com.stleo.ott.extractors.Sblongvu
import com.stleo.ott.extractors.Sbnet
import com.stleo.ott.extractors.Sbrapid
import com.stleo.ott.extractors.Sbsonic
import com.stleo.ott.extractors.Sbspeed
import com.stleo.ott.extractors.Sbthe
import com.stleo.ott.extractors.SecvideoOnline
import com.stleo.ott.extractors.Sendvid
import com.stleo.ott.extractors.Server1uns
import com.stleo.ott.extractors.SfastwishCom
import com.stleo.ott.extractors.ShaveTape
import com.stleo.ott.extractors.SibNet
import com.stleo.ott.extractors.Simpulumlamerop
import com.stleo.ott.extractors.Smoothpre
import com.stleo.ott.extractors.Sobreatsesuyp
import com.stleo.ott.extractors.Ssbstream
import com.stleo.ott.extractors.StreamEmbed
import com.stleo.ott.extractors.StreamHLS
import com.stleo.ott.extractors.StreamM4u
import com.stleo.ott.extractors.StreamSB
import com.stleo.ott.extractors.StreamSB1
import com.stleo.ott.extractors.StreamSB10
import com.stleo.ott.extractors.StreamSB11
import com.stleo.ott.extractors.StreamSB2
import com.stleo.ott.extractors.StreamSB3
import com.stleo.ott.extractors.StreamSB4
import com.stleo.ott.extractors.StreamSB5
import com.stleo.ott.extractors.StreamSB6
import com.stleo.ott.extractors.StreamSB7
import com.stleo.ott.extractors.StreamSB8
import com.stleo.ott.extractors.StreamSB9
import com.stleo.ott.extractors.StreamSilk
import com.stleo.ott.extractors.StreamTape
import com.stleo.ott.extractors.StreamTapeNet
import com.stleo.ott.extractors.StreamTapeXyz
import com.stleo.ott.extractors.Watchadsontape
import com.stleo.ott.extractors.StreamWishExtractor
import com.stleo.ott.extractors.StreamhideCom
import com.stleo.ott.extractors.StreamhideTo
import com.stleo.ott.extractors.Streamhub2
import com.stleo.ott.extractors.Streamlare
import com.stleo.ott.extractors.StreamoUpload
import com.stleo.ott.extractors.Streamplay
import com.stleo.ott.extractors.Streamsss
import com.stleo.ott.extractors.Streamwish2
import com.stleo.ott.extractors.Strwish
import com.stleo.ott.extractors.Strwish2
import com.stleo.ott.extractors.Supervideo
import com.stleo.ott.extractors.Swdyu
import com.stleo.ott.extractors.Swhoi
import com.stleo.ott.extractors.TRsTX
import com.stleo.ott.extractors.Tantifilm
import com.stleo.ott.extractors.TauVideo
import com.stleo.ott.extractors.Techinmind
import com.stleo.ott.extractors.Tubeless
import com.stleo.ott.extractors.Uasopt
import com.stleo.ott.extractors.Up4FunTop
import com.stleo.ott.extractors.Up4Stream
import com.stleo.ott.extractors.Upstream
import com.stleo.ott.extractors.UpstreamExtractor
import com.stleo.ott.extractors.Uqload
import com.stleo.ott.extractors.Uqload1
import com.stleo.ott.extractors.Uqload2
import com.stleo.ott.extractors.Uqloadcx
import com.stleo.ott.extractors.Uqloadbz
import com.stleo.ott.extractors.UqloadsXyz
import com.stleo.ott.extractors.Urochsunloath
import com.stleo.ott.extractors.Userload
import com.stleo.ott.extractors.Userscloud
import com.stleo.ott.extractors.Uservideo
import com.stleo.ott.extractors.Videa
import com.stleo.ott.extractors.Vicloud
import com.stleo.ott.extractors.VidHidePro
import com.stleo.ott.extractors.VidHidePro1
import com.stleo.ott.extractors.VidHidePro2
import com.stleo.ott.extractors.VidHidePro3
import com.stleo.ott.extractors.VidHidePro4
import com.stleo.ott.extractors.VidHidePro5
import com.stleo.ott.extractors.VidHidePro6
import com.stleo.ott.extractors.VidHideHub
import com.stleo.ott.extractors.Ryderjet
import com.stleo.ott.extractors.Streamcash
import com.stleo.ott.extractors.Thebesthostertv
import com.stleo.ott.extractors.VidChampions
import com.stleo.ott.extractors.VidMoxy
import com.stleo.ott.extractors.VidStack
import com.stleo.ott.extractors.VideoSeyred
import com.stleo.ott.extractors.Videzz
import com.stleo.ott.extractors.Vidgomunime
import com.stleo.ott.extractors.Vidgomunimesb
import com.stleo.ott.extractors.VidhideExtractor
import com.stleo.ott.extractors.Vidmoly
import com.stleo.ott.extractors.Vidmolyme
import com.stleo.ott.extractors.Vidmolyto
import com.stleo.ott.extractors.Vidmolybiz
import com.stleo.ott.extractors.Vido
import com.stleo.ott.extractors.Vidoza
import com.stleo.ott.extractors.VinovoSi
import com.stleo.ott.extractors.VinovoTo
import com.stleo.ott.extractors.VidNest
import com.stleo.ott.extractors.VidaaraxCom
import com.stleo.ott.extractors.VidaaraxNet
import com.stleo.ott.extractors.Vidara
import com.stleo.ott.extractors.VidaraSo
import com.stleo.ott.extractors.Vidaraa
import com.stleo.ott.extractors.Vidaratem
import com.stleo.ott.extractors.Vidaraw
import com.stleo.ott.extractors.Vidarax
import com.stleo.ott.extractors.Vidavaca
import com.stleo.ott.extractors.Vide0Net
import com.stleo.ott.extractors.Vidmatrixa
import com.stleo.ott.extractors.Vids
import com.stleo.ott.extractors.Vidsonic
import com.stleo.ott.extractors.Vixeo
import com.stleo.ott.extractors.VkExtractor
import com.stleo.ott.extractors.Voe
import com.stleo.ott.extractors.Voe1
import com.stleo.ott.extractors.Voe2
import com.stleo.ott.extractors.Vtbe
import com.stleo.ott.extractors.Wibufile
import com.stleo.ott.extractors.WishembedPro
import com.stleo.ott.extractors.Wishfast
import com.stleo.ott.extractors.Wishonly
import com.stleo.ott.extractors.XStreamCdn
import com.stleo.ott.extractors.Xenolyzb
import com.stleo.ott.extractors.Yipsu
import com.stleo.ott.extractors.YourUpload
import com.stleo.ott.extractors.YoutubeExtractor
import com.stleo.ott.extractors.YoutubeMobileExtractor
import com.stleo.ott.extractors.YoutubeNoCookieExtractor
import com.stleo.ott.extractors.YoutubeShortLinkExtractor
import com.stleo.ott.extractors.Yufiles
import com.stleo.ott.extractors.Yuguaab
import com.stleo.ott.extractors.Zplayer
import com.stleo.ott.extractors.ZplayerV2
import com.stleo.ott.extractors.Ztreamhub
import com.stleo.ott.mvvm.logError
import com.stleo.ott.utils.Coroutines.atomicListOf
import io.ktor.http.Url
import io.ktor.http.decodeURLPart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.coroutines.cancellation.CancellationException
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

/**
 * For use in the ConcatenatingMediaSource.
 * If features are missing (headers), please report and we can add it.
 * @param durationUs use Long.toUs() for easier input
 * */
data class PlayListItem(
    val url: String,
    val durationUs: Long,
)

/**
 * Converts Seconds to MicroSeconds, multiplication by 1_000_000
 * */
fun Long.toUs(): Long {
    return this * 1_000_000
}

/**
 * If your site has an unorthodox m3u8-like system where there are multiple smaller videos concatenated
 * use this.
 * */
@Suppress("DEPRECATION")
data class ExtractorLinkPlayList(
    override val source: String,
    override val name: String,
    val playlist: List<PlayListItem>,
    override var referer: String,
    override var quality: Int,
    override var headers: Map<String, String> = mapOf(),
    /** Used for getExtractorVerifierJob() */
    override var extractorData: String? = null,
    override var type: ExtractorLinkType,
    override var audioTracks: List<AudioFile> = emptyList(),
) : ExtractorLink(
    source = source,
    name = name,
    url = "",
    referer = referer,
    quality = quality,
    headers = headers,
    extractorData = extractorData,
    type = type,
    audioTracks = audioTracks
) {
    constructor(
        source: String,
        name: String,
        playlist: List<PlayListItem>,
        referer: String,
        quality: Int,
        isM3u8: Boolean = false,
        headers: Map<String, String> = mapOf(),
        extractorData: String? = null,
    ) : this(
        source = source,
        name = name,
        playlist = playlist,
        referer = referer,
        quality = quality,
        type = if (isM3u8) ExtractorLinkType.M3U8 else ExtractorLinkType.VIDEO,
        headers = headers,
        extractorData = extractorData,
    )
}

/** Metadata about the file type used for downloads and exoplayer hint,
 * if you respond with the wrong one the file will fail to download or be played */
enum class ExtractorLinkType {
    /** Single stream of bytes no matter the actual file type */
    VIDEO,

    /** Split into several .ts files, has support for encrypted m3u8s */
    M3U8,

    /** Like m3u8 but uses xml, currently no download support */
    DASH,

    /** No support at the moment */
    TORRENT,

    /** No support at the moment */
    MAGNET;

    // See https://www.iana.org/assignments/media-types/media-types.xhtml
    @JsonIgnore
    fun getMimeType(): String {
        return when (this) {
            VIDEO -> "video/mp4"
            M3U8 -> "application/x-mpegURL"
            DASH -> "application/dash+xml"
            TORRENT -> "application/x-bittorrent"
            MAGNET -> "application/x-bittorrent"
        }
    }
}

private fun inferTypeFromUrl(url: String): ExtractorLinkType {
    val path = try {
        Url(url).encodedPath.decodeURLPart()
    } catch (_: Throwable) {
        // don't log magnet links as errors
        null
    }
    return when {
        path?.endsWith(".m3u8") == true -> ExtractorLinkType.M3U8
        path?.endsWith(".mpd") == true -> ExtractorLinkType.DASH
        path?.endsWith(".torrent") == true -> ExtractorLinkType.TORRENT
        url.startsWith("magnet:") -> ExtractorLinkType.MAGNET
        else -> ExtractorLinkType.VIDEO
    }
}

val INFER_TYPE: ExtractorLinkType? = null

/**
 * [Uuid] for the ClearKey DRM scheme.
 *
 *
 * ClearKey is supported on Android devices running Android 5.0 (API Level 21) and up.
 */
val CLEARKEY_DRM_UUID = Uuid.fromLongs(-0x1d8e62a7567a4c37L, 0x781AB030AF78D30EL)

/**
 * [Uuid] for the Widevine DRM scheme.
 *
 *
 * Widevine is supported on Android devices running Android 4.3 (API Level 18) and up.
 */
val WIDEVINE_DRM_UUID = Uuid.fromLongs(-0x121074568629b532L, -0x5c37d8232ae2de13L)

/**
 * [Uuid] for the PlayReady DRM scheme.
 *
 *
 * PlayReady is supported on all AndroidTV devices. Note that most other Android devices do not
 * provide PlayReady support.
 */
val PLAYREADY_DRM_UUID = Uuid.fromLongs(-0x65fb0f8667bfbd7aL, -0x546d19a41f77a06bL)

// Deprecate after next stable

// @Deprecated("Use CLEARKEY_DRM_UUID", ReplaceWith("CLEARKEY_DRM_UUID"), level = DeprecationLevel.WARNING)
val CLEARKEY_UUID = CLEARKEY_DRM_UUID.toJavaUuid()

// @Deprecated("Use WIDEVINE_DRM_UUID", ReplaceWith("WIDEVINE_DRM_UUID"), level = DeprecationLevel.WARNING)
val WIDEVINE_UUID = WIDEVINE_DRM_UUID.toJavaUuid()

// @Deprecated("Use PLAYREADY_DRM_UUID", ReplaceWith("PLAYREADY_DRM_UUID"), level = DeprecationLevel.WARNING)
val PLAYREADY_UUID = PLAYREADY_DRM_UUID.toJavaUuid()

suspend fun newExtractorLink(
    source: String,
    name: String,
    url: String,
    type: ExtractorLinkType? = null,
    initializer: suspend ExtractorLink.() -> Unit = { }
): ExtractorLink {

    @Suppress("DEPRECATION_ERROR")
    val builder =
        ExtractorLink(
            source = source,
            name = name,
            url = url,
            type = type ?: INFER_TYPE
        )

    builder.initializer()
    return builder
}

// Deprecate after next stable
/* @Deprecated(
    message = "Use Kotlin Uuid (kotlin.uuid.Uuid) instead of Java UUID.",
    level = DeprecationLevel.WARNING,
) */
suspend fun newDrmExtractorLink(
    source: String,
    name: String,
    url: String,
    type: ExtractorLinkType? = null,
    uuid: java.util.UUID,
    initializer: suspend DrmExtractorLink.() -> Unit = { }
): DrmExtractorLink {
    @Suppress("DEPRECATION_ERROR")
    val builder =
        DrmExtractorLink(
            source = source,
            name = name,
            url = url,
            uuid = uuid.toKotlinUuid(),
            type = type ?: INFER_TYPE
        )

    builder.initializer()
    return builder
}

suspend fun newDrmExtractorLink(
    source: String,
    name: String,
    url: String,
    type: ExtractorLinkType? = null,
    uuid: Uuid,
    initializer: suspend DrmExtractorLink.() -> Unit = {},
): DrmExtractorLink {
    @Suppress("DEPRECATION_ERROR")
    val builder =
        DrmExtractorLink(
            source = source,
            name = name,
            url = url,
            uuid = uuid,
            type = type ?: INFER_TYPE
        )

    builder.initializer()
    return builder
}

/** Class holds extracted DRM media info to be passed to the player.
 * @property source Name of the media source, appears on player layout.
 * @property name Title of the media, appears on player layout.
 * @property url Url string of media file
 * @property referer Referer that will be used by network request.
 * @property quality Quality of the media file
 * @property headers Headers <String, String> map that will be used by network request.
 * @property extractorData Used for getExtractorVerifierJob()
 * @property type the type of the media, use [INFER_TYPE] if you want to auto infer the type from the url
 * @property kid  Base64 value of The KID element (Key Id) contains the identifier of the key associated with a license.
 * @property key Base64 value of Key to be used to decrypt the media file.
 * @property uuid Drm [Uuid] [WIDEVINE_DRM_UUID], [PLAYREADY_DRM_UUID], [CLEARKEY_DRM_UUID] (by default) .. etc
 * @property kty Key type "oct" (octet sequence) by default
 * @property keyRequestParameters Parameters that will used to request the key.
 * @see newDrmExtractorLink
 * */
@Suppress("DEPRECATION")
open class DrmExtractorLink private constructor(
    override val source: String,
    override val name: String,
    override val url: String,
    override var referer: String,
    override var quality: Int,
    override var headers: Map<String, String> = mapOf(),
    /** Used for getExtractorVerifierJob() */
    override var extractorData: String? = null,
    override var type: ExtractorLinkType,
    open var kid: String? = null,
    open var key: String? = null,
    open var uuid: Uuid,
    open var kty: String? = null,
    open var keyRequestParameters: HashMap<String, String>,
    open var licenseUrl: String? = null,
    override var audioTracks: List<AudioFile> = emptyList(),
) : ExtractorLink(
    source, name, url, referer, quality, headers, extractorData, type, audioTracks
) {
    @Deprecated("Use newDrmExtractorLink", level = DeprecationLevel.ERROR)
    constructor(
        source: String,
        name: String,
        url: String,
        referer: String? = null,
        quality: Int? = null,
        /** the type of the media, use INFER_TYPE if you want to auto infer the type from the url */
        type: ExtractorLinkType? = INFER_TYPE,
        headers: Map<String, String> = mapOf(),
        /** Used for getExtractorVerifierJob() */
        extractorData: String? = null,
        kid: String? = null,
        key: String? = null,
        uuid: Uuid = CLEARKEY_DRM_UUID,
        kty: String? = "oct",
        keyRequestParameters: HashMap<String, String> = hashMapOf(),
        licenseUrl: String? = null,
    ) : this(
        source = source,
        name = name,
        url = url,
        referer = referer ?: "",
        quality = quality ?: Qualities.Unknown.value,
        headers = headers,
        extractorData = extractorData,
        type = type ?: inferTypeFromUrl(url),
        kid = kid,
        key = key,
        uuid = uuid,
        keyRequestParameters = keyRequestParameters,
        kty = kty,
        licenseUrl = licenseUrl,
    )

    @Deprecated("Use newDrmExtractorLink", level = DeprecationLevel.ERROR)
    constructor(
        source: String,
        name: String,
        url: String,
        referer: String,
        quality: Int,
        /** the type of the media, use INFER_TYPE if you want to auto infer the type from the url */
        type: ExtractorLinkType?,
        headers: Map<String, String> = mapOf(),
        /** Used for getExtractorVerifierJob() */
        extractorData: String? = null,
        kid: String? = null,
        key: String? = null,
        uuid: Uuid = CLEARKEY_DRM_UUID,
        kty: String? = "oct",
        keyRequestParameters: HashMap<String, String> = hashMapOf(),
        licenseUrl: String? = null,
    ) : this(
        source = source,
        name = name,
        url = url,
        referer = referer,
        quality = quality,
        headers = headers,
        extractorData = extractorData,
        type = type ?: inferTypeFromUrl(url),
        kid = kid,
        key = key,
        uuid = uuid,
        keyRequestParameters = keyRequestParameters,
        kty = kty,
        licenseUrl = licenseUrl,
    )

    @Deprecated(message = "Use Kotlin Uuid", level = DeprecationLevel.HIDDEN)
    fun setUuid(uuid: java.util.UUID) {
        this.uuid = uuid.toKotlinUuid()
    }

    @Deprecated(message = "Use Kotlin Uuid", level = DeprecationLevel.HIDDEN)
    fun getUuid(): java.util.UUID = this.uuid.toJavaUuid()
}

/** Class holds extracted media info to be passed to the player.
 * @property source Name of the media source, appears on player layout.
 * @property name Title of the media, appears on player layout.
 * @property url Url string of media file
 * @property referer Referer that will be used by network request.
 * @property quality Quality of the media file
 * @property headers Headers <String, String> map that will be used by network request.
 * @property extractorData Used for getExtractorVerifierJob()
 * @property type Extracted link type (Video, M3u8, Dash, Torrent or Magnet)
 * @property audioTracks List of separate audio tracks that can be used with this video
 * @see newExtractorLink
 * */
@Serializable
open class ExtractorLink
@Deprecated("Use newExtractorLink", level = DeprecationLevel.WARNING)
constructor(
    @SerialName("source") open val source: String,
    @SerialName("name") open val name: String,
    @SerialName("url") override val url: String,
    @SerialName("referer") override var referer: String,
    @SerialName("quality") open var quality: Int,
    @SerialName("headers") override var headers: Map<String, String> = mapOf(),
    /** Used for getExtractorVerifierJob() */
    @SerialName("extractorData") open var extractorData: String? = null,
    @SerialName("type") open var type: ExtractorLinkType,
    /** List of separate audio tracks that can be merged with this video */
    @SerialName("audioTracks") open var audioTracks: List<AudioFile> = emptyList(),
) : IDownloadableMinimum {
    @get:JsonIgnore val isM3u8: Boolean get() = type == ExtractorLinkType.M3U8
    @get:JsonIgnore val isDash: Boolean get() = type == ExtractorLinkType.DASH

    // Cached video size
    @Transient private var videoSize: Long? = null

    /**
     * Get video size in bytes with one head request. Only available for ExtractorLinkType.Video
     * @param timeoutSeconds timeout of the head request.
     */
    suspend fun getVideoSize(timeoutSeconds: Long = 3L): Long? {
        // Content-Length is not applicable to other types of formats
        if (this.type != ExtractorLinkType.VIDEO) return null

        videoSize = videoSize ?: runCatching {
            val response =
                app.head(this.url, headers = headers, referer = referer, timeout = timeoutSeconds)
            response.headers["Content-Length"]?.toLong()
        }.getOrNull()

        return videoSize
    }

    @JsonIgnore
    fun getAllHeaders(): Map<String, String> {
        if (referer.isBlank()) {
            return headers
        } else if (headers.keys.none { it.equals("referer", ignoreCase = true) }) {
            return headers + mapOf("referer" to referer)
        }
        return headers
    }

    @Suppress("DEPRECATION")
    @Deprecated("Use newExtractorLink", level = DeprecationLevel.ERROR)
    constructor(
        source: String,
        name: String,
        url: String,
        referer: String? = null,
        quality: Int? = null,
        /** the type of the media, use INFER_TYPE if you want to auto infer the type from the url */
        type: ExtractorLinkType? = INFER_TYPE,
        headers: Map<String, String> = mapOf(),
        /** Used for getExtractorVerifierJob() */
        extractorData: String? = null,
    ) : this(
        source = source,
        name = name,
        url = url,
        referer = referer ?: "",
        quality = quality ?: Qualities.Unknown.value,
        headers = headers,
        extractorData = extractorData,
        type = type ?: inferTypeFromUrl(url)
    )

    @Suppress("DEPRECATION")
    @Deprecated("Use newExtractorLink", level = DeprecationLevel.ERROR)
    constructor(
        source: String,
        name: String,
        url: String,
        referer: String,
        quality: Int,
        /** the type of the media, use INFER_TYPE if you want to auto infer the type from the url */
        type: ExtractorLinkType?,
        headers: Map<String, String> = mapOf(),
        /** Used for getExtractorVerifierJob() */
        extractorData: String? = null,
    ) : this(
        source = source,
        name = name,
        url = url,
        referer = referer,
        quality = quality,
        headers = headers,
        extractorData = extractorData,
        type = type ?: inferTypeFromUrl(url)
    )

    /**
     * Old constructor without isDash, allows for backwards compatibility with extensions.
     * Should be removed after all extensions have updated their leovonix.jar
     **/
    @Suppress("DEPRECATION_ERROR")
    @Deprecated("Use newExtractorLink", level = DeprecationLevel.ERROR)
    constructor(
        source: String,
        name: String,
        url: String,
        referer: String,
        quality: Int,
        isM3u8: Boolean = false,
        headers: Map<String, String> = mapOf(),
        /** Used for getExtractorVerifierJob() */
        extractorData: String? = null
    ) : this(source, name, url, referer, quality, isM3u8, headers, extractorData, false)

    @Suppress("DEPRECATION")
    @Deprecated("Use newExtractorLink", level = DeprecationLevel.ERROR)
    constructor(
        source: String,
        name: String,
        url: String,
        referer: String,
        quality: Int,
        isM3u8: Boolean = false,
        headers: Map<String, String> = mapOf(),
        /** Used for getExtractorVerifierJob() */
        extractorData: String? = null,
        isDash: Boolean,
    ) : this(
        source = source,
        name = name,
        url = url,
        referer = referer,
        quality = quality,
        headers = headers,
        extractorData = extractorData,
        type = if (isDash) ExtractorLinkType.DASH else if (isM3u8) ExtractorLinkType.M3U8 else ExtractorLinkType.VIDEO
    )

    override fun toString(): String {
        return "ExtractorLink(name=$name, url=$url, referer=$referer, type=$type)"
    }
}

/**
 * Removes https:// and www.
 * To match urls regardless of schema, perhaps Url() can be used?
 */
val schemaStripRegex = Regex("""^(https:|)//(www\.|)""")

enum class Qualities(var value: Int, val defaultPriority: Int) {
    Unknown(400, 4),
    P144(144, 0), // 144p
    P240(240, 2), // 240p
    P360(360, 3), // 360p
    P480(480, 4), // 480p
    P720(720, 5), // 720p
    P1080(1080, 6), // 1080p
    P1440(1440, 7), // 1440p
    P2160(2160, 8); // 4k or 2160p

    companion object {
        fun getStringByInt(qual: Int?): String {
            return when (qual) {
                0 -> "Auto"
                Unknown.value -> ""
                P2160.value -> "4K"
                null -> ""
                else -> "${qual}p"
            }
        }

        fun getStringByIntFull(quality: Int): String {
            return when (quality) {
                0 -> "Auto"
                Unknown.value -> "Unknown"
                P2160.value -> "4K"
                else -> "${quality}p"
            }
        }
    }
}

fun getQualityFromName(qualityName: String?): Int {
    if (qualityName == null)
        return Qualities.Unknown.value

    val match = qualityName.lowercase().replace("p", "").trim()
    return when (match) {
        "4k" -> Qualities.P2160
        else -> null
    }?.value ?: match.toIntOrNull() ?: Qualities.Unknown.value
}

private val packedRegex = Regex("""eval\(function\(p,a,c,k,e,.*\)\)""")
fun getPacked(string: String): String? {
    return packedRegex.find(string)?.value
}

fun getAndUnpack(string: String): String {
    val packedText = getPacked(string)
    return JsUnpacker(packedText).unpack() ?: string
}

suspend fun unshortenLinkSafe(url: String): String {
    return try {
        if (ShortLink.isShortLink(url))
            ShortLink.unshorten(url)
        else url
    } catch (e: Exception) {
        logError(e)
        url
    }
}

suspend fun loadExtractor(
    url: String,
    subtitleCallback: (SubtitleFile) -> Unit,
    callback: (ExtractorLink) -> Unit
): Boolean {
    return loadExtractor(
        url = url,
        referer = null,
        subtitleCallback = subtitleCallback,
        callback = callback
    )
}


/**
 * Tries to load the appropriate extractor based on link, returns true if any extractor is loaded.
 * */
@Throws(CancellationException::class)
suspend fun loadExtractor(
    url: String,
    referer: String? = null,
    subtitleCallback: (SubtitleFile) -> Unit,
    callback: (ExtractorLink) -> Unit
): Boolean {
    // Ensure this coroutine has not timed out
    coroutineScope { ensureActive() }

    val currentUrl = unshortenLinkSafe(url)
    val compareUrl = currentUrl.lowercase().replace(schemaStripRegex, "")

    // Iterate in reverse order so the new registered ExtractorApi takes priority
    for (index in extractorApis.lastIndex downTo 0) {
        val extractor = extractorApis[index]
        if (compareUrl.startsWith(extractor.mainUrl.replace(schemaStripRegex, ""))) {
            try {
                extractor.getUrl(currentUrl, referer, subtitleCallback, callback)
            } catch (e: Exception) {
                logError(e)
                // Rethrow if we have timed out
                if (e is CancellationException) {
                    throw e
                }
            }
            return true
        }
    }

    // this is to match mirror domains - like example.com, example.net
    for (index in extractorApis.lastIndex downTo 0) {
        val extractor = extractorApis[index]
        if (Levenshtein.partialRatio(
                extractor.mainUrl,
                currentUrl
            ) > 80
        ) {
            try {
                extractor.getUrl(currentUrl, referer, subtitleCallback, callback)
            } catch (e: Exception) {
                logError(e)
                // Rethrow if we have timed out
                if (e is CancellationException) {
                    throw e
                }
            }
            return true
        }
    }

    return false
}

val extractorApis: AtomicMutableList<ExtractorApi> = atomicListOf(
    //AllProvider(),
    Mp4Upload(),
    StreamTape(),
    StreamTapeNet(),
    ShaveTape(),
    StreamTapeXyz(),
    Watchadsontape(),

    //mixdrop extractors
    MixDropBz(),
    MixDropCh(),
    MixDropTo(),
    MixDropAg(),
    MixDrop(),
    MixDropPs(),
    Mdy(),
    MxDropTo(),
    MixDropSi(),

    XStreamCdn(),

    StreamSB(),
    Sblona(),
    Vidgomunimesb(),
    StreamSilk(),
    StreamSB1(),
    StreamSB2(),
    StreamSB3(),
    StreamSB4(),
    StreamSB5(),
    StreamSB6(),
    StreamSB7(),
    StreamSB8(),
    StreamSB9(),
    StreamSB10(),
    StreamSB11(),
    SBfull(),
    // Streamhub(), cause Streamhub2() works
    Streamhub2(),
    Ssbstream(),
    Sbthe(),
    Vidgomunime(),
    Sbflix(),
    Streamsss(),
    Sbspeed(),
    Sbsonic(),
    Sbface(),
    Sbrapid(),
    Lvturbo(),

    Fastream(),
    Videa(),
    FEmbed(),
    FeHD(),
    Fplayer(),
    DBfilm(),
    Luxubu(),
    LayarKaca(),
    Rasacintaku(),
    FEnet(),
    Kotakajair(),
    Cdnplayer(),
    //  WatchSB(), 'cause StreamSB.kt works
    Uqload(),
    Uqload1(),
    Uqload2(),
    Uqloadcx(),
    Uqloadbz(),
    Evoload(),
    Evoload1(),
    UpstreamExtractor(),

    Odnoklassniki(),
    TauVideo(),
    SibNet(),
    ContentX(),
    Hotlinger(),
    FourCX(),
    PlayRu(),
    FourPlayRu(),
    Pichive(),
    FourPichive(),
    HDMomPlayer(),
    HDPlayerSystem(),
    VideoSeyred(),
    PeaceMakerst(),
    HDStreamAble(),
    RapidVid(),
    TRsTX(),
    VidMoxy(),
    Sobreatsesuyp(),
    PixelDrain(),
    PixelDrainDev(),
    MailRu(),

    OkRuSSL(),
    OkRuSSLMobile(),
    OkRuHTTP(),
    OkRuHTTPMobile(),
    Sendvid(),

    // dood extractors
    DoodCxExtractor(),
    DoodPmExtractor(),
    DoodToExtractor(),
    DoodSoExtractor(),
    DoodLaExtractor(),
    Dooood(),
    D0000d(),
    D000dCom(),
    DoodstreamCom(),
    DoodWsExtractor(),
    DoodShExtractor(),
    DoodWatchExtractor(),
    DoodWfExtractor(),
    DoodYtExtractor(),
    Doodspro(),
    Dsvplay(),

    // GenericM3U8(),
    Zplayer(),
    ZplayerV2(),
    Upstream(),

    Maxstream(),
    Tantifilm(),
    Userload(),
    Supervideo(),
    Streamcash(),

    // StreamSB.kt works
    //  SBPlay(),
    //  SBPlay1(),
    //  SBPlay2(),

    PlayerVoxzer(),

    Blogger(),
    YourUpload(),

    Hxfile(),
    KotakAnimeid(),
    Neonime8n(),
    Neonime7n(),
    Yufiles(),
    Aico(),

    JWPlayer(),
    Meownime(),
    DesuArcg(),
    DesuOdchan(),
    DesuOdvip(),
    DesuDrive(),


    Keephealth(),
    Sbnet(),
    Sbasian(),
    Sblongvu(),
    Fembed9hd(),
    StreamM4u(),
    Krakenfiles(),
    Gofile(),
    Vicloud(),
    Uservideo(),
    Userscloud(),
    HubuCloud(),

    Movhide(),
    StreamhideCom(),
    StreamhideTo(),
    Wibufile(),
    FileMoonIn(),
    Moviesm4u(),
    Filesim(),
    Multimoviesshg(),
    Ahvsh(),
    Guccihide(),
    FileMoon(),
    FileMoonSx(),
    FilemoonV2(),

    Vido(),
    Linkbox(),
    Acefile(),
    Embedgram(),
    Mvidoo(),
    Streamplay(),
    Vidmoly(),
    Vidmolyme(),
    Vidmolyto(),
    Vidmolybiz(),
    Voe(),
    Voe1(),
    Voe2(),
    Tubeless(),
    Moviehab(),
    MoviehabNet(),
    Jeniusplay(),
    StreamoUpload(),
    Vidara(),
    Vidavaca(),
    Vidaraa(),
    Vidaraw(),
    Vidarax(),
    VidaraSo(),
    Vidaratem(),
    VidaaraxCom(),
    VidaaraxNet(),
    Odysseusa(),
    Handfacesnap(),
    Namefacesnap(),
    Thebesthostertv(),
    Vidmatrixa(),
    VidChampions(),
    Antarcticadocs(),
    Nameitweb(),

    GamoVideo(),
    Gdriveplayerapi(),
    Gdriveplayerapp(),
    Gdriveplayerfun(),
    Gdriveplayerio(),
    Gdriveplayerme(),
    Gdriveplayerbiz(),
    Gdriveplayerorg(),
    Gdriveplayerus(),
    Gdriveplayerco(),
    GoodstreamExtractor(),
    Gdriveplayer(),
    DatabaseGdrive(),
    DatabaseGdrive2(),
    Mediafire(),

    YoutubeExtractor(),
    YoutubeShortLinkExtractor(),
    YoutubeMobileExtractor(),
    YoutubeNoCookieExtractor(),
    Streamlare(),
    PlayLtXyz(),

    Cda(),
    Dailymotion(),
    Ztreamhub(),
    Rabbitstream(),
    Dokicloud(),
    Megacloud(),
    VidhideExtractor(),
    VidHidePro(),
    VidHidePro1(),
    VidHidePro2(),
    VidHidePro3(),
    VidHidePro4(),
    VidHidePro5(),
    VidHidePro6(),
    VidHideHub(),
    Ryderjet(),
    VidNest(),
    Dhtpre(),

    // CineMM Redirects
    Dhcplay(),
    HglinkTo(),

    // CineMM mirrors
    HgplayCDN(),
    Habetar(),
    Yuguaab(),
    Guxhag(),
    Auvexiug(),
    Xenolyzb(),
    Haxloppd(),
    Cavanhabg(),
    Dumbalag(),
    Uasopt(),

    Smoothpre(),
    Peytonepre(),
    LuluStream(),
    Lulustream1(),
    Lulustream2(),
    Luluvdoo(),
    StreamWishExtractor(),
    StreamHLS(),
    BigwarpIO(),
    BigwarpArt(),
    BgwpCC(),
    WishembedPro(),
    CdnwishCom(),
    FlaswishCom(),
    SfastwishCom(),
    Playerwish(),
    StreamEmbed(),
    EmturbovidExtractor(),
    Vtbe(),
    SecvideoOnline(),
    FsstOnline(),
    CsstOnline(),
    DsstOnline(),
    Simpulumlamerop(),
    Urochsunloath(),
    NathanFromSubject(),
    Yipsu(),
    MetaGnathTuggers(),
    Geodailymotion(),
    Mwish(),
    Hgcloudto(),
    Dwish(),
    Ewish(),
    Kswplayer(),
    Wishfast(),
    Streamwish2(),
    Strwish(),
    Strwish2(),
    Awish(),
    Obeywish(),
    Jodwish(),
    Swhoi(),
    Multimovies(),
    UqloadsXyz(),
    Doodporn(),
    Asnwish(),
    Nekowish(),
    Nekostream(),
    Swdyu(),
    Wishonly(),
    Ds2play(),
    Ds2video(),
    Vidsonic(),
    Vixeo(),
    InternetArchive(),
    VidStack(),
    GDMirrorbot(),
    Techinmind(),
    Server1uns(),
    VinovoSi(),
    VinovoTo(),
    Vidoza(),
    Videzz(),
    CloudMailRu(),
    HubCloud(),
    VkExtractor(),
    Bysezejataos(),
    ByseSX(),
    ByseVepoin(),
    ByseBuho(),
    MyVidPlay(),
    Playmogo(),
    Vide0Net(),
    Up4Stream(),
    Up4FunTop(),
    GUpload(),
    HlsWish(),
    ByseQekaho(),
    Flyfile(),
    Firestream(),
    FirestreamSite(),
    Vids(),
    Playmate(),
    Hexload(),
)


fun getExtractorApiFromName(name: String): ExtractorApi {
    for (api in extractorApis) {
        if (api.name == name) return api
    }
    return extractorApis[0]
}

fun requireReferer(name: String): Boolean {
    return getExtractorApiFromName(name).requiresReferer
}

fun httpsify(url: String): String {
    return if (url.startsWith("//")) "https:$url" else url
}

suspend fun getPostForm(requestUrl: String, html: String): String? {
    val document = Ksoup.parse(html)
    val inputs = document.select("Form > input")
    if (inputs.size < 4) return null
    var op: String? = null
    var id: String? = null
    var mode: String? = null
    var hash: String? = null

    for (input in inputs) {
        val value = input.attr("value")
        when (input.attr("name")) {
            "op" -> op = value
            "id" -> id = value
            "mode" -> mode = value
            "hash" -> hash = value
            else -> Unit
        }
    }
    if (op == null || id == null || mode == null || hash == null) {
        return null
    }
    delay(5000) // ye this is needed, wont work with 0 delay

    return app.post(
        requestUrl,
        headers = mapOf(
            "content-type" to "application/x-www-form-urlencoded",
            "referer" to requestUrl,
            "user-agent" to USER_AGENT,
            "accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
        ),
        data = mapOf("op" to op, "id" to id, "mode" to mode, "hash" to hash)
    ).text
}

fun ExtractorApi.fixUrl(url: String): String {
    if (url.startsWith("http") ||
        // Do not fix JSON objects when passed as urls.
        url.startsWith("{\"")
    ) {
        return url
    }
    if (url.isEmpty()) {
        return ""
    }

    val startsWithNoHttp = url.startsWith("//")
    if (startsWithNoHttp) {
        return "https:$url"
    } else {
        if (url.startsWith('/')) {
            return mainUrl + url
        }
        return "$mainUrl/$url"
    }
}

abstract class ExtractorApi {
    abstract val name: String
    abstract val mainUrl: String
    abstract val requiresReferer: Boolean

    /** Determines which plugin a given provider is from. This is the full path to the plugin. */
    var sourcePlugin: String? = null

    //suspend fun getSafeUrl(url: String, referer: String? = null): List<ExtractorLink>? {
    //    return safeAsync { getUrl(url, referer) }
    //}

    // this is the new extractorapi, override to add subtitles and stuff
    @Throws
    open suspend fun getUrl(
        url: String,
        referer: String? = null,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        getUrl(url, referer)?.forEach(callback)
    }

    suspend fun getSafeUrl(
        url: String,
        referer: String? = null,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        try {
            getUrl(url, referer, subtitleCallback, callback)
        } catch (e: Exception) {
            logError(e)
        }
    }

    /**
     * Will throw errors, use getSafeUrl if you don't want to handle the exception yourself
     */
    @Throws
    open suspend fun getUrl(url: String, referer: String? = null): List<ExtractorLink>? {
        return emptyList()
    }

    open fun getExtractorUrl(id: String): String {
        return id
    }
}
