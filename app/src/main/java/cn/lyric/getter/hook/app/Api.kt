package cn.lyric.getter.hook.app

import android.content.Context
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools.cleanLyric
import cn.lyric.getter.tool.EventTools.sendLyric
import cn.lyric.getter.tool.HookTools.isApi
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.isNot
import cn.xiaowine.xkt.Tool.isNotNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Api : BaseHook() {

    override fun init() {
        hook()
    }

    fun hook(classLoader: ClassLoader? = null) {
        isApi(classLoader) { clazz ->
            clazz.constructorFinder().first().createHook {
                before { hookParam ->
                    hookParam.thisObject.objectHelper().getObjectOrNullAs<Int>("API_VERSION").isNotNull {
                        if (it == BuildConfig.API_VERSION) {
                            hookParam.thisObject.objectHelper().setObject("hasEnable", true)
                            clazz.methodFinder().filterByParamCount(8).first { name == "sendLyric" }.createHook {
                                after { hookParam ->
                                    sendLyric(hookParam.args[0] as Context, hookParam.args[1] as String, hookParam.args[2] as Boolean, hookParam.args[3] as String, hookParam.args[4] as Boolean, hookParam.args[5] as String, hookParam.args[6] as String, hookParam.args[7] as Int)
                                }
                            }
                            clazz.methodFinder().filterByParamCount(9).first { name == "sendLyric" }.createHook {
                                after { hookParam ->
                                    @Suppress("UNCHECKED_CAST")
                                    sendLyric(hookParam.args[0] as Context, hookParam.args[1] as String, hookParam.args[2] as Boolean, hookParam.args[3] as String, hookParam.args[4] as Boolean, hookParam.args[5] as String, hookParam.args[6] as String, hookParam.args[7] as Int, hookParam.args[8] as HashMap<String, Any>?)
                                }
                            }
                            clazz.methodFinder().first { name == "stopLyric" }.createHook {
                                after { hookParam ->
                                    cleanLyric(hookParam.args[0] as Context)
                                }
                            }
                            return@before
                        }
                    }
                    "The APIs do not match".log()
                }
            }

        }.isNot {
            "Not found Api class".log()
        }
    }
}