package com.stleo.ott.ui.settings.extensions

import com.stleo.ott.LeoApp.Companion.getKey
import com.stleo.ott.CommonActivity
import com.stleo.ott.ErrorLoadingException
import com.stleo.ott.InternalAPI
import com.stleo.ott.R
import com.stleo.ott.TvType
import com.stleo.ott.app
import com.stleo.ott.cmap
import com.stleo.ott.plugins.PLUGIN_VERSION_NOT_SET
import com.stleo.ott.plugins.PluginData
import com.stleo.ott.plugins.PluginManager
import com.stleo.ott.plugins.PluginManager.deletePlugin
import com.stleo.ott.plugins.PluginManager.getPluginPath
import com.stleo.ott.plugins.PluginManager.unloadPlugin
import com.stleo.ott.plugins.RepositoryManager.PREBUILT_REPOSITORIES
import com.stleo.ott.plugins.RepositoryManager.addRepository
import com.stleo.ott.plugins.RepositoryManager.convertRawGitUrl
import com.stleo.ott.plugins.RepositoryManager.convertRawGitUrlForced
import com.stleo.ott.plugins.RepositoryManager.parseRepoUrl
import com.stleo.ott.plugins.RepositoryManager.removeRepository
import com.stleo.ott.plugins.SitePlugin
import com.stleo.ott.ui.settings.ApkUpdater
import com.stleo.ott.ui.settings.ApkUpdater.isFileDigest
import com.stleo.ui.viewmodels.DigestPair
import com.stleo.ui.viewmodels.Plugin
import com.stleo.ui.viewmodels.PluginDownloadSource
import com.stleo.ui.viewmodels.PluginInfo
import com.stleo.ui.viewmodels.PluginStatus
import com.stleo.ui.viewmodels.RepositoryInfo
import com.stleo.ui.viewmodels.RepositoryManager
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.TimeUnit
import kotlin.uuid.Uuid

@InternalAPI
object AndroidRepositoryManager : RepositoryManager {
    fun RepositoryInfo.toData() =
        RepositoryData(
            name = name,
            url = url,
            iconUrl = iconUrl,
            description = description
        )

    fun RepositoryData.toInfo() =
        RepositoryInfo(
            name = name,
            url = url,
            iconUrl = iconUrl,
            description = description
        )

    override fun getRepositories(): List<RepositoryInfo> {
        return ((getKey<Array<RepositoryData>>(REPOSITORIES_KEY)
            ?: emptyArray()) + PREBUILT_REPOSITORIES).map { info ->
            info.toInfo()
        }
    }

    override suspend fun getPlugins(
        repository: RepositoryInfo,
        repositoryUuid: Uuid
    ): Pair<RepositoryInfo, List<Plugin>> =
        withContext(Dispatchers.IO) {
            val activity =
                CommonActivity.activity ?: throw ErrorLoadingException("No activity found")

            val url = parseRepoUrl(repository.url)
            if (url.isNullOrBlank()) {
                throw ErrorLoadingException(activity.getString(R.string.error_invalid_url))
            }

            val parsedRepository = app.get(
                url = convertRawGitUrl(url),
                cacheTime = 15,
                cacheUnit = TimeUnit.MINUTES
            ).parsed<com.stleo.ott.plugins.Repository>()

            /**
             * No sane repository should be able to DDoS the app when navigating to settings, so
             * we limit this to a maximum of 10
             *
             * Note that this list is on all verified repos = 1, as pluginLists is a list of lists
             * */
            require(parsedRepository.pluginLists.size <= 10)

            /** Add this as a new repository -> replaces the old data with new icon,name and description */
            addRepository(
                RepositoryData(
                    iconUrl = parsedRepository.iconUrl,
                    url = url,
                    name = parsedRepository.name,
                    description = parsedRepository.description
                )
            )

            val plugins = parsedRepository.pluginLists.cmap { url ->
                /** This should really be a list of size ~ 1 */
                app.get(
                    url = convertRawGitUrl(url),
                    cacheTime = 15,
                    cacheUnit = TimeUnit.MINUTES
                ).parsed<Array<SitePlugin>>()
            }.flatMap { plugins ->
                plugins.map { plugin ->
                    val file = getPluginPath(
                        context = activity,
                        internalName = plugin.internalName,
                        repositoryUrl = url
                    )
                    val language = plugin.language?.trim()
                    Plugin(
                        info = PluginInfo(
                            name = plugin.name,
                            status = plugin.status,
                            internalName = plugin.internalName,
                            version = plugin.version,
                            tvTypes = plugin.tvTypes?.mapNotNull { runCatching { TvType.valueOf(it) }.getOrNull() }
                                ?.toPersistentList()
                                ?: persistentListOf(),
                            authors = plugin.authors.toPersistentList(),
                            iconUrl = plugin.iconUrl,
                            language = if (language.isNullOrBlank()) {
                                null
                            } else {
                                language
                            },
                            description = plugin.description,
                            repositoryUrl = plugin.repositoryUrl
                        ),
                        source = PluginDownloadSource(
                            size = plugin.fileSize,
                            repositoryUrl = url,
                            pluginUrl = plugin.url,
                            internalName = plugin.internalName,
                            digest = DigestPair.parse(plugin.fileHash)
                        ),
                        status = PluginStatus.Idle,
                        downloadLocation = if (file.exists()) {
                            file.absolutePath
                        } else {
                            null
                        },
                        repository = repositoryUuid,
                    )
                }
            }

            val info = RepositoryInfo(
                iconUrl = parsedRepository.iconUrl,
                url = url,
                name = parsedRepository.name,
                description = parsedRepository.description,
            )
            info to plugins
        }

