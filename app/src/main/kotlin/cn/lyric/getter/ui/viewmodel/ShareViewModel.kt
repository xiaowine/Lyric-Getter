package cn.lyric.getter.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ShareViewModel(private val state: SavedStateHandle) : ViewModel() {
    var activated: Boolean
        get() = state.get<Boolean>("activated") != false
        set(value) {
            state.set("activated", value)
        }

}