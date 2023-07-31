package cn.lyric.getter.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.data.AppInfo
import cn.lyric.getter.data.AppStatus
import cn.lyric.getter.databinding.ItemsAppBinding


class AppAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {
    private lateinit var listener: OnItemClickListener
    val dataLists: ArrayList<AppInfo> = ArrayList()

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun addData(value: AppInfo, position: Int = itemCount) {
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
        return BaseViewHolder(ItemsAppBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))
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
            appIcon.background = appInfo.icon
            appName.text = appInfo.name
            appVersionCode.text = appInfo.versionCode.toString()
            appDescription.apply {
                text = appInfo.description
                setTextColor(
                    when (appInfo.status) {
                        AppStatus.API -> Color.GREEN
                        AppStatus.Hook -> Color.GREEN
                        AppStatus.LowApi -> Color.YELLOW
                        AppStatus.MoreAPI -> Color.YELLOW
                        AppStatus.UnKnow -> Color.RED
                        AppStatus.NoSupport -> Color.RED
                    }
                )
            }
        }
    }

    override fun getItemCount() = dataLists.size
}
