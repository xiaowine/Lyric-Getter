package cn.lyric.getter.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.data.AppInfo
import cn.lyric.getter.data.lyricType
import cn.lyric.getter.databinding.ItemsAppBinding
import cn.lyric.getter.tool.LogTools.log


class AppAdapter() : RecyclerView.Adapter<BaseViewHolder<*>>() {
    private lateinit var listener: OnItemClickListener
    val data: ArrayList<AppInfo> = ArrayList()

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View)
    }


    fun addData(value: AppInfo, position: Int = itemCount) {
        itemCount.log()
        data.add(position, value)
        notifyItemInserted(position)
        notifyItemChanged(position)
    }


    fun removeData(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position)
    }

    fun removeAllData() {
        data.clear()
        for (i in 0 until data.size) {
            notifyItemRemoved(i)
            notifyItemChanged(i)
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return BaseViewHolder(ItemsAppBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder<*>, position: Int) {
        val binding = viewHolder.viewBinding as ItemsAppBinding;
        if (this::listener.isInitialized) {
            viewHolder.itemView.setOnClickListener {
                listener.onItemClick(position, it)
            }
        }
        binding.run {
            appIcon.background = data[position].icon
            appName.text = data[position].name
            appVersionCode.text = data[position].versionCode.toString()
            appDescription.text = data[position].description
        }
    }

    override fun getItemCount() = data.size
}
