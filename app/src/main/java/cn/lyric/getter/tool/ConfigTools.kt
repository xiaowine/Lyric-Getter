package cn.lyric.getter.tool

import android.annotation.SuppressLint
import android.content.SharedPreferences
import cn.xiaowine.xkt.Tool.isNull
import de.robv.android.xposed.XSharedPreferences

class ConfigTools {
    private var xSP: XSharedPreferences? = null
    private var mSP: SharedPreferences? = null
    private var mSPEditor: SharedPreferences.Editor? = null

    constructor(xSharedPreferences: XSharedPreferences?) {
        xSP = xSharedPreferences
        mSP = xSharedPreferences
    }

    @SuppressLint("CommitPrefEdits")
    constructor(sharedPreferences: SharedPreferences) {
        mSP = sharedPreferences
        mSPEditor = sharedPreferences.edit()
    }

    fun reload() {
        xSP.isNull {
            xSP = Tools.getPref("Lyric_Config")
            mSP = xSP
            return
        }
        xSP?.reload()
    }

    fun put(key: String?, any: Any) {
        when (any) {
            is Int -> mSPEditor?.putInt(key, any)
            is Long -> mSPEditor?.putLong(key, any)
            is String -> mSPEditor?.putString(key, any)
            is Boolean -> mSPEditor?.putBoolean(key, any)
            is Float -> mSPEditor?.putFloat(key, any)
        }
        mSPEditor?.apply()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> opt(key: String, defValue: T): T {
        mSP.isNull {
            return defValue
        }
        return when (defValue) {
            is String -> mSP!!.getString(key, defValue.toString()) as T
            is Long -> mSP!!.getLong(key, defValue.toLong()) as T
            is Int -> mSP!!.getInt(key, defValue) as T
            is Boolean -> mSP!!.getBoolean(key, defValue) as T
            is Float -> mSP!!.getFloat(key, defValue) as T
            else -> "" as T
        }
    }

    fun clearConfig() {
        mSPEditor?.clear()?.apply()
    }
}