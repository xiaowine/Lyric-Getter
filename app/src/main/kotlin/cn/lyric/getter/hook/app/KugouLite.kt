package cn.lyric.getter.hook.app

import android.content.Intent
import android.os.Build
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.context
import cn.lyric.getter.tool.HookTools.eventTools
import cn.lyric.getter.tool.MeiZuNotification
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.setStaticObject
import com.github.kyuubiran.ezxhelper.EzXHelper.classLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import java.lang.reflect.Method

object KugouLite : BaseHook() {

    override fun init() {
        super.init()
        HookTools.fuckTinker()
        HookTools.openBluetoothA2dpOn()
        HookTools.getApplication {
            @Suppress("DEPRECATION") val verCode = it.packageManager?.getPackageInfo(it.packageName, 0)?.versionCode ?: 0
            if (verCode < 10648) {
                loadClass("com.kugou.framework.player.c").methodFinder()
                    .filterByParamTypes(HashMap::class.java).first { name == "a" }.createHook {
                        after {
                            val hashMap = it.args[0] as HashMap<*, *>
                            eventTools.sendLyric(hashMap[0].toString())
                        }
                    }
            }
            if (verCode < 11000) {
                HookTools.MockFlyme().mock()
                loadClass("android.support.v4.content.LocalBroadcastManager", classLoader).methodFinder().first { name == "sendBroadcast" }.createHook {
                    before {
                        val intent = it.args[0] as Intent
                        val action = intent.action
                        val message = intent.getStringExtra("lyric")
                        // 处理接收到的广播
                        if (action == "com.kugou.android.update_meizu_lyric") {
                            // 执行你需要的操作
                            message?.let { it1 -> HookTools.eventTools.sendLyric(it1) }
                        }
                    }
                }
            }
            if (verCode >= 11001) {
              //后面换库了
                HookTools.MockFlyme().mock()
                loadClass("androidx.localbroadcastmanager.content.LocalBroadcastManager", classLoader).methodFinder()
                    .first { name == "sendBroadcast" }
                    .createHook {
                        before {
                            val intent = it.args[0] as Intent
                            val action = intent.action
                            val message = intent.getStringExtra("lyric")
                            // 处理接收到的广播
                            if (action == "com.kugou.android.update_meizu_lyric")
                            { // 执行你需要的操作
                                message?.let { it1 -> HookTools.eventTools.sendLyric(it1) }
                                }
                            }
                        }
                }
            }
        }
}



