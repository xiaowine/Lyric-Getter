package cn.lyric.getter.tool

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import cn.lyric.getter.R
import cn.xiaowine.xkt.LogTool.log
import java.io.DataOutputStream

object Tools {
    var xpActivation: Boolean = false

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

    //修复高版本安卓系统无法获取版本号
    fun PackageInfo.getVersionCode() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION")
        versionCode
    }
}