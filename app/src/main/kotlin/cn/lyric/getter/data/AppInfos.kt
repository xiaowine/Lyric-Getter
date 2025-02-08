package cn.lyric.getter.data

import android.graphics.drawable.Drawable
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class AppInfos(
    val appName: String,
    @Contextual val appIcon: Drawable,
    val packageName: String,
    val versionCode: Int,
    val appRule: AppRule,
    val installed: Boolean = true
)



