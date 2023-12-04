package cn.lyric.getter.hook.app

import android.view.View
import android.widget.TextView
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.xiaowine.xkt.LogTool.log
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import org.luckypray.dexkit.DexKitBridge

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
        HookTools.getApplication {
            System.loadLibrary("dexkit")
            DexKitBridge.create(it.classLoader, true).use { dexKitBridge ->
                dexKitBridge.apply {
                    val clazz = findMethod {
                        matcher {
                            usingNumbers = listOf(300L, 200L, 22025)
                        }
                    }.log()!!
                        .single().declaredClass!!.name
                    val name = findMethod {
                        matcher {
                            declaredClass = clazz
                            returnType = View::class.java.name
                        }
                    }.single().name
                    loadClass(clazz).methodFinder().filterByName(name).first().createHook {
                        after { param ->
                            (param.result as View).visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}