package cn.lyric.getter.hook.app

import android.app.Activity
import android.view.View
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.context
import cn.lyric.getter.tool.HookTools.eventTools
import cn.lyric.getter.tool.HookTools.getApplication
import cn.xiaowine.xkt.Tool.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Toside : BaseHook() {

    override fun init() {
        super.init()
        val lyricModuleClass = loadClassOrNull("cn.toside.music.mobile.lyric.LyricModule")
        lyricModuleClass.isNotNull { clazz ->
            if (config.allowSomeSoftwareToOutputAfterTheScreen) {
                getApplication {
                    HookTools.lockNotStopLyric(it.classLoader, arrayListOf("MusicModule"))
                }
            }
            clazz.methodFinder().filterByName("pause").first().createHook {
                after {
                    eventTools.cleanLyric()
                }
            }
            val lyricField = clazz.declaredFields.first { it.name == "lyric" }
            val lyricViewField = lyricField.type.declaredFields.first { it.type.superclass == Activity::class.java }
            val lyricMethod = lyricViewField.type.declaredMethods.first { method -> method.parameterCount == 2 && method.parameterTypes[0] == String::class.java && method.parameterTypes[1] == ArrayList::class.java }
            lyricMethod.createHook {
                after {
                    val lyric = it.args[0] as String
                    eventTools.sendLyric(lyric)
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