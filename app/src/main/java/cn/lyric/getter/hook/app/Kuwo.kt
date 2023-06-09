package cn.lyric.getter.hook.app


import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools.sendLyric
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.context
import cn.lyric.getter.tool.Tools.isNot
import cn.lyric.getter.tool.Tools.isNotNull
import com.github.kyuubiran.ezxhelper.ClassLoaderProvider.classLoader
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.enums.MatchType


object Kuwo : BaseHook() {

    init {
        System.loadLibrary("dexkit")
    }

    override val name: String get() = this.javaClass.simpleName

    override fun init() {
        loadClassOrNull("cn.kuwo.mod.playcontrol.RemoteControlLyricMgr").isNotNull {
            it.methodFinder().first { name == "updateLyricText" }.createHook {
                after { param ->
                    Log.d(param.args[0].toString())
                }
            }
        }.isNot {
            DexKitBridge.create(classLoader, false).use {
                it.isNotNull { bridge ->
                    val result = bridge.findMethodUsingString {
                        usingString = "bluetooth_car_lyric"
                        matchType = MatchType.FULL
                        methodReturnType = "void"
                    }
                    result.forEach { res ->
                        if (!res.declaringClassName.contains("ui") && res.isMethod) {
                            loadClass(res.declaringClassName).methodFinder().first { name == res.name }.createHook {
                                after { hookParam ->
                                    sendLyric(context, hookParam.args[0] as String, context.packageName)
                                }
                            }
                            HookTools.openBluetoothA2dpOn()
                        }

                    }
                }
            }
        }
    }
}