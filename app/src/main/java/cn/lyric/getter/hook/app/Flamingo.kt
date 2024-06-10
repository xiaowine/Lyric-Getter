package cn.lyric.getter.hook.app


import cn.lyric.getter.hook.BaseHook

object Flamingo : BaseHook() {

    override fun init() {
        super.init()
        Api.hook()
    }
}
