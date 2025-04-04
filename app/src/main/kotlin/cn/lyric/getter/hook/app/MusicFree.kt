package cn.lyric.getter.hook.app

import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object MusicFree : BaseHook() {

    override fun init() {
        super.init()
        val loadClass = loadClass("fun.upup.musicfree.lyricUtil.LyricView")
        loadClass.methodFinder().filterByName("setText").first().createHook {
            before {
                HookTools.eventTools.sendLyric(it.args[0].toString())
                it.result = null
            }
        }
        loadClass.methodFinder().filterByName("showLyricWindow").first().createHook {
            returnConstant(null)
        }
    }
}