package cn.lyric.getter.hook.app

import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools.eventTools
import cn.lyric.getter.tool.HookTools.extractValues
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder

object Poweramp : BaseHook() {

    override fun init() {
        super.init()
        val clazz = loadClass("com.maxmpz.widget.player.list.LyricsFastTextView")
        val method = MethodFinder.fromClass(clazz)
            .filter {
                val params = parameterTypes
                params.size >= 4 && params[1] == Boolean::class.java && params[2] == Int::class.java && params[3] == Int::class.java
            }
            .single()
        method.createHook {
            before {
                //"返回 ${it.result}".log()
                //"l0传入 ${Arrays.toString(it.args)}".log()
                val xc = it.args[0] // `XC` 参数，包含歌词信息
                val c = it.args[2] //判断歌词是否为现在的
                val a = xc.toString()
                val b = extractValues(a, "text")
                if (!b.isNullOrEmpty()) {
                    if (b != "null") {
                        if (c != 0) {
                            eventTools.sendLyric(b)
                        }
                    } else {
                        eventTools.cleanLyric()
                    }

                }
            }
        }
    }
}

