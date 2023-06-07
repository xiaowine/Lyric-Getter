package cn.lyrics.getter.hook.app

import cn.lyrics.getter.hook.BaseHook
import cn.lyrics.getter.tool.HookTools.mediaMetadataCompatLyric

object Luna : BaseHook() {
    override val name: String
        get() = this.javaClass.simpleName

    override fun init() {
        mediaMetadataCompatLyric()
    }
}