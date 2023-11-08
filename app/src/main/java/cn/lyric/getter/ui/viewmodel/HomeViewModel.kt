package cn.lyric.getter.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.data.NoticeData
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
                val b = list.filter { notice -> notice.apiVersion == BuildConfig.API_VERSION }.sortedByDescending { notice -> notice.apiVersion }.isNotEmpty()
                if (b) {
                    noticeList.postValue(list)
                } else {
                    noticeList.postValue(ArrayList())
                }

            }, onError = {
                it.printStackTrace()
                noticeList.postValue(ArrayList())
            })
        }.start()
    }
}