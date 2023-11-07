package cn.lyric.getter.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cn.lyric.getter.data.NoticeData
import cn.lyric.getter.tool.JsonTools.parseJSON
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.SimpleHttpTool.get
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class HomeViewModel(private val state: SavedStateHandle) : ViewModel() {
    var noticeList: MutableLiveData<ArrayList<NoticeData>> = MutableLiveData()

    var scrollY: Int
        get() = state["scrollY"] ?: 0
        set(value) {
            state["scrollY"] = value
        }

    var expanded: Boolean
        get() = state["expanded"] ?: true
        set(value) {
            state["expanded"] = value
        }
    var appRulesVersionValue: String?
        get() = state["appRulesVersionValue"]
        set(value) {
            state["appRulesVersionValue"] = value
        }
    var buildTimeValue: String?
        get() = state["buildTimeValue"]
        set(value) {
            state["buildTimeValue"] = value
        }

    fun getNotice() {
        Thread {
            "https://xiaowine.github.io/Lyric-Getter/notice_list.json".get(onSuccess = {
                val type = object : TypeToken<ArrayList<NoticeData>>() {}.type
                val list: ArrayList<NoticeData> = Gson().fromJson(it, type)
                noticeList.postValue(list)
            }, onError = {
                it.printStackTrace()
                noticeList.postValue(ArrayList())
            })
        }.start()
    }
}