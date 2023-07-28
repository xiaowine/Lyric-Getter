package cn.lyric.getter.tool

import android.app.AndroidAppHelper
import android.app.Application
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Build
import cn.lyric.getter.tool.EventTools.cleanLyric
import cn.lyric.getter.tool.EventTools.sendLyric
import cn.lyric.getter.tool.LogTools.log
import cn.lyric.getter.tool.Tools.isNotNull
import cn.lyric.getter.tool.Tools.isNull
import com.github.kyuubiran.ezxhelper.ClassLoaderProvider
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.ClassUtils.setStaticObject
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.enums.MatchType
import java.lang.reflect.Method


object HookTools {
    val context: Application by lazy { AndroidAppHelper.currentApplication() }
    fun isQQLite(classLoader: ClassLoader? = null, callback: () -> Unit): Boolean {
        loadClassOrNull("com.tencent.qqmusic.core.song.SongInfo", classLoader).isNotNull {
            callback()
            return true
        }
        return false
    }

    fun isApi(classLoader: ClassLoader? = null, callback: (Class<*>) -> Unit): Boolean {
        loadClassOrNull("cn.lyric.getter.api.tools.EventTools", classLoader).isNotNull {
            callback(it)
            return true
        }
        return false
    }

    fun mediaMetadataCompatLyric(context: Context? = null, classLoader: ClassLoader? = null) {
        loadClassOrNull("android.support.v4.media.MediaMetadataCompat\$Builder", classLoader).isNotNull {
            it.methodFinder().first { name == "putString" }.createHook {
                after { hookParam ->
                    if (hookParam.args[0].toString() == "android.media.metadata.TITLE") {
                        hookParam.args[1].isNotNull {
                            sendLyric(context ?: this@HookTools.context, hookParam.args[1] as String)
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

    fun lockNotStopLyric(classLoader: ClassLoader, fileFilter: ArrayList<String>? = null) {
        DexKitBridge.create(classLoader, true).use { dexKitBridge ->
            dexKitBridge.isNotNull { bridge ->
                val result = bridge.findMethodUsingString {
                    usingString = "android.intent.action.SCREEN_OFF"
                    matchType = MatchType.FULL
                    methodReturnType = "void"
                    methodName = "onReceive"
                }
                result.forEach { descriptor ->
                    val className = descriptor.declaringClassName
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
                    val resultCode = resultIntent.getIntExtra("intent_return_code", -114514)
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
                    if (notification.flags == 0) {
                        val isLyric = notification.flags and MeiZuNotification.FLAG_ALWAYS_SHOW_TICKER != 0 || notification.flags and MeiZuNotification.FLAG_ONLY_UPDATE_TICKER != 0
                        if (charSequence == null || !isLyric) {
                            cleanLyric(context)
                        }
                    } else {
                        it.thisObject.objectHelper {
                            sendLyric(context, charSequence.toString())
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
            loadClass("com.tencent.qqmusiccommon.util.music.RemoteLyricController", classLoader).methodFinder().first { name == "BluetoothA2DPConnected" }.createHook { returnConstant(true) }

            val remoteControlManager = loadClass("com.tencent.qqmusiccommon.util.music.RemoteControlManager", classLoader)

            remoteControlManager.methodFinder().first { name == "updataMetaData" }.createHook {
                before {
                    val lyric = if (it.args[1].isNull()) return@before else it.args[1].toString()
                    sendLyric(context, lyric)
                }
            }
        }
    }

}