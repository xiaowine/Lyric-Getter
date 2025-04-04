package cn.lyric.getter.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.R
import cn.lyric.getter.data.AppInfos
import cn.lyric.getter.data.AppStatus
import cn.lyric.getter.databinding.ItemsAppBinding
import cn.lyric.getter.tool.AppRulesTools.getAppStatus
import cn.lyric.getter.tool.AppRulesTools.getAppStatusDescription
import cn.lyric.getter.tool.ConfigTools

class AppRulesAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {
    lateinit var context: Context
    var dataLists: ArrayList<AppInfos> = ArrayList()
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
                        if (ConfigTools.config.showAllRules) {
                            status.forEach {
                                description += "${
                                    context.getString(R.string.multi_rule)
                                        .format(status.indexOf(it) + 1, getAppStatusDescription(it, rules[status.indexOf(it)]))
                                }<br>"
                            }
                        } else {
                            val status1 = status.filter { it == AppStatus.API || it == AppStatus.Hook }
                            description = if (status1.size == 1) {
                                getAppStatusDescription(status1[0], rules[status.indexOf(status1[0])])
                            } else {
                                "<font color='#388E3C'>${context.getString(R.string.multiple_modes_work)}</font>"
                            }
                        }
                    }
                } else {
                    description = context.getString(R.string.uninstall_rule).format(appInfo.appRule.rules.size)
                }
                text = Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)
            }
        }
    }

    override fun getItemCount() = dataLists.size
}