    override suspend fun downloadPlugin(
        source: PluginDownloadSource,
        downloadProgress: (Long, Long?) -> Unit
    ): String = withContext(Dispatchers.IO) {
        val context = CommonActivity.activity ?: throw ErrorLoadingException("No activity found")
        val file = getPluginPath(
            context = context,
            internalName = source.internalName,
            repositoryUrl = source.repositoryUrl
        )

        /**
         * Before we do anything that needs a network connection,
         * we actually check if we need to update it
         *
         * Because if the file digest is correct, then why do we need to do a network connection?
         * */
        if (isFileDigest(file, source.digest)) {
            if (!PluginManager.plugins.containsKey(file.absolutePath)) {
                val loaded = PluginManager.loadPlugin(
                    context = context,
                    file = file,
                    data = PluginData(
                        internalName = source.internalName,
                        url = source.pluginUrl,
                        isOnline = true,
                        filePath = file.absolutePath,
                        version = PLUGIN_VERSION_NOT_SET
                    )
                )
                if (!loaded) {
                    throw ErrorLoadingException("Unable to load plugin ${source.internalName}")
                }
            }
            return@withContext file.absolutePath
        }

        val tempFile = File.createTempFile(file.name, ".tmp", context.cacheDir)

        val primary = convertRawGitUrl(source.pluginUrl)
        val body = try {
            app.get(primary).body
        } catch (t: Throwable) {
            val cdnUrl = convertRawGitUrlForced(source.pluginUrl)
            if (cdnUrl != primary) {
                app.get(cdnUrl).body
            } else {
                tempFile.delete()
                throw t
            }
        }

        tempFile.outputStream().use { writeStream ->
            body.use { b ->
                val readStream = b.byteStream()
                ApkUpdater.transfer(
                    writeStream = writeStream,
                    readStream = readStream,
                    length = source.size,
                    downloadProgress = downloadProgress,
                    digest = source.digest
                )
            }
        }

        // Always load the plugin
        unloadPlugin(file.absolutePath)

        file.delete() // delete if we somehow already have this downloaded?
        file.parentFile?.mkdirs() // make the directories as Files.move needs this to work

        try {
            Files.move(
                tempFile.toPath(),
                file.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            )
        } catch (_: AtomicMoveNotSupportedException) {
            Files.move(
                tempFile.toPath(),
                file.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
        }

        val result = PluginManager.loadPlugin(
            context = context,
            file = file,
            data = PluginData(
                internalName = source.internalName,
                url = source.pluginUrl,
                isOnline = true,
                filePath = file.absolutePath,
                version = PLUGIN_VERSION_NOT_SET
            )
        )
        if (!result) {
            throw ErrorLoadingException("Unable to load plugin ${source.internalName}")
        }

        return@withContext file.absolutePath
    }

    override fun getLocalPlugins(localUuid: Uuid): List<Plugin> {
        val plugins = PluginManager.getPluginsLocal()
        return plugins.map { plugin ->
            Plugin(
                info = PluginInfo(
                    name = plugin.internalName,
                    internalName = plugin.internalName,
                    version = plugin.version,
                    tvTypes = persistentListOf(),
                    authors = persistentListOf(),
                    status = 1,
                    iconUrl = null,
                    language = null,
                    description = null,
                    repositoryUrl = null
                ),
                source = null,
                status = PluginStatus.Idle,
                downloadLocation = plugin.filePath,
                repository = localUuid,
            )
        }
    }

    override suspend fun deletePlugin(location: String) {
        deletePlugin(File(location))
    }

    override suspend fun deleteRepository(repository: RepositoryInfo) {
        val context = CommonActivity.activity ?: throw ErrorLoadingException("No activity found")
        removeRepository(
            context,
            repository.toData()
        )
    }

    override suspend fun updateRepositoryInfo(repository: RepositoryInfo) {
        addRepository(
            repository.toData()
        )
    }
}