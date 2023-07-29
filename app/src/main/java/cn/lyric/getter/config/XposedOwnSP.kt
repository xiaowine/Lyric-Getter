package cn.lyric.getter.config

import cn.lyric.getter.tool.Tools


object XposedOwnSP {

    val config: Config by lazy { Config(Tools.getPref("Config")) }
}