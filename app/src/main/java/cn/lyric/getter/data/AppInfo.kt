package cn.lyric.getter.data

import android.graphics.drawable.Drawable

data class AppInfo(val name: String, val packageName: String, val versionCode: Long, val icon: Drawable, val description: String = "", val status: AppStatus)



