package cn.lyric.getter.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.R
import cn.lyric.getter.data.AppRuleInfo
import cn.lyric.getter.data.AppStatus
import cn.lyric.getter.data.Rule
import cn.lyric.getter.data.lyricType
import cn.lyric.getter.databinding.ItemsAppBinding


class AppAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {
    lateinit var context: Context

    private lateinit var listener: OnItemClickListener
    val dataLists: ArrayList<AppRuleInfo> = ArrayList()

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun addData(value: AppRuleInfo, position: Int = itemCount) {
        dataLists.add(position, value)
        notifyItemInserted(position)
        notifyItemChanged(position)
    }


    fun removeData(position: Int) {
        dataLists.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position)
    }

    fun removeAllData() {
        dataLists.clear()
        for (i in 0 until dataLists.size) {
            notifyItemRemoved(i)
            notifyItemChanged(i)
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<*> {
        context = viewGroup.context
        return BaseViewHolder(ItemsAppBinding.inflate(LayoutInflater.from(context), viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder<*>, position: Int) {
        val binding = viewHolder.viewBinding as ItemsAppBinding
        if (this::listener.isInitialized) {
            viewHolder.itemView.setOnClickListener {
                listener.onItemClick(position, it)
            }
        }
        binding.run {
            val appInfo = dataLists[position]
            val packageManager = context.packageManager
            val applicationInfo = appInfo.packageInfo.applicationInfo
            val versionCode = appInfo.packageInfo.versionCode
            appIcon.background = applicationInfo.loadIcon(packageManager)
            appName.text = applicationInfo.loadLabel(packageManager).toString()
            appVersionCode.text = versionCode.toString()
            appDescription.apply {
                val rules = appInfo.appRule.rules
                val status = if (rules.size == 1) {
                    arrayListOf(getAppStatus(rules[0], versionCode))
                } else {
                    rules.map { rule ->
                        getAppStatus(rule, versionCode)
                    }
                }

                var description = ""
                if (status.size == 1) {
                    description = getAppStatusDescription(status[0], rules[0])
                } else {
                    status.forEach {
                        description += "规则${status.indexOf(it) + 1}：${getAppStatusDescription(it, rules[status.indexOf(it)])}<br>"
                    }
                }
                text = Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)
            }
        }
    }

    private fun getAppStatusDescription(status: AppStatus, rule: Rule): String {
        return when (status) {
            AppStatus.API -> "<font color='green'>${context.getString(R.string.api)}</font>"
            AppStatus.Hook -> "<font color='green'>${context.getString(R.string.hook).format(rule.getLyricType.lyricType())}</font>"
            AppStatus.LowApi -> "<font color='yellow'>${context.getString(R.string.low_api).format(rule.apiVersion, BuildConfig.API_VERSION)}</font>"
            AppStatus.MoreAPI -> "<font color='yellow'>${context.getString(R.string.more_api).format(rule.apiVersion, BuildConfig.API_VERSION)}</font>"
            AppStatus.UnKnow -> "<font color='red'>${context.getString(R.string.un_know)}</font>"
            AppStatus.NoSupport -> "<font color='red'>${context.getString(R.string.no_support)}</font>"
            AppStatus.Exclude -> "<font color='red'>${context.getString(R.string.no_support)}</font>"
        }
    }

    private fun getAppStatus(rule: Rule, versionCode: Int): AppStatus {
        return if (rule.excludeVersions.contains(versionCode)) {
            AppStatus.NoSupport
        } else {
            if (rule.useApi) {
                if (rule.apiVersion < BuildConfig.API_VERSION) {
                    AppStatus.LowApi
                } else if (rule.apiVersion > BuildConfig.API_VERSION) {
                    AppStatus.MoreAPI
                } else {
                    AppStatus.API
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
