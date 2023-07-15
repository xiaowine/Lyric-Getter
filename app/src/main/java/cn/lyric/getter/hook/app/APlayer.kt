package cn.lyric.getter.hook.app

import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools

object APlayer : BaseHook() {
    override val name: String get() = this.javaClass.simpleName

    override fun init() {
        HookTools.MockFlyme().mock().notificationLyric()
    }
}