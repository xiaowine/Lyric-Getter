package cn.lyric.getter.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.data.AppInfo
import cn.lyric.getter.databinding.ItemsAppBinding


class AppAdapter() : RecyclerView.Adapter<BaseViewHolder<*>>() {
    private lateinit var listener: OnItemClickListener
    private val dataSet: ArrayList<AppInfo> = ArrayList()

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View)
    }


    fun addData(value: AppInfo, position: Int = itemCount) {
        dataSet.add(position, value)
        notifyItemInserted(position)
        notifyItemChanged(position)
    }


    fun removeData(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position)
    }

    fun removeAllData() {
        dataSet.forEach {
            removeData(dataSet.indexOf(it))
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
            appIcon.background = dataSet[position].icon
            appName.text = dataSet[position].name
            appVersionCode.text = dataSet[position].versionCode.toString()
            appDescription.text = dataSet[position].description
        }
    }

    override fun getItemCount() = dataSet.size
}
