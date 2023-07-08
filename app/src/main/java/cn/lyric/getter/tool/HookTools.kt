package cn.lyric.getter.tool

import android.app.AndroidAppHelper
import android.app.Application
import android.app.Notification
import android.content.Context
import android.os.Build
import cn.lyric.getter.tool.EventTools.cleanLyric
import cn.lyric.getter.tool.EventTools.sendLyric
import cn.lyric.getter.tool.Tools.isNotNull
import cn.lyric.getter.tool.Tools.isNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.ClassUtils.setStaticObject
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import java.lang.reflect.Method


object HookTools {
    val context: Application by lazy { AndroidAppHelper.currentApplication() }
    fun isQQLite(classLoader: ClassLoader? = null, callback: () -> Unit): Boolean {
        return loadClassOrNull("com.tencent.qqmusic.core.song.SongInfo", classLoader).isNotNull {
            callback()
        }.isNotNull()
    }

    fun isApi(classLoader: ClassLoader? = null, callback: (Class<*>) -> Unit): Boolean {
        return loadClassOrNull("cn.lyric.getter.api.tools.EventTools", classLoader).isNotNull {
            callback(it)
        }.isNotNull()
    }

    fun mediaMetadataCompatLyric(context: Context? = null, classLoader: ClassLoader? = null) {
        loadClassOrNull("android.support.v4.media.MediaMetadataCompat\$Builder", classLoader).isNotNull {
            it.methodFinder().first { name == "putString" }.createHook {
                after { hookParam ->
                    if (hookParam.args[0].toString() == "android.media.metadata.TITLE") {
                        hookParam.args[1].isNotNull {
                            sendLyric(context ?: this@HookTools.context, hookParam.args[1] as String, (context ?: this@HookTools.context).packageName)
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

    fun fuckTinker(classLoader: ClassLoader? = null) {
        loadClassOrNull("com.tencent.tinker.loader.app.TinkerApplication", classLoader).isNotNull {
            it.methodFinder().first { name == "getTinkerFlags" }.createHook { after { returnConstant(0) } }
        }
        loadClassOrNull("com.tencent.tinker.loader.shareutil.ShareTinkerInternals", classLoader).isNotNull {
            it.methodFinder().first { name == "isTinkerEnabledAll" }.createHook { after { returnConstant(false) } }
        }
    }


    class MockFlyme(private val classLoader: ClassLoader? = null) {

        init {
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

        }

        fun notificationLyric() {
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
                            sendLyric(context, charSequence.toString(), getObjectOrNullAs<Context>("mContext")?.packageName ?: "")
                        }

                    }
                }
            }
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
                    sendLyric(context, lyric, context.packageName)
                }
            }
        }
    }

}