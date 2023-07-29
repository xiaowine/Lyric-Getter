package cn.lyric.getter.config

import android.content.SharedPreferences
import de.robv.android.xposed.XSharedPreferences
import cn.lyric.getter.tool.ConfigTools


class Config {
    var config: ConfigTools

    constructor(xSharedPreferences: XSharedPreferences?) {
        config = ConfigTools(xSharedPreferences)
    }

    constructor(sharedPreferences: SharedPreferences) {
        config = ConfigTools(sharedPreferences)
    }

    fun update() {
        config.reload()
    }


    fun clear() {
        config.clearConfig()
    }
}
