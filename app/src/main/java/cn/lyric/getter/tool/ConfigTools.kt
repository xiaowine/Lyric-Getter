package cn.lyric.getter.tool


import android.content.SharedPreferences
import cn.lyric.getter.config.Config
import cn.lyric.getter.tool.Tools.isNull
import de.robv.android.xposed.XSharedPreferences

class ConfigTools {
    private var xSP: XSharedPreferences? = null
    private var mSP: SharedPreferences? = null
    private var mSPEditor: SharedPreferences.Editor? = null

    private val configName = "${Tools.TAG}_Config"
    val xConfig: Config by lazy { Config(Tools.getPref(configName)) }

    constructor(xSharedPreferences: XSharedPreferences?) {
        xSP = xSharedPreferences
        mSP = xSharedPreferences
    }


    constructor(sharedPreferences: SharedPreferences) {
        mSP = sharedPreferences
        mSPEditor = sharedPreferences.edit()
    }

    fun update() {
        if (xSP.isNull()) {
            xSP = Tools.getPref(configName)
            mSP = xSP
            return
        }
        xSP?.reload()
    }

    fun put(key: String, default: Any) {
        when (default) {
            is Int -> mSPEditor?.putInt(key, default)
            is String -> mSPEditor?.putString(key, default)
            is Boolean -> mSPEditor?.putBoolean(key, default)
            is Float -> mSPEditor?.putFloat(key, default)
        }
        mSPEditor?.apply()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> opt(key: String, defValue: T): T {
        if (mSP.isNull()) {
            return defValue
        }
        return when (defValue) {
            is String -> mSP!!.getString(key, defValue.toString()) as T
            is Int -> mSP!!.getInt(key, defValue) as T
            else -> "" as T
        }
    }


    fun clearConfig() {
        mSPEditor?.clear()?.apply()
    }
}