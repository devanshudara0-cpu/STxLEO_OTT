package com.stleo.ott.ui.setup

import android.view.View
import android.widget.AbsListView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.stleo.ott.BuildConfig
import com.stleo.ott.LeoApp.Companion.setKey
import com.stleo.ott.CommonActivity
import com.stleo.ott.databinding.FragmentSetupLanguageBinding
import com.stleo.ott.mvvm.safe
import com.stleo.ott.plugins.PluginManager
import com.stleo.ott.R
import com.stleo.ott.ui.BaseFragment
import com.stleo.ott.ui.settings.appLanguages
import com.stleo.ott.ui.settings.getCurrentLocale
import com.stleo.ott.ui.settings.nameNextToFlagEmoji
import com.stleo.ott.utils.UIHelper.fixSystemBarsPadding

const val HAS_DONE_SETUP_KEY = "HAS_DONE_SETUP"

class SetupFragmentLanguage : BaseFragment<FragmentSetupLanguageBinding>(
    BaseFragment.BindingCreator.Inflate(FragmentSetupLanguageBinding::inflate)
) {

    override fun fixLayout(view: View) {
        fixSystemBarsPadding(view)
    }

    override fun onBindingCreated(binding: FragmentSetupLanguageBinding) {
        // We don't want a crash for all users
        safe {
            val ctx = context ?: return@safe
            val settingsManager = PreferenceManager.getDefaultSharedPreferences(ctx)

            val arrayAdapter =
                ArrayAdapter<String>(ctx, R.layout.sort_bottom_single_choice)

            binding.apply {
                // Show STxLEO OTT logo instead of cloud
                safe {
                    appIconImage.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.stleo_logo))
                }

                val tags = appLanguages.map { it.second }
                val current = getCurrentLocale(ctx).replace('_', '-')
                val currentIndex = tags.indexOf(current).takeIf { it >= 0 }
                    ?: tags.indexOf(current.substringBefore('-'))

                arrayAdapter.addAll(appLanguages.map { it.nameNextToFlagEmoji() })
                listview1.adapter = arrayAdapter
                listview1.choiceMode = AbsListView.CHOICE_MODE_SINGLE
                if (currentIndex >= 0) listview1.setItemChecked(currentIndex, true)

                listview1.setOnItemClickListener { _, _, selectedLangIndex, _ ->
                    val langTagIETF = tags[selectedLangIndex]
                    CommonActivity.setLocale(activity, langTagIETF)
                    settingsManager.edit {
                        putString(getString(R.string.locale_key), langTagIETF)
                    }
                }

                nextBtt.setOnClickListener {
                    // If no plugins go to plugins page
                    val nextDestination = if (
                        PluginManager.getPluginsOnline().isEmpty()
                        && PluginManager.getPluginsLocal().isEmpty()
                    //&& PREBUILT_REPOSITORIES.isNotEmpty()
                    ) R.id.action_navigation_global_to_navigation_setup_extensions
                    else R.id.action_navigation_setup_language_to_navigation_setup_provider_languages

                    findNavController().navigate(
                        nextDestination,
                        SetupFragmentExtensions.newInstance(true)
                    )
                }

                skipBtt.setOnClickListener {
                    setKey(HAS_DONE_SETUP_KEY, true)
                    findNavController().navigate(R.id.navigation_home)
                }
            }
        }
    }
}
