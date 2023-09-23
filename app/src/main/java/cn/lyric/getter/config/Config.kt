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

    var hideDesktopIcons: Boolean
        get() {
            return config.opt("hideDesktopIcons", false)
        }
        set(value) {
            config.put("hideDesktopIcons", value)
        }
    var showAllRules: Boolean
        get() {
            return config.opt("showAllRules", false)
        }
        set(value) {
            config.put("showAllRules", value)
        }
    var outputRepeatedLyrics: Boolean
        get() {
            return config.opt("output_repeated_lyrics", false)
        }
        set(value) {
            config.put("output_repeated_lyrics", value)
        }
    var enhancedHiddenLyrics: Boolean
        get() {
            return config.opt("enhanced_hidden_lyrics", false)
        }
        set(value) {
            config.put("enhanced_hidden_lyrics", value)
        }

    var allowSomeSoftwareToOutputAfterTheScreen: Boolean
        get() {
            return config.opt("allowSomeSoftwareToOutputAfterTheScreen", false)
        }
        set(value) {
            config.put("allowSomeSoftwareToOutputAfterTheScreen", value)
        }

    var regexReplace: String
        get() {
            return config.opt("regexReplace", "")
        }
        set(value) {
            config.put("regexReplace", value)
        }

    var isFirstLookRules: Boolean
        get() {
            return config.opt("isFirstRules", true)
        }
        set(value) {
            config.put("isFirstRules", value)
        }
}
