package cn.lyric.getter.hook.app

import android.util.Log
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.isApi
import cn.lyric.getter.tool.Tools.isNot

object Salt : BaseHook() {
    override val name: String get() = this.javaClass.simpleName

    override fun init() {

        isApi {
            Api.hook()
        }.isNot {
            HookTools.MockFlyme().mock().notificationLyric()
        }
    }
}