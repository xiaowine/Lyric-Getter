package cn.lyric.getter.hook.app


import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object MusicFree : BaseHook() {

    override fun init() {
        super.init()
        loadClass("fun.upup.musicfree.lyricUtil.LyricView").methodFinder().filterByName("setText").first().createHook {
            after {
                HookTools.eventTools.sendLyric(it.args[0].toString())
            }
        }
    }
}