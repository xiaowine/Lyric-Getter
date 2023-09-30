package cn.lyric.getter.config

import android.app.Application
import cn.xiaowine.dsp.DSP
import cn.xiaowine.dsp.annotation.SerializeConfig
import cn.xiaowine.dsp.data.MODE

@SerializeConfig("config", MODE.HOOK)
class Config(application: Application?, packageName: String, isXSPf: Boolean = false) : DSP(application, packageName, isXSPf) {
    var hideDesktopIcons: Boolean by serialLazy(false)
    var showAllRules: Boolean by serialLazy(false)
    var outputRepeatedLyrics: Boolean by serialLazy(false)
    var allowSomeSoftwareToOutputAfterTheScreen: Boolean by serialLazy(false)
    var enhancedHiddenLyrics: Boolean by serialLazy(false)
    var regexReplace: String by serialLazy("")
    var isFirstLookRules: Boolean by serialLazy(true)
}
