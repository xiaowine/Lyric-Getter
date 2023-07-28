package cn.lyric.getter.hook.app

import android.app.Activity
import android.content.Intent
import android.view.View
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools.cleanLyric
import cn.lyric.getter.tool.EventTools.sendLyric
import cn.lyric.getter.tool.HookTools.context
import cn.lyric.getter.tool.HookTools.getApplication
import cn.lyric.getter.tool.Tools.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.enums.MatchType

object Toside : BaseHook() {
    init {
        System.loadLibrary("dexkit")
    }

    override val name: String get() = this.javaClass.simpleName

    override fun init() {
        val lyricModuleClass = loadClassOrNull("cn.toside.music.mobile.lyric.LyricModule")
        lyricModuleClass.isNotNull { clazz ->
            getApplication {
                DexKitBridge.create(it.classLoader, false).use { dexKitBridge ->
                    dexKitBridge.isNotNull { bridge ->
                        val result = bridge.findMethodUsingString {
                            usingString = "ACTION_SCREEN_OFF"
                            matchType = MatchType.FULL
                            methodReturnType = "void"
                        }
                        result.forEach { descriptor ->
                            if (descriptor.name == "onReceive") {
                                loadClass(result[0].declaringClassName).methodFinder().filterByName(result[0].name).first().createHook {
                                    before { hookParam ->
                                        val intent = hookParam.args[1] as Intent
                                        if (intent.action == Intent.ACTION_SCREEN_OFF) {
                                            hookParam.result = null
                                        }
                                    }
                                }
                                return@forEach
                            }
                        }
                    }
                }
            }
            clazz.methodFinder().filterByName("pause").first().createHook {
                after {
                    cleanLyric(context)
                }
            }
            val lyricField = clazz.declaredFields.first { it.name == "lyric" }
            val lyricViewField = lyricField.type.declaredFields.first { it.type.superclass == Activity::class.java }
            val lyricMethod = lyricViewField.type.declaredMethods.first { method -> method.parameterCount == 2 && method.parameterTypes[0] == String::class.java && method.parameterTypes[1] == ArrayList::class.java }
            lyricMethod.createHook {
                after {
                    val lyric = it.args[0] as String
                    sendLyric(context, lyric)
                }
            }

            loadClass("android.view.WindowManagerImpl").methodFinder().first { name == "addView" }.createHook {
                after { view ->
                    if (view.args[0]::class.java.name.contains("cn.toside.music.mobile.lyric")) {
                        (view.args[0] as View).visibility = View.GONE
                    }
                }
            }
        }
    }
}