@file:Suppress("DEPRECATION")

package cn.lyrics.getter.tool


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import cn.lyrics.getter.BuildConfig
import com.github.kyuubiran.ezxhelper.Log
import de.robv.android.xposed.XSharedPreferences


object Tools {
    const val TAG = "Lyrics Getter"


    fun getPref(key: String?): XSharedPreferences? {
        val pref = XSharedPreferences(BuildConfig.APPLICATION_ID, key)
        return if (pref.file.canRead()) pref else null
    }

    @SuppressLint("WorldReadableFiles")
    fun getSP(context: Context, key: String): SharedPreferences? {
        return context.createDeviceProtectedStorageContext().getSharedPreferences(key, Context.MODE_WORLD_READABLE)
    }

    fun catchNoClass(callback: () -> Unit) {
        runCatching { callback() }.exceptionOrNull().let {
            Log.i("${callback.javaClass.name}错误")
        }
    }

    inline fun <T> T?.isNotNull(callback: (T) -> Unit): Boolean {
        if (this != null) {
            callback(this)
            return true
        }
        return false
    }

    inline fun Boolean.isNot(callback: () -> Unit) {
        if (!this) {
            callback()
        }
    }

    inline fun Any?.isNull(callback: () -> Unit): Boolean {
        if (this == null) {
            callback()
            return true
        }
        return false
    }

    fun Any?.isNull() = this == null

    fun Any?.isNotNull() = this != null
}