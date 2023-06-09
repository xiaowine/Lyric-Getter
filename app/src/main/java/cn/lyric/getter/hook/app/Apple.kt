package cn.lyric.getter.hook.app

import cn.lyric.getter.hook.BaseHook

object Apple : BaseHook() {
    override val name: String get() = this.javaClass.simpleName

    override fun init() {

    }
}