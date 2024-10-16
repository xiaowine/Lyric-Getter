package cn.lyric.getter.hook.app


import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools.eventTools
import cn.xiaowine.xkt.LogTool.log
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import de.robv.android.xposed.XposedHelpers.getObjectField



object Gramophone : BaseHook() {

    override fun init() {
        super.init()
        loadClass("org.akanework.gramophone.ui.components.FullBottomSheet\$LyricAdapter").methodFinder()
            .first { name == "updateHighlight" }.createHook {
                after { param ->
                    val currentFocusPos = getObjectField(param.thisObject, "currentFocusPos") as Int
                    val lyricList = getObjectField(param.thisObject, "lyricList") as List<Any>

                    if (currentFocusPos >= 0 && currentFocusPos < lyricList.size) {
                        val lyric = lyricList[currentFocusPos]
                        val content = getObjectField(lyric, "content") as String
                        //"当前高亮歌词: $content".log()
                        eventTools.sendLyric(content)
                    }
                }
            }
        }
    }



