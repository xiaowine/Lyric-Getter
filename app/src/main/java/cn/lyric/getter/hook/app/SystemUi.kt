package cn.lyric.getter.hook.app


import android.util.Log
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools
import cn.lyric.getter.tool.EventTools.TAG
import cn.lyric.getter.tool.HookTools.context
import cn.lyric.getter.tool.Tools.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object SystemUi : BaseHook() {

    private var lastStats: Boolean = false
    override val name: String get() = this.javaClass.simpleName
    override fun init() {
        loadClassOrNull("com.android.systemui.media.MediaCarouselController").isNotNull {
            it.methodFinder().first { name == "removePlayer" }.createHook {
                after {
                    EventTools.cleanLyric(context)
                }
            }
        }
        loadClassOrNull("com.android.systemui.media.MediaData").isNotNull {
            it.constructorFinder().first().createHook {
                after { hookParam ->
                    hookParam.thisObject.objectHelper {
                        val stats = getObjectOrNullAs<Boolean>("isPlaying") ?: false
                        if (lastStats != stats) {
                            lastStats = stats
                            if (!stats) {
                                EventTools.cleanLyric(context)
                            }
                        }
                    }

                }
            }
        }
//        "com.android.systemui.media.MediaData".findClass().hookAfterAllConstructors {
//            val stats = it.thisObject.callMethodAs<Boolean>("isPlaying")
//            if (lastStats != stats) {
//                lastStats = stats
//                if (!stats) {
//                    offLyric(LogMultiLang.pausePlay)
//                }
//            }
//        }
    }
}