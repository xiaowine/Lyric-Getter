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
    var playApp: String = ""
    private lateinit var receiver: LyricReceiver
    val uidObserveService: UidObserveService by lazy {
        UidObserveService { packageName ->
            "Uid gone: $packageName".log()
            eventTools.cleanLyric(packageName)
            isPlaying = false
        }
    }

    data class TitleInfo(
        val caller: String,
        val title: String
    )

    private var title: TitleInfo by observableChange(TitleInfo("", "")) { _, _, newValue ->
        "title: $newValue".log()
        if (config.enhancedHiddenLyrics)
            eventTools.cleanLyric(newValue.caller)

        if (config.showTitle)
            eventTools.sendLyric(newValue.title)
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
                        playApp = lyricData.extraData.packageName
                        uidObserveService.registerForPackage(lyricData.extraData.packageName)
                    }
                }

                override fun onStop(lyricData: LyricData) {
                    isPlaying = false
                }
            })
            Tools.registerLyricListener(application, BuildConfig.API_VERSION, receiver)
            object : MediaSessionObserve(application) {
                override fun onTitleChanged(caller: String, title: String) {
                    super.onTitleChanged(caller, title)
                    if (config.enhancedHiddenLyrics || config.showTitle) {
                        moduleRes.getString(R.string.enhanced_hidden_lyrics).log()
                        this@SystemUi.title = TitleInfo(caller, title)
                    }
                }

                override fun onStateChanged(caller: String, state: Int) {
                    super.onStateChanged(caller, state)
                    val stateString = when (state) {
                        PlaybackState.STATE_PLAYING -> "Playing"
                        PlaybackState.STATE_PAUSED -> "Paused"
                        PlaybackState.STATE_STOPPED -> "Stopped"
                        PlaybackState.STATE_BUFFERING -> "Buffering"
                        else -> "Unknown State: $state"
                    }
                    "Playback state: $stateString".log()
                    if (!isPlaying || useOwnMusicController) return
                    if (state == PlaybackState.STATE_PAUSED || state == PlaybackState.STATE_STOPPED) {
                        isPlaying = false
                        eventTools.cleanLyric(caller)
                    }
                }

                override fun onCleared() {
                    super.onCleared()
                    if (!isPlaying || useOwnMusicController) return
                    isPlaying = false
                    eventTools.cleanLyric(playApp)
                }
            }
        }
    }
}
