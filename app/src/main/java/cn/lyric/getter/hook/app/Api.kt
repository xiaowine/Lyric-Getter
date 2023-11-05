package cn.lyric.getter.hook.app

import cn.lyric.getter.BuildConfig
import cn.lyric.getter.api.data.ExtraData
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools.eventTools
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
        super.init()
        hook()
    }

    fun hook(classLoader: ClassLoader? = null) {
        isApi(classLoader) { clazz ->
            clazz.constructorFinder().first().createHook {
                before { hookParam ->
                    hookParam.thisObject.objectHelper().getObjectOrNullAs<Int>("API_VERSION").isNotNull {
                        if (it == BuildConfig.API_VERSION) {
                            hookParam.thisObject.objectHelper().setObject("hasEnable", true)
                            clazz.methodFinder().first { name == "sendLyric" }.createHook {
                                after { hookParam ->
                                    eventTools.sendLyric(
                                        hookParam.args[0] as String,
                                        hookParam.args[1] as ExtraData
                                    )
                                }
                            }
                            clazz.methodFinder().first { name == "clearLyric" }.createHook {
                                after {
                                    eventTools.cleanLyric()
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