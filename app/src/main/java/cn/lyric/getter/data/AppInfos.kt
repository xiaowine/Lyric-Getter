package cn.lyric.getter.data

import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable

//data class AppInfos(val packageInfo: PackageInfo, val description: String = "", val appRule: AppRule)
data class AppInfos(val appName: String, val appIcon: Drawable, val packageName: String, val versionCode: Int, val appRule: AppRule, val installed: Boolean = true)



