package cn.lyric.getter.hook.app

import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools

object Mimicry : BaseHook() {
    override fun init() {
        // super.init()
        HookTools.MockFlyme().notificationLyric()
    }
}