package cn.lyrics.getter.hook.app

import android.content.Context
import cn.lyrics.getter.hook.BaseHook
import cn.lyrics.getter.tool.HookTools
import cn.lyrics.getter.tool.Tools.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object RPlayer : BaseHook() {
    override val name: String get() = this.javaClass.simpleName

    override fun init() {
        loadClassOrNull("com.stub.StubApp").isNotNull {
            it.methodFinder().first { name == "attachBaseContext" }.createHook {
                after { param ->
                    val context = param.args[0] as Context
                    val classLoader = context.classLoader
                    HookTools.isApi(classLoader) {
                        Api.hook()
                    }
                }
            }

        }

    }
}