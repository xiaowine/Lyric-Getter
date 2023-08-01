package cn.lyric.getter.data


fun Int.lyricType(): String {
    return when (this) {
        0 -> "魅族状态栏模式"
        1 -> "车载蓝牙歌词模式"
        2 -> "桌面歌词模式"
        3 -> "强制模式"
        else -> ""
    }
}

data class AppRules(
    val appRules: List<AppRule>,
    val appRulesVersion: Int,
    val version: Int
)

data class AppRule(
    val packageName: String,
    val name: String,
    val rules: List<Rule>
)

data class Rule(
    val useApi: Boolean,
    val apiVersion: Int,
    val startVersionCode: Int,
    val endVersionCode: Int,
    val excludeVersions: List<Int>,
    val getLyricType: Int,
    val remarks: String
)