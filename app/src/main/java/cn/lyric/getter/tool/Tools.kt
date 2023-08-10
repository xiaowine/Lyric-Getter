@file:Suppress("DEPRECATION")

package cn.lyric.getter.tool


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.R
import cn.xiaowine.xkt.LogTools.log
import de.robv.android.xposed.XSharedPreferences
import java.io.DataOutputStream


object Tools {
    fun getPref(key: String): XSharedPreferences? {
        return try {
            val pref = XSharedPreferences(BuildConfig.APPLICATION_ID, key)
            if (pref.file.canRead()) pref else null
        } catch (e: Throwable) {
            e.log()
            null
        }
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
}