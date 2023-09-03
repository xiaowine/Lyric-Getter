package cn.lyric.getter.tool

import androidx.annotation.StringRes
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.R
import cn.lyric.getter.data.AppStatus
import cn.lyric.getter.data.Rule
import cn.lyric.getter.tool.ActivityTools.context

object AppRulesTools {

    fun getAppStatusDescription(status: AppStatus, rule: Rule, useColor: Boolean = true): String {
        return when (status) {
            AppStatus.API -> addHtmlColor(R.string.api, "#388E3C", useColor)
            AppStatus.Hook -> addHtmlColor(context.getString(R.string.hook).format(rule.getLyricType.lyricType()), "#388E3C", useColor)
            AppStatus.LowApi -> addHtmlColor(context.getString(R.string.low_api).format(rule.apiVersion, BuildConfig.API_VERSION), "#F57C00", useColor)
            AppStatus.MoreAPI -> addHtmlColor(context.getString(R.string.more_api).format(rule.apiVersion, BuildConfig.API_VERSION), "#F57C00", useColor)
            AppStatus.UnKnow -> addHtmlColor(R.string.un_know, "#D32F2F", useColor)
            AppStatus.NoSupport -> addHtmlColor(R.string.no_support, "#D32F2F", useColor)
            AppStatus.Exclude -> addHtmlColor(R.string.exclude, "#D32F2F", useColor)
        }
    }

    private fun addHtmlColor(text: String, color: String, useColor: Boolean): String = if (useColor) "<font color='$color'>$text</font>" else text
    private fun addHtmlColor(@StringRes resId: Int, color: String, useColor: Boolean): String = if (useColor) "<font color='$color'>${context.getString(resId)}</font>" else context.getString(resId)

    fun getAppStatus(rule: Rule, versionCode: Int): AppStatus {
        return if (rule.excludeVersions.contains(versionCode)) {
            AppStatus.Exclude
        } else {
            if (rule.useApi) {
                if (versionCode in rule.startVersionCode..rule.endVersionCode) {
                    if (rule.apiVersion < BuildConfig.API_VERSION) {
                        AppStatus.LowApi
                    } else if (rule.apiVersion > BuildConfig.API_VERSION) {
                        AppStatus.MoreAPI
                    } else {
                        AppStatus.API
                    }
                } else {
                    AppStatus.NoSupport
                }
            } else {
                if (rule.startVersionCode == 0) {
                    AppStatus.UnKnow
                } else {
                    if (versionCode in rule.startVersionCode..rule.endVersionCode) {
                        AppStatus.Hook
                    } else {
                        AppStatus.NoSupport
                    }
                }
            }
        }

    }

    fun Int.lyricType(): String {
        return when (this) {
            0 -> context.getString(R.string.meizu_status_bar_mode)
            1 -> context.getString(R.string.car_bluetooth_lyrics_mode)
            2 -> context.getString(R.string.desktop_lyrics_mode)
            3 -> context.getString(R.string.enforced_mode)
            else -> ""
        }
    }
}