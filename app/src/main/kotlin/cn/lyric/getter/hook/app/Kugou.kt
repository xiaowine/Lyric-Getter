package cn.lyric.getter.hook.app

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.eventTools
import cn.lyric.getter.tool.HookTools.fuckTinker
import cn.lyric.getter.tool.Tools.getVersionCode
import cn.xiaowine.xkt.LogTool.log
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.EzXHelper.classLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import android.os.Process

object Kugou : BaseHook() {
    override fun init() {
        super.init()
        fuckTinker()
        HookTools.openBluetoothA2dpOn()
        HookTools.getApplication { app ->
            val verCode: Int = app.packageManager?.getPackageInfo(app.packageName, 0)?.getVersionCode() ?: 0
            when (app.packageName) {
                "com.kugou.android" -> {
                    if (getProcessName(app) == "com.kugou.android") return@getApplication
                    when {
                        verCode <= 10000 -> hookMethodForVersionA()
                        verCode <= 12009 -> {
                            HookTools.MockFlyme().mock()
                            hookLocalBroadcast("android.support.v4.content.LocalBroadcastManager")
                        }

                        else -> {
                            HookTools.MockFlyme().mock()
                            hookLocalBroadcast("androidx.localbroadcastmanager.content.LocalBroadcastManager")
                        }
                    }
                }

                "com.kugou.android.lite" -> {
                    when {
                        verCode <= 10648 -> hookMethodForVersionA()
                        verCode <= 10999 -> {
                            HookTools.MockFlyme().mock()
                            hookLocalBroadcast("android.support.v4.content.LocalBroadcastManager")
                        }

                        else -> {
                            HookTools.MockFlyme().mock()
                            hookLocalBroadcast("androidx.localbroadcastmanager.content.LocalBroadcastManager")
                        }
                    }
                }
            }
        }
    }

    private fun getProcessName(context: Context): String? {
        val pid = Process.myPid()
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid == pid) {
                return process.processName
            }
        }
        return null
    }

    private fun hookMethodForVersionA() {
        loadClass("com.kugou.framework.player.c").methodFinder()
            .filterByParamTypes(HashMap::class.java).first { name == "a" }
            .createHook {
                after {
                    val hashMap = it.args[0] as HashMap<*, *>
                    eventTools.sendLyric(hashMap[0].toString())
                }
            }
    }

    private fun hookLocalBroadcast(className: String) {
        loadClass(className, classLoader).methodFinder()
            .first { name == "sendBroadcast" }
            .createHook {
                before {
                    val intent = it.args[0] as Intent
                    val action = intent.action
                    val message = intent.getStringExtra("lyric")
                    //action.log()
                    if (action == "com.kugou.android.update_meizu_lyric") {
                        message?.let { it3 ->
                            eventTools.sendLyric(it3)
                        }
                    }
                }
            }
    }
}
