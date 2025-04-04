package cn.lyric.getter.hook.app

import android.media.MediaMetadata
import android.media.session.PlaybackState
import cn.lyric.getter.api.data.ExtraData
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

object SystemUi : BaseHook() {
    var isPlaying: Boolean = false
    var playingApp: String = ""
    private lateinit var receiver: LyricReceiver
    val uidObserveService: UidObserveService by lazy {
        UidObserveService { packageName ->
            "Uid gone: $packageName".log()
            eventTools.cleanLyric(packageName)
            isPlaying = false
        }
    }

    // data class TitleInfo(
    //     val caller: String,
    //     val title: String
    // )
    //
    // private var title: TitleInfo by observableChange(TitleInfo("", "")) { _, _, newValue ->
    //     "title: $newValue".log()
    //     if (config.enhancedHiddenLyrics)
    //         eventTools.cleanLyric(newValue.caller)
    //
    //      if (config.showTitle)
    //          eventTools.sendLyric(newValue.title)
    // }

    private var useOwnMusicController: Boolean = false

    override fun init() {
        super.init()
        getApplication { application ->
            receiver = LyricReceiver(object : LyricListener() {
                override fun onUpdate(lyricData: LyricData) {
                    isPlaying = true
                    useOwnMusicController = lyricData.extraData.useOwnMusicController
                    if (lyricData.extraData.packageName.isNotEmpty()) {
                        playingApp = lyricData.extraData.packageName
                        uidObserveService.registerForPackage(lyricData.extraData.packageName)
                    }
                }

                override fun onStop(lyricData: LyricData) {
                    isPlaying = false
                }
            })
            Tools.registerLyricListener(application, receiver)

            object : MediaSessionObserve(application) {
                override fun onMediaDataChanged(packageName: String, metadata: MediaMetadata) {
                    super.onMediaDataChanged(packageName, metadata)

                    eventTools.sendMediaData(ExtraData().apply {
                        this.packageName = packageName
                        this.mediaMetadata = metadata
                        this.artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown Artist"
                        this.album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: "Unknown Album"
                        this.title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown Title"
                    })
                }

                override fun onStateChanged(packageName: String, state: Int) {
                    super.onStateChanged(packageName, state)
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
                        eventTools.cleanLyric(packageName)
                    }
                }

                override fun onCleared() {
                    super.onCleared()
                    if (!isPlaying || useOwnMusicController) return
                    isPlaying = false
                    eventTools.cleanLyric(playingApp)
                }
            }
        }
    }
}
