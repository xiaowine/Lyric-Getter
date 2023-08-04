package cn.lyric.getter.data

import com.google.gson.annotations.SerializedName

data class GithubReleaseApi(val assets: List<Asset>, val body: String, val name: String,@SerializedName("tag_name") val tagName: String)


data class Asset(@SerializedName("browser_download_url") val browserDownloadUrl: String, @SerializedName("content_type") val contentType: String, val name: String)