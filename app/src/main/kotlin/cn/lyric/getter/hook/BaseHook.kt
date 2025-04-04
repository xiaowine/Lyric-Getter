package cn.lyric.getter.hook

import cn.lyric.getter.BuildConfig
import cn.xiaowine.dsp.DSP
import cn.xiaowine.dsp.data.MODE

abstract class BaseHook {
    var isInit: Boolean = false
    open fun init() {
        DSP.init(null, BuildConfig.APPLICATION_ID, MODE.HOOK, true)
    }
}