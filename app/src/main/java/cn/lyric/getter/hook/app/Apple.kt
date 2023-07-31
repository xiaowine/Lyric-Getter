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
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools
import cn.lyric.getter.tool.HookTools.context
import cn.lyric.getter.tool.HookTools.getApplication
import cn.lyric.getter.tool.Tools.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import io.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Constructor
import java.util.LinkedList
import java.util.Timer
import java.util.TimerTask


object Apple : BaseHook() {  init {
    System.loadLibrary("dexkit")
}

    override val name: String get() = this.javaClass.simpleName

    private lateinit var lyricConvertConstructor: Data

    private lateinit var lyricReqConstructor: Constructor<*>

    private lateinit var playbackState: PlaybackState

    data class LyricsLine(val start: Int, val end: Int, val lyric: String)

    data class Data(val clazz: Class<*>, val methodName: String)

    private val lyricList = LinkedList<LyricsLine>()

    private var oldLyric: String = ""
    private var oldTitle: String = ""

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
                    val lyric = it.lyric
                    if (oldLyric == lyric) return@isNotNull
                    oldLyric = lyric
                    EventTools.sendLyric(context, it.lyric, (it.end - it.start) / 1000)
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

        getApplication {
            DexKitBridge.create(it.classLoader, false).use { dexKitBridge ->
                dexKitBridge.isNotNull { bridge ->
                    val result = bridge.findMethodCaller {
                        methodReturnType = "LyricsSection\$LyricsSectionNative"
                        methodDeclareClass = "com.apple.android.music.ttml.javanative.model.LyricsSection\$LyricsSectionPtr"
                    }
                    result.forEach { (key, _) ->
                        if (!key.declaringClassName.contains("apple") && key.isMethod) {
                            if (key.returnTypeSig == "Lcom/apple/android/music/ttml/javanative/model/LyricsLine\$LyricsLinePtr;") {
                                lyricConvertConstructor = Data(loadClass(key.declaringClassName), key.name)
                                return@forEach
                            }
                        }
                    }
                }
            }
            DexKitBridge.create(it.classLoader, false).use { dexKitBridge ->
                dexKitBridge.isNotNull { bridge ->
                    val result = bridge.findMethodCaller {
                        methodName = "get"
                        methodDeclareClass = "com.apple.android.music.ttml.javanative.model.SongInfo\$SongInfoPtr"
                    }
                    result.forEach { (key, _) ->
                        if (!key.declaringClassName.contains("apple") && key.isMethod && key.name == "call") {
                            val callBackClass = loadClass(key.declaringClassName)
                            lyricReqConstructor = callBackClass.enclosingClass.getConstructor(Context::class.java, Long::class.javaPrimitiveType, Long::class.javaPrimitiveType, Long::class.javaPrimitiveType, loadClass("com.apple.android.mediaservices.javanative.common.StringVector\$StringVectorNative"), Boolean::class.javaPrimitiveType)
                            return@forEach
                        }
                    }
                }
            }
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
