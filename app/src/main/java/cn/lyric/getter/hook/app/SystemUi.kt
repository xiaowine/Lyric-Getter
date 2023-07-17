package cn.lyric.getter.hook.app


import android.media.session.PlaybackState
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.api.tools.Tools
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools
import cn.lyric.getter.tool.HookTools.context
import cn.lyric.getter.tool.HookTools.getApplication
import cn.lyric.getter.tool.Tools.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder


object SystemUi : BaseHook() {

    private var useOwnMusicController: Boolean = false
    override val name: String get() = this.javaClass.simpleName

    private fun onPlaybackStateChanged(it: Class<*>) {
        it.methodFinder().filterByName("onPlaybackStateChanged").first().createHook {
            after { hookParam ->
                val playbackState = hookParam.args[0] as PlaybackState
                if (playbackState.state == 2) if (!useOwnMusicController) EventTools.cleanLyric(context)
            }
        }
    }

    private fun Class<*>.hasMethod(methodName: String): Boolean {
        val methods = declaredMethods
        for (method in methods) {
            if (method.name == methodName) {
                return true
            }
        }
        return false
    }

    override fun init() {
        loadClassOrNull("com.android.systemui.statusbar.NotificationMediaManager").isNotNull {
            it.methodFinder().filterByName("clearCurrentMediaNotificationSession").first().createHook {
                after {
                    if (!useOwnMusicController) EventTools.cleanLyric(context)
                }
            }
        }
        for (i in 0..10) {
            val clazz = loadClassOrNull("com.android.systemui.statusbar.NotificationMediaManager$$i")
            if (clazz.isNotNull()) {
                if (clazz!!.hasMethod("onPlaybackStateChanged")) {
                    onPlaybackStateChanged(clazz)
                    break
                }
            }
        }
        getApplication {
            Tools.receptionLyric(it, BuildConfig.VERSION_CODE) { lyricData ->
                useOwnMusicController = lyricData.useOwnMusicController
            }
        }
    }
}
