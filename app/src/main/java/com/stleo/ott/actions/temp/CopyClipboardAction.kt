package com.stleo.ott.actions.temp

import android.content.Context
import com.stleo.ott.actions.VideoClickAction
import com.stleo.ott.ui.result.LinkLoadingResult
import com.stleo.ott.ui.result.ResultEpisode
import com.stleo.ott.utils.txt
import com.stleo.ott.utils.UIHelper.clipboardHelper

class CopyClipboardAction: VideoClickAction() {
    override val name = txt("Copy to clipboard")

    override val oneSource = true

    override fun shouldShow(context: Context?, video: ResultEpisode?) = true

    override suspend fun runAction(
        context: Context?,
        video: ResultEpisode,
        result: LinkLoadingResult,
        index: Int?
    ) {
        if (index == null) return
        val link = result.links.getOrNull(index) ?: return
        clipboardHelper(txt(link.name), link.url)
    }
}