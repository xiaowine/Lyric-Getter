package cn.lyric.getter.config

import android.content.SharedPreferences
import cn.lyric.getter.tool.ConfigTools
import de.robv.android.xposed.XSharedPreferences

class Config {
    private var config: ConfigTools

    constructor(xSharedPreferences: XSharedPreferences?) {
        config = ConfigTools(xSharedPreferences)
    }

    constructor(sharedPreferences: SharedPreferences) {
        config = ConfigTools(sharedPreferences)
    }

    fun update() {
        config.update()
    }




    fun clear() {
        config.clearConfig()
    }

    var mainSwitch: Boolean
        get() {
            return config.opt("mainSwitch", false)
        }
        set(value) {
            config.put("mainSwitch", value)
        }

}
