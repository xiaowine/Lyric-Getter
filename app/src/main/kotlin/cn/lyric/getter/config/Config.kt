package cn.lyric.getter.config

import cn.xiaowine.dsp.delegate.Delegate.serialLazy

class Config {
    var hideDesktopIcons: Boolean by serialLazy(false)
    var showAllRules: Boolean by serialLazy(false)
    var outputRepeatedLyrics: Boolean by serialLazy(false)
    var allowSomeSoftwareToOutputAfterTheScreen: Boolean by serialLazy(false)
    var enhancedHiddenLyrics: Boolean by serialLazy(false)
    var regexReplace: String by serialLazy("")
    var saltUseFlyme: Boolean by serialLazy(false)
    var updateTime: Long by serialLazy(0L)
    var showTitle: Boolean by serialLazy(false)
    var fuckwyysb163: Boolean by serialLazy(false)
}
        