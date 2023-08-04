package cn.lyric.getter.hook.app

import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.isQQLite

object MiPlayer : BaseHook() {

    override fun init() {
        isQQLite {
            HookTools.QQLite()
        }
    }
}