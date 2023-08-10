package cn.lyric.getter.hook.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools.sendLyric
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.MockFlyme
import cn.lyric.getter.tool.HookTools.fuckTinker
import cn.lyric.getter.tool.HookTools.mediaMetadataCompatLyric
import cn.xiaowine.xkt.Tool.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.github.kyuubiran.ezxhelper.paramTypes
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.enums.MatchType


@SuppressLint("StaticFieldLeak")
object Netease : BaseHook() {

    init {
        System.loadLibrary("dexkit")
    }


    override fun init() {
        MockFlyme().mock()
        fuckTinker()
        HookTools.getApplication {
            val verCode = it.packageManager?.getPackageInfo("com.netease.cloudmusic", 0)?.versionCode ?: 0
            if (verCode >= 8000041) {
                DexKitBridge.create(it.classLoader, false).use { use ->
                    use.isNotNull { bridge ->
                        val result = bridge.findMethodUsingString {
                            usingString = "StatusBarLyricController"
                            matchType = MatchType.FULL
                            methodReturnType = "void"
                            paramTypes(Context::class.java)
                        }
                        result.forEach { res ->
                            IntentFilter()
                            loadClass(res.declaringClassName).methodFinder().filterByParamCount(0).filterByReturnType(String::class.java).first().createHook {
                                after { hookParam ->
                                    sendLyric(it, hookParam.result as String)
                                }
                            }
                        }
                    }
                }
            } else {
                mediaMetadataCompatLyric(it)
            }
        }
    }
}