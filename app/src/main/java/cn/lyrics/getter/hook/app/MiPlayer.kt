package cn.lyrics.getter.hook.app

import cn.lyrics.getter.hook.BaseHook
import cn.lyrics.getter.tool.HookTools
import cn.lyrics.getter.tool.HookTools.isQQLite

object MiPlayer : BaseHook() {
    override val name: String
        get() = this.javaClass.simpleName

    override fun init() {
        isQQLite {
            HookTools.QQLite()
        }
    }
}