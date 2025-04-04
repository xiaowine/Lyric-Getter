package cn.lyric.getter.hook.app

import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.mediaMetadataCompatLyric

object Kde : BaseHook() {
    override fun init() {
        super.init()
        HookTools.getApplication {
            mediaMetadataCompatLyric(it.classLoader)
        }
    }
}