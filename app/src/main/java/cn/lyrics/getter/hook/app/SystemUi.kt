package cn.lyrics.getter.hook.app

import cn.lyrics.getter.hook.BaseHook

object SystemUi : BaseHook() {

    override val name: String get() = this.javaClass.simpleName
    override fun init() {

    }
}