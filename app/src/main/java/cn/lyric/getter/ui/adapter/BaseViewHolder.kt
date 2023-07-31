package cn.lyric.getter.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


class BaseViewHolder<T : ViewBinding>(var viewBinding: T) : RecyclerView.ViewHolder(viewBinding.root)