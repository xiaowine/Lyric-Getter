package cn.lyric.getter.hook.app

import android.annotation.SuppressLint
import android.media.MediaMetadata
import android.media.session.PlaybackState
import android.os.Message
import android.os.SystemClock
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools
import cn.lyric.getter.tool.HookTools.context
import cn.lyric.getter.tool.Tools.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import java.util.LinkedList
import java.util.Timer
import java.util.TimerTask


object Apple : BaseHook() {
    override val name: String get() = this.javaClass.simpleName

    private lateinit var playbackState: PlaybackState

    data class LyricsLine(val start: Int, val end: Int, val lyric: String)

    private val lyricList = LinkedList<LyricsLine>()

    private var oldLyric: String = ""
    private var oldTitle: String = ""

    private var timer: Timer? = null
    private var isRunning = false

    private lateinit var lyricsLineNative: Any
    private fun startTimer() {
        if (isRunning) return
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (lyricList.isEmpty()) return
                val currentPosition = ((SystemClock.elapsedRealtime() - playbackState.lastPositionUpdateTime) * playbackState.playbackSpeed + playbackState.position).toLong()
                lyricList.firstOrNull { it.start <= currentPosition && it.end >= currentPosition }.isNotNull {
                    val lyric = it.lyric
                    if (oldLyric == lyric) return@isNotNull
                    oldLyric = lyric
                    EventTools.sendLyric(context, it.lyric, context.packageName)
                }
            }
        }, 0, 400)
        isRunning = true
    }

    private fun stopTimer() {
        if (!isRunning) return
        timer?.cancel()
        EventTools.cleanLyric(context)
        isRunning = false
    }


    @SuppressLint("SwitchIntDef")
    override fun init() {
        loadClassOrNull("com.apple.android.music.ttml.javanative.model.LyricsLine\$LyricsLineNative").isNotNull {
            it.constructorFinder().first().createHook {
                after { hookParam ->
                    lyricsLineNative = hookParam.thisObject
                }
            }
            it.methodFinder().filterByName("getHtmlLineText").first().createHook {
                after { hookParam ->
                    val start = hookParam.thisObject.objectHelper().invokeMethodBestMatch("getBegin") as Int
                    val end = hookParam.thisObject.objectHelper().invokeMethodBestMatch("getEnd") as Int
                    val lyric = hookParam.result as String
                    if (lyricList.isEmpty()) {
                        lyricList.add(LyricsLine(start, end, lyric))
                    } else {
                        if (lyricList.last().end < end) {
                            lyricList.add(LyricsLine(start, end, lyric))
                        }
                    }
                }
            }
        }

        loadClassOrNull("com.apple.android.music.player.viewmodel.PlayerLyricsViewModel").isNotNull {
            it.methodFinder().filterByName("buildTimeRangeToLyricsMap").first().createHook {
                after {
                    if (this@Apple::lyricsLineNative.isInitialized) {
                        lyricsLineNative.objectHelper().invokeMethodBestMatch("getHtmlLineText")
                    }
                }
            }
        }

        loadClassOrNull("android.media.session.PlaybackState").isNotNull {
            it.constructorFinder().first().createHook {
                after { hookParam ->
                    playbackState = hookParam.thisObject as PlaybackState
                }
            }
        }

        loadClassOrNull("android.support.v4.media.MediaMetadataCompat").isNotNull {
            it.methodFinder().filterByName("a").first().createHook {
                after { hookParam ->
                    val mediaMetadata = hookParam.args[0] as MediaMetadata
                    val title = mediaMetadata.getString(MediaMetadata.METADATA_KEY_TITLE)
                    if (oldTitle == title) return@after
                    oldTitle = title
                    lyricList.clear()
                    EventTools.cleanLyric(context)
                }
            }
        }

        loadClassOrNull("android.support.v4.media.session.MediaControllerCompat\$a\$b").isNotNull {
            it.methodFinder().filterByName("handleMessage").first().createHook {
                after { hookParam ->
                    val message = hookParam.args[0] as Message
                    if (message.what == 2 && Apple::playbackState.isInitialized) {
                        val playbackState = playbackState
                        when (playbackState.state) {
                            PlaybackState.STATE_PLAYING -> {
                                startTimer()
                            }

                            PlaybackState.STATE_PAUSED -> {
                                stopTimer()
                            }
                        }
                    }
                }
            }
        }
    }
}
