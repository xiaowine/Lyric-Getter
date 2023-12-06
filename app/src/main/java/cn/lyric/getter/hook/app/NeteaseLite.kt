package cn.lyric.getter.hook.app

import android.widget.LinearLayout
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.xiaowine.xkt.Tool
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object NeteaseLite : BaseHook() {
    init {
        System.loadLibrary("dexkit")
    }

    private var nowLyric: String = ""

    private var nextLyric: String by Tool.observableChange("") { _, _, newValue ->
        if (nowLyric.isNotEmpty()) {
            HookTools.eventTools.sendLyric(nowLyric)
        }
        nowLyric = newValue
    }

    override fun init() {
        super.init()
        loadClass("com.netease.cloudmusic.meta.LyricLine").methodFinder().filterByName("getContent").first().createHook {
            after {
                nextLyric = it.result.toString()
            }
        }
        loadClass("android.view.WindowManagerImpl").methodFinder().first { name == "addView" }.createHook {
            after { view ->
                if (view.args[0]::class.java.name.contains("floatlyric")) {
                    (view.args[0] as LinearLayout).removeAllViews()
                }
            }
        }
    }
}
