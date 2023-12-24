package cn.lyric.getter.hook.app

import cn.lyric.getter.hook.BaseHook
import cn.xiaowine.xkt.LogTool.log
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Spotify : BaseHook() {

    data class Lyric(val time: Long, val text: String)

    private var lyricList = mutableListOf<Lyric>()
    override fun init() {
        super.init()

        loadClass("com.spotify.lyrics.data.model.Lyrics\$Line").constructorFinder().first().createHook {
            after {
//                Lyric(it.args[1] as Long, it.args[0] as String).log()
            }
        }
        loadClass("com.spotify.share.menuimpl.domain.FormatResult\$Loaded").constructorFinder().first().createHook {
            after {
                it.thisObject.log()            }
        }

        loadClass("p.haf").methodFinder().filterByName("d").first().createHook {
            before {
//                it.args[0].log()
//                if (it.args[0] == 2) {
//                it.result = null
//                "onFinishInflate".log()
//                }
            }

        }
//        loadClass("com.spotify.lyrics.lyricswidget.view.LyricsWidgetView").methodFinder().filterByName("G").first().createHook {
//            before() {
//                it.result = null
//                "onFinishInflate".log()
////                ViewGroup
//            }
//
//        }
    }
}