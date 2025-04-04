package cn.lyric.getter.tool

import android.app.ActivityManager
import android.app.AndroidAppHelper
import android.app.Application
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.isNot
import cn.xiaowine.xkt.Tool.isNotNull
import cn.xiaowine.xkt.Tool.isNull
import com.github.kyuubiran.ezxhelper.ClassLoaderProvider
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.ClassUtils.setStaticObject
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Method

object HookTools {
    val eventTools by lazy { EventTools(context) }

    val context: Application by lazy { AndroidAppHelper.currentApplication() }

    fun dexKitBridge(classLoader: ClassLoader? = null, block: (DexKitBridge) -> Unit) {
        System.loadLibrary("dexkit")
        if (classLoader.isNull()) {
            getApplication { application ->
                DexKitBridge.create(application.classLoader, false).use {
                    block(it)
                }
            }
        } else {
            DexKitBridge.create(classLoader!!, false).use {
                block(it)
            }
        }
    }

    fun isQQLite(classLoader: ClassLoader? = null, callback: () -> Unit): Boolean {
        loadClassOrNull("com.tencent.qqmusic.core.song.SongInfo", classLoader).isNotNull {
            callback()
            return true
        }
        return false
    }

    fun isApi(classLoader: ClassLoader? = null, callback: (Class<*>) -> Unit): Boolean {
        loadClassOrNull("cn.lyric.getter.api.API", classLoader).isNotNull {
            callback(it)
            return true
        }
        return false
    }

    fun mediaMetadataCompatLyric(classLoader: ClassLoader? = null) {
        loadClassOrNull("android.support.v4.media.MediaMetadataCompat\$Builder", classLoader).isNotNull {
            it.methodFinder().first { name == "putString" }.createHook {
                after { hookParam ->
                    if (hookParam.args[0].toString() == "android.media.metadata.TITLE") {
                        hookParam.args[1].isNotNull {
                            eventTools.sendLyric(hookParam.args[1] as String)
                        }
                    }
                }
            }
        }
    }

    fun openBluetoothA2dpOn(classLoader: ClassLoader? = null) {
        loadClassOrNull("android.media.AudioManager", classLoader).isNotNull {
            it.methodFinder().first { name == "isBluetoothA2dpOn" }.createHook {
                returnConstant(true)
            }
        }
        loadClassOrNull("android.bluetooth.BluetoothAdapter", classLoader).isNotNull {
            it.methodFinder().first { name == "isEnabled" }.createHook {
                returnConstant(true)
            }
        }
    }

    fun getApplication(callback: (Application) -> Unit) {
        var isLoad = false
        Application::class.java.methodFinder().filterByName("attach").first().createHook {
            after {
                if (isLoad) return@after
                isLoad = true
                callback(it.thisObject as Application)
            }
        }
    }

    /** 获取应用进程名
     * @param context 为应用context
     * @return 为进程名*/
    fun getProcessName(context: Context): String? {
        val pid = Process.myPid()
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid == pid) {
                return process.processName
            }
        }
        return null
    }

    fun extractValues(text: String, keyword: String): String? {
        val rmn = text.replace("\n", " ")//剔除换行符
        val regex = Regex("text=(.*?)\\s+scenes=") // 匹配关键字
        return regex.find(rmn)?.groupValues?.get(1)?.trim()
    }

    fun lockNotStopLyric(classLoader: ClassLoader, fileFilter: ArrayList<String>? = null) {
        dexKitBridge(classLoader) { dexKitBridge ->
            dexKitBridge.apply {
                val result = findMethod {
                    matcher {
                        usingStrings(listOf("android.intent.action.SCREEN_OFF"), StringMatchType.Contains, false)
                        returnType = Void::class.java.name
                        name = "onReceive"
                        paramTypes(Context::class.java)
                    }
                }
                result.forEach { descriptor ->
                    val className = descriptor.declaredClassName
                    if (!className.contains("Fragment") && !className.contains("Activity") && fileFilter?.none { className.contains(it) } != false) {
                        "lockNotStopLyric:${className}".log()
                        loadClass(className).methodFinder().filterByName("onReceive").first().createHook {
                            before { hookParam ->
                                val intent = hookParam.args[1] as Intent
                                if (intent.action == Intent.ACTION_SCREEN_OFF) {
                                    hookParam.result = null
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun fuckTinker(classLoader: ClassLoader? = null) {
        loadClassOrNull("com.tencent.tinker.loader.TinkerLoader", classLoader).isNotNull { clazz ->
            clazz.methodFinder().filterByName("tryLoad").first().createHook {
                after { param ->
                    val resultIntent = param.result as Intent
                    val application = param.args[0] as Application
                    val resultCode = resultIntent.getIntExtra("intent_return_code", -2)
                    if (resultCode == 0) {
                        ClassLoaderProvider.classLoader = application.classLoader
                    }
                }
            }
        }
    }


    class MockFlyme(private val classLoader: ClassLoader? = null) {
        fun mock(): MockFlyme {
            loadClass("android.os.SystemProperties", classLoader).methodFinder().first { name == "get" }.createHook {
                after {
                    setStaticObject(Build::class.java, "BRAND", "meizu")
                    setStaticObject(Build::class.java, "MANUFACTURER", "Meizu")
                    setStaticObject(Build::class.java, "DEVICE", "m1892")
                    setStaticObject(Build::class.java, "DISPLAY", "Flyme")
                    setStaticObject(Build::class.java, "PRODUCT", "meizu_16thPlus_CN")
                    setStaticObject(Build::class.java, "MODEL", "meizu 16th Plus")

                }
            }
            Class::class.java.methodFinder().first { name == "getField" }.replaceName()
            Class::class.java.methodFinder().first { name == "getDeclaredField" }.replaceName()
            return this
        }

        fun notificationLyric(): MockFlyme {
            loadClass("android.app.NotificationManager", classLoader).methodFinder().first { name == "notify" }.createHook {
                after {
                    val notification = it.args[1] as Notification
                    val charSequence = notification.tickerText
                    val isLyric = notification.flags and MeiZuNotification.FLAG_ALWAYS_SHOW_TICKER != 0 || notification.flags and MeiZuNotification.FLAG_ONLY_UPDATE_TICKER != 0
                    if (isLyric) {
                        charSequence.isNotNull {
                            eventTools.sendLyric(charSequence.toString())
                        }.isNot {
                            eventTools.cleanLyric()
                        }
                    }
                }
            }
            return this
        }

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
    }

    class QQLite(classLoader: ClassLoader? = null) {
        init {
            loadClass("com.tencent.qqmusiccommon.util.music.RemoteLyricController", classLoader).methodFinder()
                .first { name == "BluetoothA2DPConnected" }
                .createHook {
                    returnConstant(true)
                }

            loadClass("com.tencent.qqmusiccommon.util.music.RemoteControlManager", classLoader).methodFinder()
                .first { name == "updataMetaData" }
                .createHook {
                    before {
                        val lyric = if (it.args[1].isNull()) return@before else it.args[1].toString()
                        if ("NEED_NOT_UPDATE_TITLE" == lyric) return@before
                        eventTools.sendLyric(lyric)
                    }
                }
        }
    }

}
