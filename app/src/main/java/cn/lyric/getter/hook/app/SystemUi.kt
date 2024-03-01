package cn.lyric.getter.hook.app


import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.os.Build
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.R
import cn.lyric.getter.tool.ConfigTools.xConfig as config
import cn.lyric.getter.api.data.LyricData
import cn.lyric.getter.api.listener.LyricListener
import cn.lyric.getter.api.listener.LyricReceiver
import cn.lyric.getter.api.tools.Tools
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools.eventTools
import cn.lyric.getter.tool.HookTools.getApplication
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.isNotNull
import cn.xiaowine.xkt.Tool.observableChange
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder


object SystemUi : BaseHook() {
    var isPlayer: Boolean = false
    private lateinit var receiver: LyricReceiver

    private var title: String by observableChange("") { _, _, newValue ->
        "title: $newValue".log()
        if (config.enhancedHiddenLyrics) {
            eventTools.cleanLyric()
        }
        if (config.showTitle) {
            eventTools.sendLyric(newValue)
        }
    }

    private var useOwnMusicController: Boolean = false

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
        super.init()
        if (Build.VERSION.SDK_INT <= 33) {
//            Android13及其以下适用这个方法
            loadClassOrNull("com.android.systemui.statusbar.NotificationMediaManager").isNotNull {
                it.methodFinder().filterByName("clearCurrentMediaNotification").first().createHook {
                    after {
                        if (!isPlayer || !useOwnMusicController) return@after
                        isPlayer = false
                        eventTools.cleanLyric()
                    }
                }
            }
        } else {
//            Android14 clearCurrentMediaNotification消失了，使用removePlayer代替
            loadClassOrNull("com.android.systemui.media.controls.ui.MediaCarouselController").isNotNull {
                it.methodFinder().filterByName("removePlayer\$default").first().createHook {
                    after {
                        if (!isPlayer || !useOwnMusicController) return@after
                        isPlayer = false
                        eventTools.cleanLyric()
                    }
                }
            }
        }
        for (i in 0..10) {
            val clazz = loadClassOrNull("com.android.systemui.statusbar.NotificationMediaManager$$i")
            if (clazz.isNotNull()) {
                if (clazz!!.hasMethod("onPlaybackStateChanged")) {
                    clazz.methodFinder().filterByName("onPlaybackStateChanged").first().createHook {
                        after { hookParam ->
                            if (!isPlayer || !useOwnMusicController) return@after
                            val playbackState = hookParam.args[0] as PlaybackState
                            if (playbackState.state == 2) {
                                isPlayer = false
                                eventTools.cleanLyric()
                            }
                        }
                    }
                    break
                }
            }
        }
        if (config.enhancedHiddenLyrics || config.showTitle) {
            moduleRes.getString(R.string.enhanced_hidden_lyrics).log()
            for (i in 0..10) {
                val clazz = loadClassOrNull("com.android.systemui.statusbar.NotificationMediaManager$$i")
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


        loadClass("android.media.session.MediaController").methodFinder().filterByParamCount(1).filterByName("unregisterCallback").first().createHook {
            after {
                if (!isPlayer || !useOwnMusicController) return@after
                if (it.args[0]::class.java.name.contains("statusbar")) {
                    isPlayer = false
                    eventTools.cleanLyric()
                }

            }
        }
        loadClass("android.media.session.MediaController").methodFinder().filterByParamCount(1).filterByName("registerCallback").first().createHook {
            after {
                if (!isPlayer || !useOwnMusicController) return@after
                if (it.args[0]::class.java.name.contains("statusbar")) {
                    (it.thisObject as MediaController).registerCallback(object : MediaController.Callback() {
                        override fun onPlaybackStateChanged(state: PlaybackState?) {
                            super.onPlaybackStateChanged(state)
                            if (state != null) {
                                if (state.state == PlaybackState.STATE_PAUSED) {
                                    isPlayer = false
                                    eventTools.cleanLyric()
                                }

                            }
                        }
                    })
                }
            }
        }

        getApplication { application ->
            receiver = LyricReceiver(object : LyricListener() {
                override fun onUpdate(lyricData: LyricData) {
                    isPlayer = true
                    useOwnMusicController = lyricData.extraData.useOwnMusicController
                }

                override fun onStop(lyricData: LyricData) {
                    isPlayer = false
                }
            })
            Tools.registerLyricListener(application, BuildConfig.API_VERSION, receiver)
        }
    }
}
