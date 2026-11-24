package com.stleo.ott.plugins

import android.content.Context
import androidx.preference.PreferenceManager
import com.stleo.ott.MainAPI

object RepoToggleHelper {
    private const val DISABLED_REPOS_KEY = "DISABLED_REPO_URLS"

    fun isRepoEnabled(context: Context, repoUrl: String): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val disabled = prefs.getStringSet(DISABLED_REPOS_KEY, emptySet()) ?: emptySet()
        return !disabled.contains(repoUrl)
    }

    fun setRepoEnabled(context: Context, repoUrl: String, enabled: Boolean) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val disabled = prefs.getStringSet(DISABLED_REPOS_KEY, emptySet())?.toMutableSet() ?: mutableSetOf()
        if (enabled) {
            disabled.remove(repoUrl)
        } else {
            disabled.add(repoUrl)
        }
        prefs.edit().putStringSet(DISABLED_REPOS_KEY, disabled).apply()
    }

    fun isPluginFileEnabled(context: Context, filePath: String): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val disabled = prefs.getStringSet(DISABLED_REPOS_KEY, emptySet()) ?: emptySet()
        if (disabled.isEmpty()) return true
        for (repoUrl in disabled) {
            val folderName = PluginManager.getPluginSanitizedFileName(repoUrl)
            if (filePath.contains("/$folderName/")) {
                return false
            }
        }
        return true
    }

    fun isApiEnabled(context: Context, api: MainAPI): Boolean {
        val plugin = PluginManager.plugins[api.sourcePlugin]
        val filePath = plugin?.filename ?: api.sourcePlugin ?: return true
        return isPluginFileEnabled(context, filePath)
    }
}
