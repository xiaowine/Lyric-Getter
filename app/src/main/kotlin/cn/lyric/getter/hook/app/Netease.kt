package cn.lyric.getter.hook.app

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Build
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.ConfigTools.xConfig
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.dexKitBridge
import cn.lyric.getter.tool.HookTools.eventTools
import cn.lyric.getter.tool.HookTools.mediaMetadataCompatLyric
import cn.lyric.getter.tool.MeiZuNotification
import cn.lyric.getter.tool.Tools.getVersionCode
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.setStaticObject
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.github.kyuubiran.ezxhelper.params
import java.lang.reflect.Method

private fun Method.replaceName() {
    this.createHook {
        after {
            when (it.args[0].toString()) {
                "FLAG_ALWAYS_SHOW_TICKER" -> it.result = MeiZuNotification::class.java.getDeclaredField("FLAG_ALWAYS_SHOW_TICKER_HOOK")
                "FLAG_ONLY_UPDATE_TICKER" -> it.result = MeiZuNotification::class.java.getDeclaredField("FLAG_ONLY_UPDATE_TICKER_HOOK")
            }
        }
    }
}

@SuppressLint("StaticFieldLeak")
object Netease : BaseHook() {
    init {
        System.loadLibrary("dexkit")
    }

    override fun init() {
        super.init()
        loadClass("android.os.SystemProperties").methodFinder().first { name == "get" }.createHook {
            after {
                setStaticObject(Build::class.java, "DISPLAY", "Flyme")

            }
        }
        Class::class.java.methodFinder().first { name == "getField" }.replaceName()
        Class::class.java.methodFinder().first { name == "getDeclaredField" }.replaceName()
        HookTools.getApplication {
            val verCode = it.packageManager?.getPackageInfo(it.packageName, 0)?.getVersionCode() ?: 0
            if (verCode >= 8000041 || it.packageName == "com.hihonor.cloudmusic") {
                dexKitBridge(it.classLoader) { dexKitBridge ->
                    val result = dexKitBridge.findClass {
                        matcher {
                            addEqString("StatusBarLyricController")
                        }
                    }.single()
                    loadClass(result.name).methodFinder().filterByParamCount(0).filterByReturnType(String::class.java).first().createHook {
                        after { hookParam ->
                            eventTools.sendLyric(hookParam.result as String)
                        }
                    }
                    if (xConfig.fuckwyysb163){
                        val sbNetease = dexKitBridge.findClass {
                            matcher {
                                usingStrings("com/netease/cloudmusic/module/lyric/flyme/StatusBarLyricSettingManager.class:setSwitchStatus:(Z)V")
                            }
                        }.single()
                        loadClass(sbNetease.name).methodFinder().filterByParamCount(0).filterByName("a").first().createHook {
                            after { hookParam ->
                                val a = hookParam.result as SharedPreferences
                                a.edit().putBoolean("status_bar_lyric_setting_key",xConfig.fuckwyysb163).commit()
                            }
                        }
                    }
                }
            } else {
                mediaMetadataCompatLyric(it.classLoader)
            }
        }
    }
}
