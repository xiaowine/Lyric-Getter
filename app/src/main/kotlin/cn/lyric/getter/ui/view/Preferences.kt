package cn.lyric.getter.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.annotation.StringRes
import cn.lyric.getter.R
import com.google.android.material.materialswitch.MaterialSwitch

class Preferences(context: Context) {
    private val preferencesView: View =
        LayoutInflater.from(context).inflate(R.layout.items_md3_preferences, null)
    val preferencesButton: MaterialSwitch = preferencesView.findViewById(R.id.switchButton)
    val preferencesTitle: TextView = preferencesView.findViewById(R.id.switchTitle)
    val preferencesSummary: TextView = preferencesView.findViewById(R.id.switchSummary)

    fun getView(): View = preferencesView

    fun setTitle(title: String) {
        preferencesTitle.text = title
    }

    fun setSwitchChecked(isChecked: Boolean) {
        preferencesButton.isChecked = isChecked
    }

    fun setSwitchListener(listener: CompoundButton.OnCheckedChangeListener) {
        preferencesButton.setOnCheckedChangeListener(listener)
    }

    fun setViewClickToggleSwitch() {
        preferencesView.setOnClickListener {
            preferencesButton.isChecked = !preferencesButton.isChecked
        }
    }

    fun setSummary(summary: String?) {
        if (summary.isNullOrBlank()) {
            preferencesSummary.visibility = View.GONE
        } else {
            preferencesSummary.visibility = View.VISIBLE
            preferencesSummary.text = summary
        }
    }

    fun setSummary(@StringRes resId: Int) {
        setSummary(preferencesView.context.getString(resId))
    }
}