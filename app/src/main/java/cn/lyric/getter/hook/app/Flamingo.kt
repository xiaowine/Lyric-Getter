package cn.lyric.getter.hook.app


import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools.isApi

object Flamingo : BaseHook() {

    override fun init() {
        super.init()
        isApi {
            Api.hook()
        }
    }
}
