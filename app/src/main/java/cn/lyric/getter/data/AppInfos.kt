package cn.lyric.getter.data

import android.graphics.drawable.Drawable

data class AppInfos(
    val appName: String,
    val appIcon: Drawable,
    val packageName: String,
    val versionCode: Int,
    val appRule: AppRule,
    val installed: Boolean = true
)



