package com.stleo.ott.ui.subtitles

import com.stleo.ott.utils.BaseComposeFragment
import com.stleo.ott.utils.BaseDialogComposeFragment
import com.lagradost.cloudstream4.compose.Screen

class SubtitlesFragment2 : BaseComposeFragment(), Screen by SubtitlesScreen
class SubtitlesFragmentDialog2 : BaseDialogComposeFragment(), Screen by SubtitlesScreen