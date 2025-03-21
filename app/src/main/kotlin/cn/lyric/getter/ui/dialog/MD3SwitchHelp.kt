package cn.lyric.getter.ui.dialog

import android.content.Context
import android.view.View
import android.widget.TextView
import cn.lyric.getter.R
import android.view.LayoutInflater
import android.widget.CompoundButton
import androidx.annotation.StringRes
import com.google.android.material.materialswitch.MaterialSwitch


class MD3SwitchHelp(context: Context) {
    private val dialogView: View = LayoutInflater.from(context).inflate(R.layout.dialog_switch, null)
    val switchButton: MaterialSwitch = dialogView.findViewById(R.id.switchButton)
    val switchTitle: TextView = dialogView.findViewById(R.id.switchTitle)
    val switchtips: TextView = dialogView.findViewById(R.id.switchtips)

    fun setTitle(title: String) {
        switchTitle.text = title
    }

    fun setSwitchChecked(isChecked: Boolean) {
        switchButton.isChecked = isChecked
    }

    fun setSwitchListener(listener: CompoundButton.OnCheckedChangeListener) {
        switchButton.setOnCheckedChangeListener(listener)
    }

    fun getView(): View {
        return dialogView
    }

    fun setTips(tips: String?) {
        if (tips.isNullOrBlank()) {
            switchtips.visibility = View.GONE
        } else {
            switchtips.visibility = View.VISIBLE
            switchtips.text = tips
        }
    }

    fun setTips(@StringRes resId: Int) {
        setTips(dialogView.context.getString(resId))
    }


}
