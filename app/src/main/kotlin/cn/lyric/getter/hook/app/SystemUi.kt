package cn.lyric.getter.hook.app


import android.media.session.PlaybackState
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.R
import cn.lyric.getter.api.data.LyricData
import cn.lyric.getter.api.listener.LyricListener
import cn.lyric.getter.api.listener.LyricReceiver
import cn.lyric.getter.api.tools.Tools
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.observe.MediaSessionObserve
import cn.lyric.getter.observe.UidObserveService
import cn.lyric.getter.tool.HookTools.eventTools
import cn.lyric.getter.tool.HookTools.getApplication
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.observableChange
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
import cn.lyric.getter.tool.ConfigTools.xConfig as config


object SystemUi : BaseHook() {
    var isPlaying: Boolean = false
    private lateinit var receiver: LyricReceiver
    val uidObserveService: UidObserveService by lazy {
        UidObserveService { packageName ->
            "Uid gone: $packageName".log()
            eventTools.cleanLyric(packageName)
            isPlaying = false
        }
    }

    data class TitleInfo (
        val changedApp: String,
        val title: String
    )

    private var title: TitleInfo by observableChange(TitleInfo("","")) { _, _, newValue ->
        "title: $newValue".log()
        if (config.enhancedHiddenLyrics) {
            eventTools.cleanLyric(newValue.changedApp)
        }
        if (config.showTitle) {
            eventTools.sendLyric(newValue.title)
        }
    }

    private var useOwnMusicController: Boolean = false

    override fun init() {
        super.init()
        getApplication { application ->
            receiver = LyricReceiver(object : LyricListener() {
                override fun onUpdate(lyricData: LyricData) {
                    isPlaying = true
                    useOwnMusicController = lyricData.extraData.useOwnMusicController
                    if (lyricData.extraData.packageName.isNotEmpty()) {
                        uidObserveService.registerForPackage(lyricData.extraData.packageName)
                    }
                }

                override fun onStop(lyricData: LyricData) {
                    isPlaying = false
                }
            })
            Tools.registerLyricListener(application, BuildConfig.API_VERSION, receiver)
            object : MediaSessionObserve(application) {
                override fun onTitleChanged(changedApp: String, title: String) {
                    super.onTitleChanged(changedApp, title)
                    if (config.enhancedHiddenLyrics || config.showTitle) {
                        moduleRes.getString(R.string.enhanced_hidden_lyrics).log()
                        this@SystemUi.title = TitleInfo(changedApp, title)
                    }
                }

                override fun onStateChanged(changedApp: String, state: Int) {
                    super.onStateChanged(changedApp, state)
                    val stateString = when (state) {
                        PlaybackState.STATE_PLAYING -> "Playing"
                        PlaybackState.STATE_PAUSED -> "Paused"
                        PlaybackState.STATE_STOPPED -> "Stopped"
                        PlaybackState.STATE_BUFFERING -> "Buffering"
                        else -> "Unknown State"
                    }
                    "Playback state: $stateString".log()
                    if (!isPlaying || useOwnMusicController) return
                    if (state == PlaybackState.STATE_PAUSED) {
                        isPlaying = false
                        eventTools.cleanLyric(changedApp)
                    }
                }

                override fun onCleared() {
                    super.onCleared()
                    if (!isPlaying || useOwnMusicController) return
                    isPlaying = false
                    eventTools.cleanLyric()
                }
            }
        }
    }
}
