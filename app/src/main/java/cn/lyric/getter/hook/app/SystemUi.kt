package cn.lyric.getter.hook.app


import android.media.MediaMetadata
import android.media.session.PlaybackState
import androidx.appcompat.app.ActionBarDrawerToggle.Delegate
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.R
import cn.lyric.getter.api.tools.Tools
import cn.lyric.getter.config.XposedOwnSP.config
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools
import cn.lyric.getter.tool.HookTools.context
import cn.lyric.getter.tool.HookTools.getApplication
import cn.lyric.getter.tool.LogTools.log
import cn.lyric.getter.tool.Tools.isNotNull
import cn.lyric.getter.tool.Tools.observableChange
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import kotlin.properties.Delegates


object SystemUi : BaseHook() {

    private var title: String by observableChange("") { _, newValue ->
        if (newValue.isNotEmpty()) {
            EventTools.cleanLyric(context)
        }
    }
    private var useOwnMusicController: Boolean = false
    override val name: String get() = this.javaClass.simpleName

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
                    clazz.methodFinder().filterByName("onPlaybackStateChanged").first().createHook {
                        after { hookParam ->
                            val playbackState = hookParam.args[0] as PlaybackState
                            if (playbackState.state == 2) if (!useOwnMusicController) EventTools.cleanLyric(context)
                        }
                    }
                    break
                }
            }
        }
        if (config.enhancedHiddenLyrics) {
            moduleRes.getString(R.string.enhanced_hidden_lyrics).log()
            for (i in 0..10) {
                val clazz = loadClassOrNull("com.android.systemui.statusbar.NotificationMediaManager$$i")
                clazz.log()
                if (clazz.isNotNull()) {
                    if (clazz!!.hasMethod("onMetadataChanged")) {
                        clazz.methodFinder().filterByName("onMetadataChanged").first().createHook {
                            after { hookParam ->
                                val metadata = hookParam.args[0] as MediaMetadata
                                title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
                            }
                        }
                        break
                    }
                }
            }
        }
        getApplication { application ->
            Tools.receptionLyric(application, BuildConfig.VERSION_CODE) { lyricData ->
                useOwnMusicController = lyricData.useOwnMusicController
            }
        }
    }
}
