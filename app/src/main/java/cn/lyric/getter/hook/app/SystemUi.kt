package cn.lyric.getter.hook.app


import android.util.Log
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools
import cn.lyric.getter.tool.EventTools.TAG
import cn.lyric.getter.tool.HookTools.context
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object SystemUi : BaseHook() {

    private var lastStats: Boolean = false
    override val name: String get() = this.javaClass.simpleName
    override fun init() {
        loadClass("com.android.systemui.media.MediaCarouselController").methodFinder().first { name == "removePlayer" }.createHook {
            after {
                Log.d(TAG, "removePlayer")
                EventTools.cleanLyric(context)
            }
        }
        loadClass("com.android.systemui.media.MediaData").constructorFinder().first().createHook {
            after {
                it.thisObject.objectHelper {
                    val stats = getObjectOrNullAs<Boolean>("isPlaying") ?: false
                    Log.d(TAG, "isPlaying: $stats")
                    if (lastStats != stats) {
                        lastStats = stats
                        if (!stats) {
                            EventTools.cleanLyric(context)
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