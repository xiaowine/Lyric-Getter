package cn.lyric.getter.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import cn.lyric.getter.R
import cn.lyric.getter.api.API
import cn.lyric.getter.api.data.LyricData
import cn.lyric.getter.api.listener.LyricListener
import cn.lyric.getter.api.listener.LyricReceiver
import cn.lyric.getter.api.tools.Tools.registerLyricListener
import cn.lyric.getter.api.tools.Tools.unregisterLyricListener
import cn.lyric.getter.databinding.FragmentSettingsBinding
import cn.lyric.getter.tool.ConfigTools.config
import cn.lyric.getter.tool.Tools.dp2px
import cn.lyric.getter.ui.activity.DialogTransparentActivity
import cn.lyric.getter.ui.view.Preferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSettings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSettings() {
        val context = context ?: return

        binding.fragmentSettingLinearlayout.apply {
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.salt_use_flyme,
                    summaryResId = R.string.salt_use_flyme_summary,
                    isChecked = config.saltUseFlyme,
                    onCheckedChange = { _, isChecked -> config.saltUseFlyme = isChecked }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.regex_replace,
                    onClick = {
                        val intent = Intent(context, DialogTransparentActivity::class.java)
                        startActivity(intent) }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.lyricsetting,
                    onClick = { showLyricSettingsDialog(context) }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.fuckwyyabout,
                    onClick = { showFuckWyySettingsDialog(context) }
                ))
            addView(
                createClickableView(
                    context = context,
                    titleResId = R.string.testlyric,
                    onClick = { showTestLyricDialog(context) }
                ))
        }
    }

    private fun showLyricSettingsDialog(context: Context) {
        val (scrollView, contentLayout) = createScrollableDialogLayout(context)

        contentLayout.apply {
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.enhanced_hidden_lyrics,
                    summaryResId = R.string.enhanced_hidden_lyrics_summary,
                    isChecked = config.enhancedHiddenLyrics,
                    onCheckedChange = { _, isChecked -> config.enhancedHiddenLyrics = isChecked }
                ))
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.output_repeated_lyrics,
                    isChecked = config.outputRepeatedLyrics,
                    onCheckedChange = { _, isChecked -> config.outputRepeatedLyrics = isChecked }
                ))
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.allow_some_software_to_output_after_the_screen,
                    isChecked = config.allowSomeSoftwareToOutputAfterTheScreen,
                    onCheckedChange = { _, isChecked ->
                        config.allowSomeSoftwareToOutputAfterTheScreen = isChecked
                    }
                ))
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.show_title,
                    isChecked = config.showTitle,
                    onCheckedChange = { _, isChecked -> config.showTitle = isChecked }
                ))
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.lyricsetting)
            .setView(scrollView)
            .show()
    }

    private fun showFuckWyySettingsDialog(context: Context) {
        val (scrollView, contentLayout) = createScrollableDialogLayout(context)

        contentLayout.apply {
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.fuckfuckwyy,
                    summaryResId = R.string.fuckfuckwyy_tips,
                    isChecked = config.fuckWyy2,
                    onCheckedChange = { _, isChecked -> config.fuckWyy2 = isChecked }
                ))
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.fuckwyy,
                    summaryResId = R.string.fuckwyy_tips,
                    isChecked = config.fuckWyy,
                    onCheckedChange = { _, isChecked -> config.fuckWyy = isChecked }
                ))
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.fuckwyyabout)
            .setView(scrollView)
            .show()
    }

    private fun showTestLyricDialog(context: Context) {
        val testLyricView = LayoutInflater.from(context).inflate(R.layout.dialog_lyric_test, null)

        val testAppName = testLyricView.findViewById<TextView>(R.id.test_app_name_text)
        val testAppIcon = testLyricView.findViewById<TextView>(R.id.test_app_icon_text)
        val testAppCustomIcon = testLyricView.findViewById<TextView>(R.id.test_app_customIcon_text)
        val testAppPlay = testLyricView.findViewById<TextView>(R.id.test_app_play_text)
        val testAppLyric = testLyricView.findViewById<TextView>(R.id.test_app_lyric_text)
        val testAppDelay = testLyricView.findViewById<TextView>(R.id.test_app_delay_text)

        val receiver = LyricReceiver(object : LyricListener() {
            override fun onUpdate(lyricData: LyricData) {
                updateTestLyricViews(
                    lyricData, testAppLyric, testAppIcon, testAppCustomIcon,
                    testAppPlay, testAppName, testAppDelay
                )
            }

            override fun onStop(lyricData: LyricData) {
                updateTestLyricViews(
                    lyricData, testAppLyric, testAppIcon, testAppCustomIcon,
                    testAppPlay, testAppName, testAppDelay
                )
            }
        })

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(R.string.testlyric)
            .setView(testLyricView)
            .create()

        dialog.setOnDismissListener {
            unregisterLyricListener(context, receiver)
        }

        registerLyricListener(context, API.API_VERSION, receiver)
        dialog.show()
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
        testAppName.text = lyricData.extraData.packageName
        testAppDelay.text = lyricData.extraData.delay.toString()
    }

    private fun createScrollableDialogLayout(context: Context): Pair<ScrollView, LinearLayout> {
        val scrollView = ScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(0, 0, 0, dp2px(context, 8f))
        }

        val contentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        scrollView.addView(contentLayout)
        return Pair(scrollView, contentLayout)
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
