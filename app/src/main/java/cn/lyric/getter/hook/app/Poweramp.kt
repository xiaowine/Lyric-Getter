package cn.lyric.getter.hook.app

import android.database.CharArrayBuffer
import android.graphics.Color
import android.view.View
import android.widget.TextView
import cn.lyric.getter.hook.BaseHook
import cn.xiaowine.xkt.LogTool.log
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder


object Poweramp : BaseHook() {

    override fun init() {
        super.init()
        loadClass("com.maxmpz.widget.base.FastTextView").constructorFinder().first().createHook {
            after {
                if (it.thisObject::class.java.simpleName == "LyricsFastTextView") {
                    error("LyricsFastTextView")
                }
            }
        }
//        loadClass("com.maxmpz.widget.player.list.LyricsFastTextView").methodFinder().filterByName("z").first().createHook {
//            after {
//
//                it.args[0].log()
//                val charArrayBuffer: CharArrayBuffer = (it.args[1] as CharArrayBuffer) // your CharArrayBuffer
//                String(charArrayBuffer.data, 0, charArrayBuffer.sizeCopied).log()
//            }
//        }
    }


}