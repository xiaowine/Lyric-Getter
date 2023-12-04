package cn.lyric.getter.hook.app

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.enums.MatchType

object Luna : BaseHook() {

    override fun init() {
        super.init()
        TextView::class.java.methodFinder().filterByName("setText").first().createHook {
            after {
                if (it.thisObject::class.java.simpleName == "MarqueeLastLineLyricTextView") {
                    HookTools.eventTools.sendLyric(it.args[0].toString())
                }
            }
        }
        HookTools.getApplication {
            System.loadLibrary("dexkit")
            DexKitBridge.create(it.classLoader, true).use { dexKitBridge ->
                dexKitBridge.isNotNull { bridge ->
                    val classDescriptors = bridge.findMethodUsingString {
                        usingString = "floating_lyrics"
                        matchType = MatchType.FULL
                    }
                    val s = classDescriptors[0].declaringClassName.split("\$")[0]
                    val b = bridge.findMethod {
                        methodDeclareClass = s
                        methodReturnType = View::class.java.name
                    }
                    loadClass(s).methodFinder().filterByName(b[0].name).first().createHook {
                        after { param ->
                            (param.result as View).visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}