package cn.lyric.getter.data

import android.content.pm.PackageInfo

data class AppRuleInfo(val packageInfo: PackageInfo, val description: String = "", val appRule: AppRule)



