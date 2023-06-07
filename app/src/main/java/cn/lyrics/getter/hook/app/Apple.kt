package cn.lyrics.getter.hook.app

import cn.lyrics.getter.hook.BaseHook

object Apple : BaseHook() {
    override val name: String get() = this.javaClass.simpleName

    override fun init() {

    }
}