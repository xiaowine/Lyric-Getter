package cn.lyric.getter.hook.app


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.media.AudioManager
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools.cleanLyric
import cn.lyric.getter.tool.EventTools.sendLyric
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.Tools.isNot
import cn.lyric.getter.tool.Tools.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.enums.MatchType
import java.util.Timer
import java.util.TimerTask


@SuppressLint("StaticFieldLeak")
object Kuwo : BaseHook() {

    init {
        System.loadLibrary("dexkit")
    }

    override val name: String get() = this.javaClass.simpleName

    val audioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    lateinit var context: Context

    private var timer: Timer? = null
    private var isRunning = false
    private fun startTimer() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (!audioManager.isMusicActive) {
                    cleanLyric(context)
                    stopTimer()
                }
            }
        }, 0, 1000)
        isRunning = true
    }

    private fun stopTimer() {
        timer?.cancel()
        isRunning = false
    }


    override fun init() {
        Application::class.java.methodFinder().filterByName("attach").first().createHook {
            after {
                context = it.thisObject as Application
                loadClassOrNull("cn.kuwo.mod.playcontrol.RemoteControlLyricMgr").isNotNull { clazz ->
                    clazz.methodFinder().first { name == "updateLyricText" }.createHook {
                        after { param ->
                            if (!isRunning) startTimer()
                            sendLyric(context, param.args[0].toString(), context.packageName)
                        }
                    }
                }.isNot {
                    DexKitBridge.create(context.classLoader, false).use { dexKitBridge ->
                        dexKitBridge.isNotNull { bridge ->
                            val result = bridge.findMethodUsingString {
                                usingString = "bluetooth_car_lyric"
                                matchType = MatchType.FULL
                                methodReturnType = "void"
                            }
                            result.forEach { res ->
                                if (!res.declaringClassName.contains("ui") && res.isMethod) {
                                    loadClass(res.declaringClassName).methodFinder().first { name == res.name }.createHook {
                                        after { hookParam ->
                                            if (!isRunning) startTimer()
                                            sendLyric(context, hookParam.args[0].toString(), context.packageName)
                                        }
                                    }
                                    HookTools.openBluetoothA2dpOn()
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}