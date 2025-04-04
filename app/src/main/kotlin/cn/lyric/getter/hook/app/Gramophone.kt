package cn.lyric.getter.hook.app

import android.app.Notification
import cn.lyric.getter.api.data.ExtraData
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools.eventTools
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.EzXHelper.classLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Gramophone : BaseHook() {

    override fun init() {
        super.init()
        loadClass("androidx.media3.common.util.Util", classLoader).methodFinder()
            .first { name == "setForegroundServiceNotification" }
            .createHook {
                before { hookParam ->
                    // 获取Notification
                    val notification = hookParam.args[2] as? Notification
                    notification?.let {
                        // 获取并修改通知的属性，例如 ticker
                        val tickerText = it.tickerText ?: ""
                        //"当前前台服务通知歌词: $tickerText".log() // 输出歌词到日志
                        eventTools.sendLyric(tickerText.toString(), extra = ExtraData().apply {
                            packageName = "org.akanework.gramophone"
                            customIcon = true
                            base64Icon = "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"
                            useOwnMusicController = false
                            delay = 0
                        })
                    }
                }
            }
    }
}






