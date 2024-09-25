package cn.lyric.getter.hook.app

import android.content.Intent
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.eventTools
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.EzXHelper.classLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import android.content.pm.PackageInfo
import android.os.Build
import cn.lyric.getter.tool.HookTools.fuckTinker

import cn.xiaowine.xkt.LogTool.log

//修复高版本安卓系统无法获取版本号
fun PackageInfo.getVersionCode() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    longVersionCode.toInt()
} else {
    @Suppress("DEPRECATION")
    versionCode
}

object Kugou : BaseHook() {
    override fun init() {
        super.init()
        fuckTinker()
        HookTools.openBluetoothA2dpOn()
        HookTools.getApplication { app ->
            val verCode:Int = app.packageManager?.getPackageInfo(app.packageName, 0)?.getVersionCode() ?: 0
            when {
                verCode <= 10000 && app.packageName == "com.kugou.android" -> {
                    //"第一个".log()
                    //verCode.log()
                    hookMethodForVersionA()
                }
                verCode <= 12009 && app.packageName == "com.kugou.android" -> {
                    //"第二个".log()
                    //verCode.log()
                    HookTools.MockFlyme().mock()
                    hookLocalBroadcast("android.support.v4.content.LocalBroadcastManager")
                }
                verCode <= 999999 && app.packageName == "com.kugou.android" -> {
                    //"第三个".log()
                    //verCode.log()
                    HookTools.MockFlyme().mock()
                    hookLocalBroadcast("androidx.localbroadcastmanager.content.LocalBroadcastManager")
                }
                //verCode >= 999999 && app.packageName == "com.kugou.android" -> {
                    //"第四个".log()
                    //verCode.log()
                   // hookMethodForVersionA()
               // }
                verCode <= 10648 && app.packageName == "com.kugou.android.lite" -> {
                   // "第五个".log()
                    //verCode.log()
                    hookMethodForVersionA()
                }
                verCode <= 10999 && app.packageName == "com.kugou.android.lite" -> {
                    //"第七个".log()
                    //verCode.log()
                    HookTools.MockFlyme().mock()
                    hookLocalBroadcast("android.support.v4.content.LocalBroadcastManager")
                }
                verCode > 11001 && app.packageName == "com.kugou.android.lite" -> {
                    //"第八个".log()
                    //verCode.log()
                    HookTools.MockFlyme().mock()
                    hookLocalBroadcast("androidx.localbroadcastmanager.content.LocalBroadcastManager")
                }
            }
        }
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
                        message?.let { it3 -> eventTools.sendLyric(it3)
                        }
                    }
                }
            }
    }
}
