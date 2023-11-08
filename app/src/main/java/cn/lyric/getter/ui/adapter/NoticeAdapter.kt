package cn.lyric.getter.ui.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.data.NoticeData
import cn.lyric.getter.databinding.ItemsNoticeBinding
import cn.xiaowine.xkt.AcTool.openURL
import com.github.islamkhsh.CardSliderAdapter

class NoticeAdapter(private val noticeDataList: ArrayList<NoticeData>) : CardSliderAdapter<BaseViewHolder<*>>() {

    override fun getItemCount() = noticeDataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val context = parent.context
        return BaseViewHolder(ItemsNoticeBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun bindVH(holder: BaseViewHolder<*>, position: Int) {
        val binding = holder.viewBinding as ItemsNoticeBinding
        val data = noticeDataList[position]
        binding.apply {
            if (data.apiVersion != BuildConfig.API_VERSION) return
            title.text = Html.fromHtml(data.title, Html.FROM_HTML_MODE_COMPACT)
            subhead.text = Html.fromHtml(data.subhead, Html.FROM_HTML_MODE_COMPACT)
            content.text = Html.fromHtml(data.content, Html.FROM_HTML_MODE_COMPACT)
            if (data.url.isNotEmpty()) {
                linearLayout.setOnClickListener {
                    data.url.openURL()
                }
            }
        }
    }
}