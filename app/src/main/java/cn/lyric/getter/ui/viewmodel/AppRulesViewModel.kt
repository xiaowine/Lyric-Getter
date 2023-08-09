package cn.lyric.getter.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cn.lyric.getter.data.AppInfos

class AppRulesViewModel(private val state: SavedStateHandle) : ViewModel() {

    var expandedList: ArrayList<String>
        get() = state["expandedList"] ?: ArrayList<String>().also { state["expandedList"] = it }
        set(value) {
            state["expandedList"] = value
        }
    var dataLists: ArrayList<AppInfos>
        get() = state["dataLists"] ?: ArrayList<AppInfos>().also { state["dataLists"] = it }
        set(value) {
            state["dataLists"] = value
        }
    var scrollY: Int
        get() = state["scrollY"] ?: 0
        set(value) {
            state["scrollY"] = value
        }
}