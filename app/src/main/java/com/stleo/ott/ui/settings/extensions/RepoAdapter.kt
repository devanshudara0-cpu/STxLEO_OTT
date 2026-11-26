package com.stleo.ott.ui.settings.extensions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.stleo.ott.MainActivity.Companion.afterPluginsLoadedEvent
import com.stleo.ott.R
import com.stleo.ott.databinding.RepositoryItemBinding
import com.stleo.ott.databinding.RepositoryItemTvBinding
import com.stleo.ott.plugins.PLUGIN_VERSION_NOT_SET
import com.stleo.ott.plugins.PluginData
import com.stleo.ott.plugins.PluginManager
import com.stleo.ott.plugins.RepoToggleHelper
import com.stleo.ott.plugins.RepositoryManager.ONLINE_PLUGINS_FOLDER
import com.stleo.ott.plugins.RepositoryManager.PREBUILT_REPOSITORIES
import com.stleo.ott.ui.BaseDiffCallback
import com.stleo.ott.ui.NoStateAdapter
import com.stleo.ott.ui.ViewHolderState
import com.stleo.ott.ui.settings.Globals.TV
import com.stleo.ott.ui.settings.Globals.isLayout
import com.stleo.ott.utils.Coroutines.ioSafe
import com.stleo.ott.utils.Coroutines.main
import com.stleo.ott.utils.ImageLoader.loadImage
import com.stleo.ott.utils.UIHelper.clipboardHelper
import com.stleo.ott.utils.getImageFromDrawable
import com.stleo.ott.utils.txt
import java.io.File

