package cn.lyric.getter.hook

import cn.lyric.getter.tool.ConfigTools.xConfig
import cn.lyric.getter.config.Config

abstract class BaseHook {
    var isInit: Boolean = false
    val config: Config by lazy { xConfig }
    abstract fun init()
}