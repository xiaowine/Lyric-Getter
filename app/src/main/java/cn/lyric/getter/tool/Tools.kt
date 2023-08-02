@file:Suppress("DEPRECATION")

package cn.lyric.getter.tool


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.R
import cn.lyric.getter.tool.LogTools.log
import com.github.kyuubiran.ezxhelper.Log
import de.robv.android.xposed.XSharedPreferences
import java.io.DataOutputStream
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty


object Tools {
    fun <T> observableChange(initialValue: T, onChange: (oldValue: T, newValue: T) -> Unit): ReadWriteProperty<Any?, T> {
        return Delegates.observable(initialValue) { _, oldVal, newVal ->
            if (oldVal != newVal) {
                onChange(oldVal, newVal)
            }
        }
    }

    fun getPref(key: String): XSharedPreferences? {
        return try {
            val pref = XSharedPreferences(BuildConfig.APPLICATION_ID, key)
            if (pref.file.canRead()) pref else null
        } catch (e: Throwable) {
            e.log()
            null
        }
    }

    fun goMainThread(delayed: Long = 0, callback: () -> Unit): Boolean {
        return Handler(Looper.getMainLooper()).postDelayed({
            callback()
        }, delayed * 1000)
    }

    @SuppressLint("WorldReadableFiles")
    fun getSP(context: Context, key: String): SharedPreferences? {
        @Suppress("DEPRECATION") return context.createDeviceProtectedStorageContext().getSharedPreferences(key, Context.MODE_WORLD_READABLE)
    }

    fun restartTheScopedSoftware(context: Context) {
        var s = ""
        context.resources.getStringArray(R.array.need_module).forEach {
            s += "${if (it == "com.android.systemui") "killall" else "am force-stop"} $it && "
        }
        s += "echo 0"
        shell(s, true)
    }

    private fun shell(command: String, isSu: Boolean) {
        command.log()
        runCatching {
            if (isSu) {
                val p = Runtime.getRuntime().exec("su")
                val outputStream = p.outputStream
                DataOutputStream(outputStream).apply {
                    writeBytes(command)
                    flush()
                    close()
                }
                outputStream.close()
            } else {
                Runtime.getRuntime().exec(command)
            }
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