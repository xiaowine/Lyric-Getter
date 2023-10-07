package cn.lyric.getter.hook.app

import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools.mediaMetadataCompatLyric

object Luna : BaseHook() {

    override fun init() {
        super.init()
        mediaMetadataCompatLyric()
    }
}