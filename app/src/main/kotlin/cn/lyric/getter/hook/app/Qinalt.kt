package cn.lyric.getter.hook.app

import android.content.Context
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.xiaowine.xkt.Tool.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Qinalt : BaseHook() {

    override fun init() {
        super.init()
        ClassUtils.loadClassOrNull("com.stub.StubApp").isNotNull {
            it.methodFinder().first { name == "attachBaseContext" }.createHook {
                after { param ->
                    val context = param.args[0] as Context
                    val classLoader = context.classLoader
                    HookTools.MockFlyme(classLoader).mock().notificationLyric()
                }
            }
        }
    }
}