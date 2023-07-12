package cn.lyric.getter.hook.app

import android.content.Context
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools.cleanLyric
import cn.lyric.getter.tool.EventTools.sendLyric
import cn.lyric.getter.tool.HookTools.isApi
import cn.lyric.getter.tool.Tools.isNot
import cn.lyric.getter.tool.Tools.isNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Api : BaseHook() {
    override val name: String get() = this.javaClass.simpleName

    override fun init() {
        hook()
    }

    fun hook(classLoader: ClassLoader? = null) {
        isApi(classLoader) { clazz ->
            clazz.constructorFinder().first().createHook {
                before { hookParam ->
                    hookParam.thisObject.objectHelper {
                        val objectOrNullAs = getObjectOrNullAs<Int>("API_VERSION")
                        if (objectOrNullAs.isNull() || objectOrNullAs!! != BuildConfig.API_VERSION) {
                            clazz.methodFinder().first { name == "hasEnable" }.createHook { returnConstant(true) }
                            clazz.methodFinder().filterByParamCount(7).first { name == "sendLyric" }.createHook {
                                after { hookParam ->
                                    sendLyric(hookParam.args[0] as Context, hookParam.args[1] as String, hookParam.args[2] as Boolean, hookParam.args[3] as String, hookParam.args[4] as Boolean, hookParam.args[5] as String, hookParam.args[6] as String)
                                }
                            }
                            clazz.methodFinder().first { name == "stopLyric" }.createHook {
                                after { hookParam ->
                                    cleanLyric(hookParam.args[0] as Context)
                                }
                            }
                        } else {
                            Log.d("The APIs do not match")
                        }
                    }
                }
            }

        }.isNot {
            Log.d("Not found")
        }
    }
}