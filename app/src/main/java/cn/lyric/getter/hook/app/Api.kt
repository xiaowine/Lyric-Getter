package cn.lyric.getter.hook.app

import android.content.Context
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools.cleanLyric
import cn.lyric.getter.tool.EventTools.sendLyric
import cn.lyric.getter.tool.HookTools.isApi
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Api : BaseHook() {
    override val name: String get() = this.javaClass.simpleName

    override fun init() {
        hook()
    }

    fun hook(classLoader: ClassLoader? = null) {
        isApi(classLoader) {
            it.methodFinder().first { name == "hasEnable" }.createHook { after { returnConstant(true) } }
            it.methodFinder().filterByParamCount(7).first { name == "sendLyric" }.createHook {
                after { hookParam ->
                    sendLyric(hookParam.args[0] as Context, hookParam.args[1] as String, hookParam.args[2] as Boolean, hookParam.args[3] as String, hookParam.args[4] as Boolean, hookParam.args[5] as String, hookParam.args[6] as String)
                }
            }
            it.methodFinder().first { name == "stopLyric" }.createHook {
                after {
                    cleanLyric(it.args[0] as Context)
                }
            }
        }
    }
}