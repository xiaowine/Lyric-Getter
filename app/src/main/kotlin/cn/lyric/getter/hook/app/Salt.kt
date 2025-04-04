package cn.lyric.getter.hook.app

import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.ConfigTools.xConfig
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.isApi
import cn.xiaowine.xkt.Tool.isNot

object Salt : BaseHook() {

    override fun init() {
        super.init()
        if (xConfig.saltUseFlyme) {
            HookTools.MockFlyme().mock().notificationLyric()
        } else {
            isApi {
                Api.hook()
            }.isNot {
                HookTools.MockFlyme().mock().notificationLyric()
            }
        }
    }
}