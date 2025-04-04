package cn.lyric.getter.hook.app

import android.os.Build
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.dexKitBridge
import cn.lyric.getter.tool.Tools.getVersionCode
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.setStaticObject
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Oppo : BaseHook() {
    init {
        System.loadLibrary("dexkit")
    }

    override fun init() {

        /** 绕过oppo设备限制 */
        loadClass("android.os.SystemProperties").methodFinder().first { name == "get" }.createHook {
            after {
                setStaticObject(Build::class.java, "BRAND", "oppo")
                setStaticObject(Build::class.java, "MANUFACTURER", "Oppo")
                setStaticObject(Build::class.java, "DISPLAY", "Color")

            }
        }
        HookTools.mediaMetadataCompatLyric()
        HookTools.getApplication { app ->
            /** 版本号获取预留 不同软件包名分离方便未来调整hook规则*/
            val verCode: Int = app.packageManager?.getPackageInfo(app.packageName, 0)?.getVersionCode() ?: 0
            dexKitBridge(app.classLoader) { dexKitBridge ->
                when (app.packageName) {
                    "com.heytap.music" -> {
                        /** 强开蓝牙歌词 */
                        loadClass("com.allsaints.music.player.thirdpart.MediaSessionHelper").methodFinder().first { name == "l" }.createHook {
                            after { params ->
                                params.result = true
                            }
                        }
                    }

                    "com.oppo.music" -> {
                        /** 强开蓝牙歌词 */
                        loadClass("com.allsaints.music.player.thirdpart.MediaSessionHelper").methodFinder().first { name == "l" }.createHook {
                            after { params ->
                                params.result = true
                            }
                        }
                    }
                }
            }
        }
    }
}