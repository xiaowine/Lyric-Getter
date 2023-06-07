package cn.lyrics.getter.hook.app

import cn.lyrics.getter.hook.BaseHook
import cn.lyrics.getter.tool.HookTools

object QQMusic : BaseHook() {
    override val name: String get() = this.javaClass.simpleName
    override fun init() {
        HookTools.MockFlyme().notificationLyric()
    }
}