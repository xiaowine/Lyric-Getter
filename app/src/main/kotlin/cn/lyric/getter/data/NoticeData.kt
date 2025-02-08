package cn.lyric.getter.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoticeData(
    val title: String,
    val subhead: String,
    val content: String,
    val url: String = "",
    @SerialName("api_version") val apiVersion: Int = 0,
)
