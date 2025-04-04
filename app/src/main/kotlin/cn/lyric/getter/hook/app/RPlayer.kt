package cn.lyric.getter.hook.app

import android.content.Context
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.xiaowine.xkt.Tool.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import cn.lyric.getter.tool.ConfigTools.xConfig as config

object RPlayer : BaseHook() {

    override fun init() {
        super.init()
        loadClassOrNull("com.stub.StubApp").isNotNull {
            it.methodFinder().first { name == "attachBaseContext" }.createHook {
                after { param ->
                    val context = param.args[0] as Context
                    val classLoader = context.classLoader
                    HookTools.mediaMetadataCompatLyric(classLoader)
                    if (config.allowSomeSoftwareToOutputAfterTheScreen) HookTools.lockNotStopLyric(classLoader)
                }
            }
        }
    }
}