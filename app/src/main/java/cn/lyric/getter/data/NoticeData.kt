package cn.lyric.getter.data

import com.google.gson.annotations.SerializedName

data class NoticeData(
    val title: String,
    val subhead: String,
    val content: String,
    val url: String = "",
    @SerializedName("api_version")
    val apiVersion: Int = 0,
)
