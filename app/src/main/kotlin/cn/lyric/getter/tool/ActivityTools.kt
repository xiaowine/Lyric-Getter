package cn.lyric.getter.tool

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.text.method.LinkMovementMethod
import android.widget.TextView
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.R
import cn.lyric.getter.data.AppRules
import cn.lyric.getter.data.GithubReleaseApi
import cn.lyric.getter.tool.JsonTools.parseJSON
import cn.xiaowine.xkt.AcTool.openURL
import cn.xiaowine.xkt.AcTool.restartApp
import cn.xiaowine.xkt.AcTool.showToast
import cn.xiaowine.xkt.SimpleHttpTool.get
import cn.xiaowine.xkt.Tool.goMainThread
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.noties.markwon.Markwon
import io.noties.markwon.image.glide.GlideImagesPlugin
import java.io.File

@SuppressLint("StaticFieldLeak")
object ActivityTools {
    lateinit var context: Context
    lateinit var application: Application

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
            "https://xiaowine.github.io/Lyric-Getter/app_rules_version".get(onSuccess = {
                val i = it.replace("\n", "").toInt()
                if (i > getAppRules().appRulesVersion) {
                    context.getString(R.string.new_rule_detected_loading).showToast()
                    "https://xiaowine.github.io/Lyric-Getter/app_rules.json".get(onSuccess = { it1 ->
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
                    })
                }
            })
        }.start()
    }

    fun checkUpdate() {
        Thread {
            "https://api.github.com/repos/xiaowine/Lyric-Getter/releases/latest".get(onSuccess = {
                try {
                    it.parseJSON<GithubReleaseApi>().run {
                        if (tagName.split("-")[0].toInt() > BuildConfig.VERSION_CODE) {
                            goMainThread {
                                val markwon = Markwon.builder(context)
                                    .usePlugin(GlideImagesPlugin.create(context)) // 图片支持
                                    .build()
                                val markdown = " ##   $name \n" + body.trimIndent()
                                val textView = TextView(context).apply {
                                    setPadding(32, 40, 32, 40)
                                    // 可根据需求设置 maxHeight，防止太大撑满屏幕
                                    maxHeight = 2000
                                    movementMethod = LinkMovementMethod.getInstance() // 支持链接点击
                                }
                                markwon.setMarkdown(textView, markdown)
                                MaterialAlertDialogBuilder(context).apply {
                                    setTitle(R.string.new_version_detected)
                                    //setMessage("${name}\n${spannedText}")
                                    setView(textView)
                                    setPositiveButton(R.string.update) { _, _ ->
                                        assets.forEach { asset ->
                                            if (asset.name.contains("release", true) && asset.contentType == "application/vnd.android.package-archive") {
                                                asset.browserDownloadUrl.openURL()
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
            })
        }.start()
    }
}