class RepoAdapter(
    val isSetup: Boolean,
    val clickCallback: RepoAdapter.(RepositoryData) -> Unit,
    val imageClickCallback: RepoAdapter.(RepositoryData) -> Unit,
    /** In setup mode the trash icons will be replaced with download icons */
) :
    NoStateAdapter<RepositoryData>(diffCallback = BaseDiffCallback(itemSame = { a, b ->
        a.url == b.url
    })) {

    override fun onCreateContent(parent: ViewGroup): ViewHolderState<Any> {
        val layout = if (isLayout(TV)) RepositoryItemTvBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ) else RepositoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderState(layout)
    }

    override fun onClearView(holder: ViewHolderState<Any>) {
        when (val binding = holder.view) {
            is RepositoryItemBinding -> clearImage(binding.entryIcon)
            is RepositoryItemTvBinding -> clearImage(binding.entryIcon)
        }
    }

    override fun onBindContent(holder: ViewHolderState<Any>, item: RepositoryData, position: Int) {
        val isPrebuilt = PREBUILT_REPOSITORIES.contains(item)
        val drawable =
            if (isSetup) R.drawable.netflix_download else R.drawable.ic_baseline_delete_outline_24
        when (val binding = holder.view) {
            is RepositoryItemTvBinding -> {
                binding.apply {
                    val context = root.context

                    // Setup toggle switch
                    repoSwitch.isVisible = !isSetup
                    repoSwitch.setOnCheckedChangeListener(null)
                    repoSwitch.isChecked = RepoToggleHelper.isRepoEnabled(context, item.url)
                    repoSwitch.setOnCheckedChangeListener { _, isChecked ->
                        RepoToggleHelper.setRepoEnabled(context, item.url, isChecked)
                        val folderName = PluginManager.getPluginSanitizedFileName(item.url)
                        ioSafe {
                            val online = PluginManager.getPluginsOnline()
                            val matching = online.filter { it.filePath.contains("/$folderName/") }
                            if (!isChecked) {
                                matching.forEach { PluginManager.unloadPlugin(it.filePath) }
                            } else {
                                matching.forEach { PluginManager.loadPlugin(context, File(it.filePath), it) }
                                val repoDir = File("${context.filesDir}/${ONLINE_PLUGINS_FOLDER}/$folderName")
                                if (repoDir.exists() && repoDir.isDirectory) {
                                    repoDir.listFiles()?.forEach { file ->
                                        if ((file.extension == "leo" || file.extension == "cs3" || file.extension == "zip") && matching.none { it.filePath == file.absolutePath }) {
                                            PluginManager.loadPlugin(
                                                context,
                                                file,
                                                PluginData(file.name, null, true, file.absolutePath, PLUGIN_VERSION_NOT_SET)
                                            )
                                        }
                                    }
                                }
                            }
                            main {
                                afterPluginsLoadedEvent.invoke(false)
                            }
                        }
                    }

                    // Only shows icon if on setup or if it isn't a prebuilt repo.
                    // No delete buttons on prebuilt repos.
                    actionButton.isVisible = !isPrebuilt || isSetup
                    if (!isPrebuilt || isSetup) {
                        actionButton.setImageResource(drawable)
                        actionButton.setOnClickListener {
                            imageClickCallback(item)
                        }
                    } else {
                        actionButton.setOnClickListener(null)
                    }

                    repositoryItemRoot.setOnClickListener {
                        clickCallback(item)
                    }
                    mainText.text = item.name
                    subText.text = item.url
                    if (!item.iconUrl.isNullOrEmpty()) {
                        entryIcon.loadImage(item.iconUrl) {
                            error(
                                getImageFromDrawable(
                                    binding.root.context,
                                    R.drawable.ic_github_logo
                                )
                            )
                        }
                    } else {
                        entryIcon.loadImage(R.drawable.ic_github_logo)
                    }
                }
            }

            is RepositoryItemBinding -> {
                binding.apply {
                    val context = root.context

                    // Setup toggle switch
                    repoSwitch.isVisible = !isSetup
                    repoSwitch.setOnCheckedChangeListener(null)
                    repoSwitch.isChecked = RepoToggleHelper.isRepoEnabled(context, item.url)
                    repoSwitch.setOnCheckedChangeListener { _, isChecked ->
                        RepoToggleHelper.setRepoEnabled(context, item.url, isChecked)
                        val folderName = PluginManager.getPluginSanitizedFileName(item.url)
                        ioSafe {
                            val online = PluginManager.getPluginsOnline()
                            val matching = online.filter { it.filePath.contains("/$folderName/") }
                            if (!isChecked) {
                                matching.forEach { PluginManager.unloadPlugin(it.filePath) }
                            } else {
                                matching.forEach { PluginManager.loadPlugin(context, File(it.filePath), it) }
                                val repoDir = File("${context.filesDir}/${ONLINE_PLUGINS_FOLDER}/$folderName")
                                if (repoDir.exists() && repoDir.isDirectory) {
                                    repoDir.listFiles()?.forEach { file ->
                                        if ((file.extension == "leo" || file.extension == "cs3" || file.extension == "zip") && matching.none { it.filePath == file.absolutePath }) {
                                            PluginManager.loadPlugin(
                                                context,
                                                file,
                                                PluginData(file.name, null, true, file.absolutePath, PLUGIN_VERSION_NOT_SET)
                                            )
                                        }
                                    }
                                }
                            }
                            main {
                                afterPluginsLoadedEvent.invoke(false)
                            }
                        }
                    }

                    // Only shows icon if on setup or if it isn't a prebuilt repo.
                    // No delete buttons on prebuilt repos.
                    actionButton.isVisible = !isPrebuilt || isSetup
                    if (!isPrebuilt || isSetup) {
                        actionButton.setImageResource(drawable)
                        actionButton.setOnClickListener {
                            imageClickCallback(item)
                        }
                    } else {
                        actionButton.setOnClickListener(null)
                    }

                    repositoryItemRoot.setOnClickListener {
                        clickCallback(item)
                    }

                    repositoryItemRoot.setOnLongClickListener(null)

                    mainText.text = item.name
                    subText.text = item.url
                    if (!item.iconUrl.isNullOrEmpty()) {
                        entryIcon.loadImage(item.iconUrl) {
                            error(
                                getImageFromDrawable(
                                    binding.root.context,
                                    R.drawable.ic_github_logo
                                )
                            )
                        }
                    } else {
                        entryIcon.loadImage(R.drawable.ic_github_logo)
                    }
                }
            }
        }
    }

    companion object {
        const val SHAREABLE_REPO_SEPARATOR = " : "
    }
}