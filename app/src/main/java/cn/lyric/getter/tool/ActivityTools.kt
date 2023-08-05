package cn.lyric.getter.tool


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.R
import cn.lyric.getter.data.AppRules
import cn.lyric.getter.data.GithubReleaseApi
import cn.lyric.getter.tool.JsonTools.parseJSON
import cn.lyric.getter.tool.LogTools.log
import cn.lyric.getter.tool.Tools.goMainThread
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import kotlin.system.exitProcess

@SuppressLint("StaticFieldLeak")
object ActivityTools {
    lateinit var context: Context

    var activated = false


    private val handler by lazy { Handler(Looper.getMainLooper()) }

    fun getAppRules(): AppRules {
        val file = File("${context.filesDir.path}/app_rules.json")
        if (file.canWrite()) {
            return file.reader().parseJSON<AppRules>()
        } else {
            file.deleteRecursively()
            context.assets.open("app_rules.json").use {
                return it.reader().parseJSON<AppRules>()
            }
        }
    }

    fun updateAppRules() {
        Thread {
            "https://xiaowine.github.io/Lyric-Getter/app_rules_version".getHttp {
                val i = it.replace("\n", "").toInt()
                if (i > getAppRules().appRulesVersion) {
                    context.getString(R.string.new_rule_detected_loading).showToast()
                    "https://xiaowine.github.io/Lyric-Getter/app_rules.json".getHttp { it1 ->
                        if (it1.parseJSON<AppRules>().version == BuildConfig.APP_RULES_API_VERSION) {
                            val file = File("${context.filesDir.path}/app_rules.json")
                            if (file.canWrite() || file.createNewFile()) {
                                file.writeText(it1)
                                goMainThread {
                                    MaterialAlertDialogBuilder(context).apply {
                                        setTitle(R.string.new_rule_detected)
                                        setMessage(R.string.new_rule_detected_tips)
                                        setPositiveButton(R.string.restart) { _, _ ->
                                            restartApp()
                                        }
                                        show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.start()
    }

    fun checkUpdate() {
        Thread {
            "https://api.github.com/repos/xiaowine/Lyric-Getter/releases/latest".getHttp {
                try {
                    it.parseJSON<GithubReleaseApi>().run {
                        if (tagName.split("-")[0].toInt() > BuildConfig.VERSION_CODE) {
                            goMainThread {
                                MaterialAlertDialogBuilder(context).apply {
                                    setTitle(R.string.new_version_detected)
                                    setMessage("${name}\n${body}")
                                    setPositiveButton(R.string.update) { _, _ ->
                                        assets.forEach { asset ->
                                            if (asset.name.contains("release", true) && asset.contentType == "application/vnd.android.package-archive") {
                                                asset.browserDownloadUrl.openUrl()
                                                return@forEach
                                            }
                                        }
                                    }
                                    setCancelable(false)
                                    show()
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    context.getString(R.string.check_update_error).showToast()
                }
            }
        }.start()
    }

    fun Any?.showToast() {
        runCatching {
            handler.post {
                Toast.makeText(context, this.toString(), Toast.LENGTH_LONG).show()
                this.log()
            }
        }
    }


    fun String.openUrl() {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(this)))
    }


    private fun String.getHttp(callback: (String) -> Unit): Boolean {
        return try {
            val connection = (URL(this).openConnection() as java.net.HttpURLConnection).apply {
                connectTimeout = 5000
            }
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            callback(reader.readText())
            true
        } catch (_: Exception) {
            false
        }
    }

    fun restartApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName).apply {
            this?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        context.startActivity(intent)
        exitProcess(0)
    }
}