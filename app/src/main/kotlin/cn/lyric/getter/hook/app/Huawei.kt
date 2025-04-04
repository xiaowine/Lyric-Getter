package cn.lyric.getter.hook.app

import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools.eventTools
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import java.util.Arrays

object Huawei : BaseHook() {
    override fun init() {
        super.init()
        //// 让程序以为连接了蓝牙
        loadClass("com.android.mediacenter.localmusic.VehicleLyricControl").methodFinder()
            .first { name == "isEnableRefreshShowLyric" }
            .createHook {
                after { param ->
                    val resultIntent = param.result as Boolean
                    //resultIntent.log()
                }
                before {
                    val field = it.thisObject.javaClass.getDeclaredField("mIsBluetoothA2dpConnect")
                    field.isAccessible = true // 确保可以访问私有字段
                    field.setBoolean(it.thisObject, true) // 设置值为 true
                }
            }


        loadClass("com.android.mediacenter.localmusic.MediaSessionController").methodFinder()
            .first { name == "updateLyric" }
            .createHook {
                before {
                    val lyric = it.args
                    val lyricWithoutBrackets = Arrays.toString(lyric).substring(1, Arrays.toString(lyric).length - 1)
                    //lyricWithoutBrackets.log()
                    eventTools.sendLyric(lyricWithoutBrackets)
                }
            }
    }
}
