package cn.lyric.getter.hook.app

import android.annotation.SuppressLint
import android.media.session.PlaybackState
import android.os.SystemClock
import cn.lyric.getter.api.data.ExtraData
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.dexKitBridge
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool
import cn.xiaowine.xkt.Tool.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import java.util.regex.Pattern

object Bodian : BaseHook() {
    data class Lyric(val time: Long, val text: String)

    private var lyricList = mutableListOf<Lyric>()

    private var lyric: String by Tool.observableChange("") { _, _, newValue ->
        HookTools.eventTools.sendLyric(newValue, ExtraData().apply {
            this.delay = delay
        })
    }


    private lateinit var playbackState: PlaybackState

    private var timer: Timer? = null
    private var isRunning = false
    private fun startTimer() {
        if (isRunning) return
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (lyricList.isEmpty()) return
                val currentPosition = ((SystemClock.elapsedRealtime() - playbackState.lastPositionUpdateTime) * playbackState.playbackSpeed + playbackState.position).toLong()
                lyric = lyricList.findLast { it.time < currentPosition }?.text ?: ""
            }
        }, 0, 400)
        isRunning = true
    }

    private fun stopTimer() {
        if (!isRunning) return
        timer?.cancel()
        "stopTimer".log()
        HookTools.eventTools.cleanLyric()
        isRunning = false
    }


    @SuppressLint("SwitchIntDef")
    override fun init() {
        super.init()
        dexKitBridge { dexKitBridge ->
            val clazz = dexKitBridge.findClass {
                matcher {
                    addEqString("<(-?\\d+),(-?\\d+)(?:,-?\\d+)?>")
                }
            }.findMethod {
                matcher {
                    usingStrings = arrayListOf("\\n")
                }
            }.single()
            loadClass(clazz.declaredClassName).methodFinder().filterByName(clazz.name).filterByParamCount(1).filterByParamTypes(String::class.java).first().createHook {
                after {
                    lyricList = mutableListOf()
                    stopTimer()
                    it.args[0].toString().lines().forEach { input ->
                        val cleanedInput = input.replace("<\\d+,-?\\d+>".toRegex(), "")
                        val pattern = Pattern.compile("\\[(\\d{2}:\\d{2}\\.\\d{3})](.*)")
                        val matcher = pattern.matcher(cleanedInput)
                        if (matcher.matches()) {
                            val time = matcher.group(1).ifEmpty { "0" }
                            val text = matcher.group(2).ifEmpty { "" }
                            val t = 28800000 + SimpleDateFormat("mm:ss.SSS", Locale.getDefault()).parse(time)?.time!!
                            lyricList.add(Lyric(t, text))
                        }
                    }
                }
            }
        }
        loadClass("android.media.session.PlaybackState").isNotNull {
            it.constructorFinder().first().createHook {
                after { hookParam ->
                    playbackState = hookParam.thisObject as PlaybackState
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