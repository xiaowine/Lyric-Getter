package cn.lyric.getter.hook.app

import android.app.Activity
import android.view.View
import cn.lyric.getter.tool.EventTools.sendLyric
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools.context
import cn.lyric.getter.tool.HookTools.isApi
import cn.lyric.getter.tool.Tools.isNot
import cn.lyric.getter.tool.Tools.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Toside : BaseHook() {

    override val name: String get() = this.javaClass.simpleName

    override fun init() {
        isApi {
            Api.init()
        }.isNot {
            val lyricModuleClass = loadClassOrNull("cn.toside.music.mobile.lyric.LyricModule")
            lyricModuleClass.isNotNull { clazz ->
                val lyricField = clazz.declaredFields.first { it.name == "lyric" }
                val lyricViewField = lyricField.type.declaredFields.first { it.type.superclass == Activity::class.java }
                val lyricMethod = lyricViewField.type.declaredMethods.first { method -> method.parameterCount == 2 && method.parameterTypes[0] == String::class.java && method.parameterTypes[1] == ArrayList::class.java }
                lyricMethod.createHook {
                    after {
                        val lyric = it.args[0] as String
                        sendLyric(context, lyric, "cn.toside.music.mobile")
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
}