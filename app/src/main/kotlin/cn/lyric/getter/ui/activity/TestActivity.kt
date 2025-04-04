package cn.lyric.getter.ui.activity

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import cn.lyric.getter.R
import cn.lyric.getter.api.API
import cn.lyric.getter.api.data.LyricData
import cn.lyric.getter.api.listener.LyricListener
import cn.lyric.getter.api.listener.LyricReceiver
import cn.lyric.getter.api.tools.Tools.registerLyricListener
import cn.lyric.getter.api.tools.Tools.unregisterLyricListener
import cn.lyric.getter.tool.EventTools
import cn.lyric.getter.tool.Tools
import cn.lyric.getter.ui.view.Preferences
import com.google.android.material.appbar.MaterialToolbar

class TestActivity : AppCompatActivity() {
    private lateinit var receiver: LyricReceiver
    private var mi: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val testView = findViewById<LinearLayout>(R.id.test_linearlayout)

        val testAppName = findViewById<TextView>(R.id.test_app_name_text)
        val testAppIcon = findViewById<TextView>(R.id.test_app_icon_text)
        val testAppCustomIcon = findViewById<TextView>(R.id.test_app_customIcon_text)
        val testAppPlay = findViewById<TextView>(R.id.test_app_play_text)
        val testAppLyric = findViewById<TextView>(R.id.test_app_lyric_text)
        testAppLyric.setSelected(true)
        val testAppDelay = findViewById<TextView>(R.id.test_app_delay_text)

        receiver = LyricReceiver(object : LyricListener() {
            @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
            override fun onUpdate(lyricData: LyricData) {
                updateTestLyricViews(
                    lyricData, testAppLyric, testAppIcon, testAppCustomIcon,
                    testAppPlay, testAppName, testAppDelay
                )
                if (mi) Tools.sendNotification(lyricData.lyric, lyricData.extraData.packageName, this@TestActivity)
            }

            override fun onStop(lyricData: LyricData) {
                updateTestLyricViews(
                    lyricData, testAppLyric, testAppIcon, testAppCustomIcon,
                    testAppPlay, testAppName, testAppDelay
                )
                if (mi) Tools.cancelNotification(context = this@TestActivity)
            }
        })
        registerLyricListener(this@TestActivity, API.API_VERSION, receiver)

        testView.apply {
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.send_test_lyric,
                    onClick = { EventTools(context).sendLyric("testlyric") }
                )
            )
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.clean_test_lyric,
                    onClick = { EventTools(context).cleanLyric() }
                )
            )
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.mifocus,
                    summaryResId = R.string.mifocus_tips,
                    isChecked = mi,
                    onCheckedChange = { _, isChecked ->
                        mi = isChecked
                        if (!isChecked) {
                            Tools.cancelNotification(context = this@TestActivity)
                        }
                    }
                )
            )
        }

    }

    override fun onStop() {
        super.onStop()
        unregisterLyricListener(this, receiver)
        if (mi) Tools.cancelNotification(context = this@TestActivity)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterLyricListener(this, receiver)
        if (mi) Tools.cancelNotification(context = this@TestActivity)
    }

    override fun onResume() {
        super.onResume()
        registerLyricListener(this@TestActivity, API.API_VERSION, receiver)
    }

    private fun updateTestLyricViews(
        lyricData: LyricData,
        testAppLyric: TextView,
        testAppIcon: TextView,
        testAppCustomIcon: TextView,
        testAppPlay: TextView,
        testAppName: TextView,
        testAppDelay: TextView
    ) {
        testAppLyric.text = lyricData.lyric
        testAppIcon.text = lyricData.extraData.base64Icon
        testAppCustomIcon.text = lyricData.extraData.customIcon.toString()
        testAppPlay.text = lyricData.type.toString()
        this.let { testAppName.text = it.getPackageManager().getApplicationInfo(lyricData.extraData.packageName, 0).loadLabel(it.packageManager) }
        testAppDelay.text = lyricData.extraData.delay.toString()
    }

    private fun createSwitchView(
        context: Context,
        titleResId: Int,
        summaryResId: Int = 0,
        isChecked: Boolean = false,
        onCheckedChange: (CompoundButton, Boolean) -> Unit
    ): View {
        return createCustomView(
            context = context,
            titleResId = titleResId,
            summaryResId = summaryResId,
            isChecked = isChecked,
            onCheckedChange = onCheckedChange,
            hideSummary = summaryResId == 0,
            hideSwitch = false
        )
    }

    private fun createClickableView(
        context: Context,
        titleResId: Int,
        onClick: (View) -> Unit
    ): View {
        return createCustomView(
            context = context,
            titleResId = titleResId,
            onClick = onClick,
            hideSummary = true,
            hideSwitch = true
        )
    }

    private fun createCustomView(
        context: Context,
        titleResId: Int,
        summaryResId: Int = 0,
        isChecked: Boolean = false,
        onCheckedChange: ((CompoundButton, Boolean) -> Unit)? = null,
        hideSummary: Boolean = false,
        hideSwitch: Boolean = false,
        onClick: ((View) -> Unit)? = null
    ): View {
        val preferences = Preferences(context)
        preferences.setViewClickToggleSwitch()
        val switchView = preferences.getView()

        // Title
        preferences.preferencesTitle.setText(titleResId)

        // Summary
        if (hideSummary || summaryResId == 0) {
            preferences.preferencesSummary.visibility = View.GONE
        } else {
            preferences.setSummary(summaryResId)
        }

        // Switch
        if (hideSwitch || onCheckedChange == null) {
            preferences.preferencesButton.visibility = View.GONE
        } else {
            preferences.preferencesButton.isChecked = isChecked
            preferences.preferencesButton.setOnCheckedChangeListener(onCheckedChange)
        }

        // Click listener
        onClick?.let { switchView.setOnClickListener(it) }

        return switchView
    }
}