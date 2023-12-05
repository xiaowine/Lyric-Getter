package cn.lyric.getter.hook.app

import android.view.View
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import org.luckypray.dexkit.DexKitBridge

object NeteaseLite : BaseHook() {
    init {
        System.loadLibrary("dexkit")
    }

    var lyric: String by Tool.observableChange("") { _, _, newValue ->
        HookTools.eventTools.sendLyric(newValue)
    }

    override fun init() {
        super.init()
        loadClass("com.netease.cloudmusic.meta.LyricLine").methodFinder().filterByName("getContent").first().createHook {
            after {
                lyric = it.result.toString()
            }
        }
        HookTools.getApplication { application ->
            DexKitBridge.create(application.classLoader, false).use { dexKitBridge ->
                dexKitBridge.apply {
                    val single = findClass {
                        matcher {
                            addEqString("JwsARRhTXw==")
                        }
                    }.log()!!.single()
                    loadClass(single.name).constructorFinder().first().createHook {
                        after {
                            (it.thisObject as View).visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}
