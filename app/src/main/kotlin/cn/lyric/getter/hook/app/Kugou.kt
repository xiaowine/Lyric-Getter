package cn.lyric.getter.hook.app

import cn.lyric.getter.tool.EventTools
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.context
import cn.lyric.getter.tool.HookTools.eventTools
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Kugou : BaseHook() {

    override fun init() {
        super.init()
        HookTools.openBluetoothA2dpOn()
        HookTools.fuckTinker()
        loadClass("com.kugou.framework.player.c").methodFinder().filterByParamTypes(HashMap::class.java).first { name == "a" }.createHook {
            after {
                val hashMap = it.args[0] as HashMap<*, *>
                eventTools.sendLyric(hashMap[0].toString())
            }
        }
    }
}