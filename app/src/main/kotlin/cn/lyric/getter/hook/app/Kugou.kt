package cn.lyric.getter.hook.app

import android.content.Intent
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.eventTools
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.EzXHelper.classLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Kugou : BaseHook() {

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
                if (verCode < 12000) {
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
                //后面换库了
                if (verCode < 12999) {
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
                //// 不知道12之后哪些版本因为腾讯fuckTinker会导致异常，先留着
                if (verCode >= 13000) {loadClass("com.kugou.framework.player.c").methodFinder()
                    .filterByParamTypes(HashMap::class.java).first { name == "a" }.createHook {
                        after {
                            val hashMap = it.args[0] as HashMap<*, *>
                            eventTools.sendLyric(hashMap[0].toString())
                        }
                    }

                }
            }
        }
    }
