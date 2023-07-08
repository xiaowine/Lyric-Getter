package cn.lyric.getter.hook.app


import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.EventTools
import cn.lyric.getter.tool.HookTools.context
import cn.lyric.getter.tool.Tools.isNot
import cn.lyric.getter.tool.Tools.isNotNull
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object SystemUi : BaseHook() {

    private var lastStats: Boolean = false
    override val name: String get() = this.javaClass.simpleName
    private fun removePlayer(it: Class<*>) {
        it.methodFinder().first { name == "removePlayer" }.createHook {
            after {
                EventTools.cleanLyric(context)
            }
        }
    }

    private fun isPlaying(it: Class<*>) {
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

    override fun init() {
        loadClassOrNull("com.android.systemui.media.controls.pipeline.MediaDataFilter").isNotNull {
            it.methodFinder().filterByName("onMediaDataRemoved").first().createHook {
                after {
                    EventTools.cleanLyric(context)
                }
            }
        }
        loadClassOrNull("com.android.systemui.media.MediaCarouselController").isNotNull {
            removePlayer(it)
        }.isNot {
            loadClassOrNull("com.android.systemui.media.controls.ui.MediaCarouselController").isNotNull {
                removePlayer(it)
            }
        }

        loadClassOrNull("com.android.systemui.media.MediaData").isNotNull {
            isPlaying(it)
        }.isNot {
            loadClassOrNull("com.android.systemui.media.controls.models.player.MediaData").isNotNull {
                isPlaying(it)
            }
        }

    }
}