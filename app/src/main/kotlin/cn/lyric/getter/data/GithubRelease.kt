package cn.lyric.getter.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubReleaseApi(
    val assets: List<Asset>,
    val body: String,
    val name: String,
    @SerialName("tag_name") val tagName: String
)

@Serializable
data class Asset(
    @SerialName("browser_download_url") val browserDownloadUrl: String,
    @SerialName("content_type") val contentType: String,
    val name: String
)