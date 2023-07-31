package cn.lyric.getter.tool


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import cn.lyric.getter.data.AppRules
import cn.lyric.getter.tool.JsonTools.parseJSON
import cn.lyric.getter.tool.LogTools.log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import kotlin.system.exitProcess

@SuppressLint("StaticFieldLeak")
object ActivityTools {
    lateinit var context: Context

    var activated = false

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    fun getAppRules(): AppRules {
        context.assets.open("app_rules.json").use {
            return it.reader().parseJSON<AppRules>()
        }
    }

    fun String.getAppVersionCode(context: Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(this, 0)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
        } catch (_: Exception) {
            0L
        }
    }

    fun Any?.showToast() {
        try {
            handler.post {
                Toast.makeText(context, this.toString(), Toast.LENGTH_LONG).show()
                this.log()
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }


    fun openUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }


    private fun getHttp(url: String, callback: (String) -> Unit): Boolean {
        return try {
            val connection = URL(url).openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            callback(reader.readLine())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun restartApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
        exitProcess(0)
    }
}