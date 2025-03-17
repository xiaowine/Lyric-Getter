package cn.lyric.getter.hook.app

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Process
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.eventTools
import cn.lyric.getter.tool.HookTools.fuckTinker
import cn.lyric.getter.tool.HookTools.getProcessName
import cn.lyric.getter.tool.Tools.getVersionCode
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.EzXHelper.classLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import de.robv.android.xposed.XposedHelpers

object Kugou : BaseHook() {
    override fun init() {
        super.init()
        fuckTinker()
        HookTools.openBluetoothA2dpOn()
        HookTools.getApplication { app ->
            val verCode: Int = app.packageManager?.getPackageInfo(app.packageName, 0)?.getVersionCode() ?: 0
            when (app.packageName) {
                "com.kugou.android" -> {
                    if (getProcessName(app) == "com.kugou.android.support"){
                        when{
                            verCode <= 10000 -> hookCarLyric()
                        }
                    }
                    if (getProcessName(app) == "com.kugou.android.support") return@getApplication
                    when {
                        verCode <= 10000 -> HookTools.MockFlyme().mock()
                        verCode <= 12009 -> {
                            HookTools.MockFlyme().mock()
                            hookLocalBroadcast("android.support.v4.content.LocalBroadcastManager")
                        }
                        else -> {
                            HookTools.MockFlyme().mock()
                            hookLocalBroadcast("androidx.localbroadcastmanager.content.LocalBroadcastManager")
                            fixProbabilityCollapse()
                        }
                    }
                }

                "com.kugou.android.lite" -> {
                    if (getProcessName(app) == "com.kugou.android.lite.support") return@getApplication
                    when {
                        verCode <= 10648 -> {
                            HookTools.MockFlyme().mock()
                            hookLocalBroadcast("android.support.v4.content.LocalBroadcastManager")
                            fixProbabilityCollapse()
                        }
                        else -> {
                            HookTools.MockFlyme().mock()
                            hookLocalBroadcast("androidx.localbroadcastmanager.content.LocalBroadcastManager")
                            fixProbabilityCollapse()
                        }
                    }
                }
            }
        }
    }


    private fun hookCarLyric() {
        loadClass("com.kugou.framework.player.c").methodFinder()
            .filterByParamTypes(HashMap::class.java).first { name == "a" }
            .createHook {
                after {
                    val hashMap = it.args[0] as HashMap<*, *>
                    eventTools.sendLyric(hashMap[0].toString())
                }
            }
    }

    // 非常神奇的崩溃点
    private fun fixProbabilityCollapse() {
        loadClass("com.kugou.framework.hack.ServiceFetcherHacker\$FetcherImpl").methodFinder()
            .first { name == "createServiceObject" }
            .createHook {
                after {
                    val mServiceName = XposedHelpers.getObjectField(it.thisObject, "serviceName")
                    if (mServiceName == Context.WIFI_SERVICE && it.throwable != null) { // 当有错误抛出时才使用替代方法，防止软件崩溃。
                        it.throwable = null
                        it.result = null
                    }
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
