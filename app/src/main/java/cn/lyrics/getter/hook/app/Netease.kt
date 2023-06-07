package cn.lyrics.getter.hook.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import cn.lyrics.getter.tool.EventTools.sendLyric
import cn.lyrics.getter.hook.BaseHook
import cn.lyrics.getter.tool.HookTools.MockFlyme
import cn.lyrics.getter.tool.HookTools.fuckThinker
import cn.lyrics.getter.tool.HookTools.mediaMetadataCompatLyric
import cn.lyrics.getter.tool.Tools.isNotNull
import com.github.kyuubiran.ezxhelper.ClassLoaderProvider
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
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
        fuckThinker()
        Application::class.java.methodFinder().first { name == "attach" }.createHook {
            after {
                context = it.args[0] as Context
                val verCode = context.packageManager?.getPackageInfo("com.netease.cloudmusic", 0)?.versionCode ?: 0
                if (verCode >= 8000041) {
                    DexKitBridge.create(ClassLoaderProvider.classLoader, false).use { use ->
                        use.isNotNull { bridge ->
                            val result = bridge.findMethodUsingString {
                                usingString = "com/netease/cloudmusic/notification/flyme/StatusBarLyricController.class:init:(Landroid/content/Context;)V"
                                matchType = MatchType.FULL
                                methodReturnType = "void"
                                paramTypes(Context::class.java)
                            }
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
}