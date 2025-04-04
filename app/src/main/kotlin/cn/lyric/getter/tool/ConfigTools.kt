package cn.lyric.getter.tool

import android.annotation.SuppressLint
import cn.lyric.getter.config.Config

@SuppressLint("StaticFieldLeak")
object ConfigTools {
    val config: Config by lazy { Config() }
    val xConfig: Config by lazy { Config() }
}
