package cn.lyric.getter.hook.app

import android.annotation.SuppressLint
import android.content.Context
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.MockFlyme
import cn.lyric.getter.tool.HookTools.eventTools
import cn.lyric.getter.tool.HookTools.fuckTinker
import cn.lyric.getter.tool.HookTools.mediaMetadataCompatLyric
import cn.xiaowine.xkt.LogTool.log
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.StringMatchType


@SuppressLint("StaticFieldLeak")
object Netease : BaseHook() {
    override fun init() {
        super.init()
        MockFlyme().mock()
        fuckTinker()
        HookTools.getApplication {
            System.loadLibrary("dexkit")
            val verCode = it.packageManager?.getPackageInfo(it.packageName, 0)?.versionCode ?: 0
            verCode.log()
            if (verCode >= 8000041 || it.packageName == "com.hihonor.cloudmusic") {
                DexKitBridge.create(it.classLoader, false).use { use ->
                    use.apply {
                        val result = findMethod {
                            matcher {
                                usingStrings(listOf("StatusBarLyricController"), StringMatchType.Contains, false)
                                returnType = Void::class.java.name
                                paramTypes(Context::class.java)
                            }
                        }
                        result.forEach { res ->
                            loadClass(res.declaredClassName).methodFinder().filterByParamCount(0).filterByReturnType(String::class.java).first().createHook {
                                after { hookParam ->
                                    eventTools.sendLyric(hookParam.result as String)
                                }
                            }
                        }
                    }
                }
            } else {
                mediaMetadataCompatLyric(it.classLoader)
            }
        }
    }
}