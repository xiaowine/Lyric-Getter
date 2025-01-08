package cn.lyric.getter.tool

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import cn.lyric.getter.R
import cn.xiaowine.xkt.LogTool.log
import java.io.DataOutputStream

object Tools {
    var xpActivation: Boolean = false

    val getPhoneName by lazy {
        val marketName = getSystemProperties("ro.product.marketname")
        val vivomarketName = getSystemProperties("ro.vivo.market.name")
        if (bigtextone(Build.BRAND) =="Vivo" ){
            bigtextone(vivomarketName)
        } else{
            if (marketName.isNotEmpty()) bigtextone(marketName) else bigtextone(Build.BRAND) + " " + Build.MODEL
        }
    }

    fun bigtextone(st:String): String {
        val formattedBrand = st.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        return formattedBrand
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

    //修复高版本安卓系统无法获取版本号
    fun PackageInfo.getVersionCode() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION")
        versionCode
    }

    @SuppressLint("PrivateApi")
    fun getSystemProperties(key: String): String {
        val ret: String = try {
            Class.forName("android.os.SystemProperties").getDeclaredMethod("get", String::class.java).invoke(null, key) as String
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            ""
        }
        return ret
    }
}