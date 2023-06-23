package cn.lyric.getter.hook.app

import cn.lyric.getter.tool.EventTools.sendLyric
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.context
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Kugou : BaseHook() {
    override val name: String get() = this.javaClass.simpleName

    override fun init() {
        HookTools.openBluetoothA2dpOn()
        HookTools.fuckTinker()
        loadClass("com.kugou.framework.player.c").methodFinder().filterByParamTypes(HashMap::class.java).first { name == "a" }.createHook {
            after {
                val hashMap = it.args[0] as HashMap<*, *>
                sendLyric(context, hashMap[0].toString(), context.packageName)
            }
        }
    }
}