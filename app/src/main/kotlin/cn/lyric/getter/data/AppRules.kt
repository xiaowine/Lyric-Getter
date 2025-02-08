package cn.lyric.getter.data

import kotlinx.serialization.Serializable

@Serializable
data class AppRules(
    val appRules: List<AppRule>,
    val appRulesVersion: Int,
    val version: Int
)

@Serializable
data class AppRule(
    val packageName: String,
    val name: String,
    val rules: List<Rule>
)

@Serializable
data class Rule(
    val useApi: Boolean,
    val apiVersion: Int,
    val startVersionCode: Int,
    val endVersionCode: Int,
    val excludeVersions: List<Int>,
    val getLyricType: Int,
    val remarks: String
)