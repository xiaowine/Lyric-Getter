package cn.lyric.getter.hook.app

import android.database.CharArrayBuffer
import android.view.ViewGroup
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.dexKitBridge
import cn.lyric.getter.tool.HookTools.eventTools
import cn.lyric.getter.tool.HookTools.extractValues
import cn.lyric.getter.tool.JsonTools.toJSON
import cn.xiaowine.xkt.LogTool.log
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import de.robv.android.xposed.XposedBridge
import java.util.Arrays


object Poweramp : BaseHook() {

    override fun init() {
        super.init()
        loadClass("com.maxmpz.widget.player.list.LyricsFastTextView").methodFinder().first { name == "l0"}.createHook {
            before {
                //"返回 ${it.result}".log()
                //"l0传入 ${Arrays.toString(it.args)}".log()
                val xc = it.args[0] // `XC` 参数，包含歌词信息
                val a = xc.toString()
                val b = extractValues(a,"text")
                if (!b.isNullOrEmpty()) {
                    eventTools.sendLyric(b)
                }
            }
        }
    }
}

