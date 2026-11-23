package com.stleo.ott.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.stleo.ott.databinding.ItemLogcatBinding
import com.stleo.ott.ui.BaseDiffCallback
import com.stleo.ott.ui.NoStateAdapter
import com.stleo.ott.ui.ViewHolderState

class LogcatAdapter() : NoStateAdapter<String>(
    diffCallback = BaseDiffCallback(
        itemSame = String::equals,
        contentSame = String::equals
    )
) {
    override fun onCreateContent(parent: ViewGroup): ViewHolderState<Any> {
        return ViewHolderState(
            ItemLogcatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindContent(holder: ViewHolderState<Any>, item: String, position: Int) {
        (holder.view as? ItemLogcatBinding)?.apply {
            logText.text = item
        }
    }
}