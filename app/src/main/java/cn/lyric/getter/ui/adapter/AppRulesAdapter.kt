package cn.lyric.getter.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.R
import cn.lyric.getter.config.ActivityOwnSP
import cn.lyric.getter.data.AppInfos
import cn.lyric.getter.data.AppStatus
import cn.lyric.getter.data.Rule
import cn.lyric.getter.data.lyricType
import cn.lyric.getter.databinding.ItemsAppBinding
import cn.lyric.getter.tool.JsonTools.toJSON


class AppRulesAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {
    lateinit var context: Context
    var dataLists: ArrayList<AppInfos> = ArrayList()
    var expandedList: ArrayList<String> = ArrayList()
    private lateinit var listener: OnItemClickListener


    interface OnItemClickListener {
        fun onItemClick(position: Int, viewBinding: ItemsAppBinding)
    }


    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun addData(value: AppInfos, position: Int = itemCount) {
        notifyItemInserted(position)
        dataLists.add(position, value)
    }


    fun removeData(position: Int) {
        notifyItemRemoved(position)
        dataLists.removeAt(position)
    }

    fun removeAllData() {
        notifyItemRangeRemoved(0, dataLists.size)
        dataLists.clear()
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<*> {
        context = viewGroup.context
        return BaseViewHolder(ItemsAppBinding.inflate(LayoutInflater.from(context), viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder<*>, position: Int) {
        val binding = viewHolder.viewBinding as ItemsAppBinding
        if (this::listener.isInitialized) {
            viewHolder.itemView.setOnClickListener {
                listener.onItemClick(position, binding)
            }
        }
        binding.run {
            val appInfo = dataLists[position]
            val versionCode = appInfo.versionCode
            appIconView.background = appInfo.appIcon
            appNameView.text = appInfo.appName
            appVersionCodeView.text = if (versionCode != 0) versionCode.toString() else ""
            appDescriptionView.apply {
                val rules = appInfo.appRule.rules
                var description = ""
                if (appInfo.installed) {
                    val status = if (rules.size == 1) {
                        arrayListOf(getAppStatus(rules[0], versionCode))
                    } else {
                        rules.map { rule ->
                            getAppStatus(rule, versionCode)
                        }
                    }
                    if (status.size == 1) {
                        description = getAppStatusDescription(status[0], rules[0])
                    } else {
                        if (ActivityOwnSP.config.showAllRules) {
                            status.forEach {
                                description += "${context.getString(R.string.multi_rule).format(status.indexOf(it) + 1, getAppStatusDescription(it, rules[status.indexOf(it)]))}<br>"
                            }
                        } else {
                            val status1 = status.filter { it == AppStatus.API || it == AppStatus.Hook }
                            if (status1.size == 1) {
                                description = getAppStatusDescription(status1[0], rules[status.indexOf(status1[0])])
                            }
                        }
                    }
                } else {
                    description = context.getString(R.string.uninstall_rule).format(appInfo.appRule.rules.size)
                }
                text = Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)
            }
            if (expandedList.contains(appInfo.packageName)) {
                appRulesCardView.visibility = View.VISIBLE
                appRulesTextView.text = dataLists.filter { it.packageName == dataLists[position].packageName }[0].appRule.toJSON(true)
            } else {
                appRulesCardView.visibility = View.GONE
            }
        }
    }

    private fun getAppStatusDescription(status: AppStatus, rule: Rule): String {
        return when (status) {
            AppStatus.API -> "<font color='#388E3C'>${context.getString(R.string.api)}</font>"
            AppStatus.Hook -> "<font color='#388E3C'>${context.getString(R.string.hook).format(rule.getLyricType.lyricType())}</font>"
            AppStatus.LowApi -> "<font color='#F57C00'>${context.getString(R.string.low_api).format(rule.apiVersion, BuildConfig.API_VERSION)}</font>"
            AppStatus.MoreAPI -> "<font color='#F57C00'>${context.getString(R.string.more_api).format(rule.apiVersion, BuildConfig.API_VERSION)}</font>"
            AppStatus.UnKnow -> "<font color='#D32F2F'>${context.getString(R.string.un_know)}</font>"
            AppStatus.NoSupport -> "<font color='#D32F2F'>${context.getString(R.string.no_support)}</font>"
            AppStatus.Exclude -> "<font color='#D32F2F'>${context.getString(R.string.no_support)}</font>"
        }
    }

    private fun getAppStatus(rule: Rule, versionCode: Int): AppStatus {
        return if (rule.excludeVersions.contains(versionCode)) {
            AppStatus.NoSupport
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

    override fun getItemCount() = dataLists.size
}
