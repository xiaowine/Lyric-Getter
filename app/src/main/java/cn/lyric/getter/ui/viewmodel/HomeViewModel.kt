package cn.lyric.getter.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class HomeViewModel(private val state: SavedStateHandle) : ViewModel() {
    var scrollY: Int
        get() =  state["scrollY"] ?: 0
        set(value) {
            state["scrollY"] = value
        }

    var expanded: Boolean
        get() = state["expanded"] ?: true
        set(value) {
            state["expanded"] = value
        }

}