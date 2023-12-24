package cn.lyric.getter.hook.app

import android.view.View
import android.widget.TextView
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Luna : BaseHook() {
    private var rightLyric: String = ""
    override fun init() {
        super.init()
        TextView::class.java.methodFinder().filterByName("setText").first().createHook {
            after {
                if (it.thisObject::class.java.simpleName in listOf("LyricTextView", "MarqueeLastLineLyricTextView")) {
                    if (rightLyric.isNotEmpty()) {
                        HookTools.eventTools.sendLyric(rightLyric)
                    }
                    rightLyric = it.args[0].toString()
                }
            }
        }
        HookTools.dexKitBridge {
            val clazz = it.findMethod {
                searchPackages = listOf("com.luna.biz.playing.lyric.floatinglyrics.view")
                matcher {
                    addAnnotation {
                        addEqString("android.view.LayoutInflater")
                    }
                    paramCount = 3
                    returnType = View::class.java.name
                }
            }.single()
            loadClass(clazz.declaredClassName).methodFinder().filterByName(clazz.name).first().createHook {
                after { param ->
                    (param.result as View).visibility = View.GONE
                }
            }
        }
    }
}