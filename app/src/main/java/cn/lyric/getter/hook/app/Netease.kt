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
import cn.lyric.getter.tool.Tools.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.github.kyuubiran.ezxhelper.paramTypes
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.enums.MatchType


@SuppressLint("StaticFieldLeak")
object Netease : BaseHook() {

    private lateinit var context: Context

    init {
        System.loadLibrary("dexkit")
    }

    override val name: String get() = this.javaClass.simpleName

    override fun init() {
        MockFlyme()
        fuckTinker()
        HookTools.getApplication {
            context = it
            val verCode = context.packageManager?.getPackageInfo("com.netease.cloudmusic", 0)?.versionCode ?: 0
            if (verCode >= 8000041) {
                DexKitBridge.create(context.classLoader, false).use { use ->
                    use.isNotNull { bridge ->
                        val result = bridge.findMethodUsingString {
                            usingString = "StatusBarLyricController"
                            matchType = MatchType.FULL
                            methodReturnType = "void"
                            paramTypes(Context::class.java)
                        }
                        Log.i(result.size.toString())
                        result.forEach { res ->
                            IntentFilter()
                            loadClass(res.declaringClassName).methodFinder().filterByParamCount(0).filterByReturnType(String::class.java).first().createHook {
                                after { hookParam ->
                                    sendLyric(context, hookParam.result as String, context.packageName)
                                }
                            }
                        }
                    }
                }
            } else {
                mediaMetadataCompatLyric(context = context)
            }
        }
    }
}