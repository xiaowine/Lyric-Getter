package cn.lyrics.getter.hook.app

import cn.lyrics.getter.hook.BaseHook
import cn.lyrics.getter.tool.HookTools
import cn.lyrics.getter.tool.HookTools.isQQLite
import cn.lyrics.getter.tool.Tools.isNot

object Meizu : BaseHook() {
    override val name: String
        get() = this.javaClass.simpleName

    override fun init() {
        isQQLite {
            HookTools.QQLite()
        }.isNot {
            HookTools.MockFlyme().notificationLyric()
        }
    }
}