package cn.lyric.getter.hook.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.media.MediaMetadata
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log
import cn.lyric.getter.api.data.ExtraData
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.context
import cn.lyric.getter.tool.HookTools.eventTools
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.isNotNull
import cn.xiaowine.xkt.Tool.observableChange
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import java.util.LinkedList
import java.util.Timer
import java.util.TimerTask

object Apple : BaseHook() {
    private lateinit var lyricConvertConstructor: Data

    private lateinit var playbackState: PlaybackState

    data class LyricsLine(val start: Int, val end: Int, val lyric: String)

    data class Data(val clazz: Class<*>, val methodName: String)

    private val lyricList = LinkedList<LyricsLine>()

    private var delay: Int = 0

    private var lyric: String by observableChange("") { _, _, newValue ->
        eventTools.sendLyric(newValue, ExtraData().apply {
            this.delay = delay
        })
    }

    private var title: String by observableChange("") { _, oldValue, newValue ->
        if (oldValue == newValue) return@observableChange
        lyricList.clear()
        eventTools.cleanLyric()
    }


    private var timer: Timer? = null
    private var isRunning = false
    private fun startTimer() {
        if (isRunning) return
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (lyricList.isEmpty()) return
                val currentPosition = ((SystemClock.elapsedRealtime() - playbackState.lastPositionUpdateTime) * playbackState.playbackSpeed + playbackState.position).toLong()
                lyricList.firstOrNull { it.start <= currentPosition && it.end >= currentPosition }.isNotNull {
                    delay = (it.end - it.start) / 1000
                    lyric = it.lyric
                }
            }
        }, 0, 400)
        isRunning = true
    }

    private fun stopTimer() {
        if (!isRunning) return
        timer?.cancel()
        "stopTimer".log()
        eventTools.cleanLyric()
        isRunning = false
    }

    @SuppressLint("SwitchIntDef")
    override fun init() {
        super.init()
        loadClassOrNull("com.apple.android.music.player.viewmodel.PlayerLyricsViewModel").isNotNull {
            it.methodFinder().filterByName("buildTimeRangeToLyricsMap").first().createHook {
                after { hookParam ->
                    hookParam.args[0].isNotNull { any ->
                        val curSongInfo = any.objectHelper().invokeMethodBestMatch("get")!!
                        val lyricsSectionVector = curSongInfo.objectHelper().invokeMethodBestMatch("getSections")
                        if (this@Apple::lyricConvertConstructor.isInitialized) {
                            val curLyricObj = lyricConvertConstructor.clazz.getConstructor(lyricsSectionVector!!::class.java).newInstance(lyricsSectionVector)
                            var i = 1
                            do {
                                var lyricsLinePtr: Any
                                try {
                                    lyricsLinePtr = curLyricObj.objectHelper().invokeMethodBestMatch(lyricConvertConstructor.methodName, null, i)!!
                                } catch (_: NullPointerException) {
                                    break
                                }
                                val lyricsLine = lyricsLinePtr.objectHelper().invokeMethodBestMatch("get")!!
                                val lyric = lyricsLine.objectHelper().invokeMethodBestMatch("getHtmlLineText") as String
                                val start = lyricsLine.objectHelper().invokeMethodBestMatch("getBegin") as Int
                                val end = lyricsLine.objectHelper().invokeMethodBestMatch("getEnd") as Int
                                if (lyricList.isNotEmpty() && lyricList.last().start > start) {
                                    lyricList.clear()
                                }
                                lyricList.add(LyricsLine(start, end, lyric))
                                i += 1
                            } while (true)
                        }
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
                    title = mediaMetadata.getString(MediaMetadata.METADATA_KEY_TITLE)
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
        HookTools.dexKitBridge { dexKitBridge ->
            val result = dexKitBridge.findMethod {
                matcher {
                    returnType = "com.apple.android.music.ttml.javanative.model.LyricsLine\$LyricsLinePtr"
                    paramTypes = listOf("int")
                    usingNumbers(0)
                }
            }.single()
            lyricConvertConstructor = Data(loadClass(result.declaredClassName), result.name)
            val result1 = dexKitBridge.findClass {
                matcher {
                    addEqString("No Internet, Unable to get the SongInfo instance.")
                }
            }.single()
            loadClass(result1.name).getConstructor(Context::class.java, Long::class.javaPrimitiveType, Long::class.javaPrimitiveType, Long::class.javaPrimitiveType, loadClass("com.apple.android.mediaservices.javanative.common.StringVector\$StringVectorNative"), Boolean::class.javaPrimitiveType)

        }
        val playbackItemClass = loadClass("com.apple.android.music.model.PlaybackItem")

        val playerLyricsViewModelClass = loadClass("com.apple.android.music.player.viewmodel.PlayerLyricsViewModel")

        loadClassOrNull("com.apple.android.music.model.BaseContentItem").isNotNull {
            it.methodFinder().filterByName("setId").first().createHook {
                after { hookParam ->
                    val trace = Log.getStackTraceString(Exception())
                    if (playbackItemClass.isInstance(hookParam.thisObject) && trace.contains("getItemAtIndex") && (trace.contains("i7.u.accept") || trace.contains("e3.h.w") || trace.contains("k7.t.accept"))) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            playerLyricsViewModelClass.getConstructor(Application::class.java).newInstance(context).objectHelper().invokeMethodBestMatch("loadLyrics", null, hookParam.thisObject)
                        }, 400)
                    }
                }
            }
        }
    }
}
