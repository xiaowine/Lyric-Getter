package cn.lyric.getter.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.R
import cn.lyric.getter.api.API
import cn.lyric.getter.api.data.LyricData
import cn.lyric.getter.api.listener.LyricReceiver
import cn.lyric.getter.api.listener.LyricListener
import cn.lyric.getter.api.tools.Tools.registerLyricListener
import cn.lyric.getter.api.tools.Tools.unregisterLyricListener
import cn.lyric.getter.databinding.FragmentSettingsBinding
import cn.lyric.getter.tool.ConfigTools.config
import cn.lyric.getter.ui.dialog.MD3SwitchHelp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.accentButtonPref
import de.Maxr1998.modernpreferences.helpers.editText
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.screen
import de.Maxr1998.modernpreferences.helpers.switch
import de.Maxr1998.modernpreferences.preferences.EditTextPreference

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
        val screen = screen(context) {
            editText("regex_replace") {
                titleRes = R.string.regex_replace
                defaultValue = config.regexReplace
                textChangeListener = EditTextPreference.OnTextChangeListener { _, text ->
                    config.regexReplace = text.toString()
                    false
                }
            }
        }

        /** 椒盐魅族接口  */
        val salt_use_flyme = context?.let { MD3SwitchHelp(it) }
        val salt_use_flymeView = salt_use_flyme?.getView()
        salt_use_flyme?.switchTitle?.setText(R.string.salt_use_flyme)
        salt_use_flyme?.setTips(R.string.salt_use_flyme_summary)
        salt_use_flyme?.switchButton?.isChecked = (config.saltUseFlyme)
        val switch = salt_use_flyme?.switchButton
        switch?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            config.saltUseFlyme = isChecked
        }
        // 设置点击监听器
        salt_use_flymeView?.setOnClickListener {
            // 切换开关状态
            salt_use_flyme.switchButton.isChecked = !salt_use_flyme.switchButton.isChecked
            config.saltUseFlyme = salt_use_flyme.switchButton.isChecked
        }

        /** 歌词获取设置  */
        val lyricsetting = context?.let { MD3SwitchHelp(it) }
        val lyricsettingView = lyricsetting?.getView()
        lyricsetting?.switchTitle?.setText(R.string.lyricsetting)
        val switchsetting = lyricsetting?.switchButton
        switchsetting?.visibility = View.GONE
        // 设置点击监听器
        lyricsettingView?.setOnClickListener {
            context?.let { showlyricSwitchDialog(it) }
        }

        /** fuckwyy  */
        val fuckwyyabout = context?.let { MD3SwitchHelp(it) }
        val fuckwyyaboutView = fuckwyyabout?.getView()
        fuckwyyabout?.switchTitle?.setText(R.string.fuckwyyabout)
        fuckwyyabout?.switchButton?.visibility = View.GONE
        // 设置点击监听器
        fuckwyyaboutView?.setOnClickListener {
            context?.let { showfuckwyySwitchDialog(it) }
        }

        /** testlyric  */
        val testlyric = context?.let { MD3SwitchHelp(it) }
        val testlyricView = testlyric?.getView()
        testlyric?.switchTitle?.setText(R.string.testlyric)
        testlyric?.switchButton?.visibility = View.GONE
        // 设置点击监听器
        testlyricView?.setOnClickListener {
            context?.let { showtestlyric(it) }
        }

        binding.fragmentSettingLinearlayout.addView(salt_use_flymeView)
        binding.fragmentSettingLinearlayout.addView(lyricsettingView)
        binding.fragmentSettingLinearlayout.addView(fuckwyyaboutView)
        binding.fragmentSettingLinearlayout.addView(testlyricView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showlyricSwitchDialog(context: Context) {
        /** all view */
        val allview = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(24, 24, 24, 24) // 根据需要设置 padding
        }

        /** 增强隐藏歌词 */
        val enhanced_hidden_lyrics = MD3SwitchHelp(context)
        val SwitchdialogView = enhanced_hidden_lyrics.getView()
        enhanced_hidden_lyrics.switchTitle.setText(R.string.enhanced_hidden_lyrics)
        enhanced_hidden_lyrics.setTips(R.string.enhanced_hidden_lyrics_summary)
        enhanced_hidden_lyrics.switchButton.isChecked = (config.enhancedHiddenLyrics)
        val switch = enhanced_hidden_lyrics.switchButton
        switch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            config.enhancedHiddenLyrics = isChecked
        }

        /** 输出重复歌词 */
        val output_repeated_lyrics = MD3SwitchHelp(context)
        val SwitchdialogView2 = output_repeated_lyrics.getView()
        output_repeated_lyrics.switchTitle.setText(R.string.output_repeated_lyrics)
        output_repeated_lyrics.switchButton.isChecked = (config.outputRepeatedLyrics)
        val switch2 = output_repeated_lyrics.switchButton
        switch2.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            config.outputRepeatedLyrics = isChecked
        }

        /** 息屏输出歌词 */
        val allow_some_software_to_output_after_the_screen = MD3SwitchHelp(context)
        val SwitchdialogView3 = allow_some_software_to_output_after_the_screen.getView()
        allow_some_software_to_output_after_the_screen.switchTitle.setText(R.string.allow_some_software_to_output_after_the_screen)
        allow_some_software_to_output_after_the_screen.switchButton.isChecked = (config.allowSomeSoftwareToOutputAfterTheScreen)
        val switch3 = allow_some_software_to_output_after_the_screen.switchButton
        switch3.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            config.allowSomeSoftwareToOutputAfterTheScreen = isChecked
        }

        /** 输出重复歌词 */
        val show_title = MD3SwitchHelp(context)
        val SwitchdialogView4 = show_title.getView()
        show_title.switchTitle.setText(R.string.show_title)
        show_title.switchButton.isChecked = (config.showTitle)
        val switch4 = show_title.switchButton
        switch4.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            config.showTitle = isChecked
        }

        allview.addView(SwitchdialogView)
        allview.addView(SwitchdialogView2)
        allview.addView(SwitchdialogView3)
        allview.addView(SwitchdialogView4)

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.lyricsetting)
            .setView(allview)
            .show()
    }

    private fun showfuckwyySwitchDialog(context: Context) {
        /** all view */
        val allview = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(24, 24, 24, 24) // 根据需要设置 padding
        }
        /** 网易云检测 */
        val fuckwyy2 = MD3SwitchHelp(context)
        val SwitchdialogView2 = fuckwyy2.getView()
        fuckwyy2.switchTitle.setText(R.string.fuckfuckwyy)
        fuckwyy2.setTips(R.string.fuckfuckwyy_tips)
        fuckwyy2.switchButton.isChecked = (config.fuckfuckwyysb163)
        val switch2 = fuckwyy2.switchButton
        switch2.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            config.fuckfuckwyysb163 = isChecked
        }

        /** 网易云强开 */
        val fuckwyy = MD3SwitchHelp(context)
        val SwitchdialogView = fuckwyy.getView()
        fuckwyy.switchTitle.setText(R.string.fuckwyy)
        fuckwyy.setTips(R.string.fuckwyy_tips)
        fuckwyy.switchButton.isChecked = (config.fuckwyysb163)
        val switch = fuckwyy.switchButton
        switch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            config.fuckwyysb163 = isChecked
        }

        allview.addView(SwitchdialogView)
        allview.addView(SwitchdialogView2)

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.fuckwyyabout)
            .setView(allview)
            .show()
    }

    private fun showtestlyric(context: Context){
        val testlyricview : View = LayoutInflater.from(context).inflate(R.layout.dialog_lyric_test, null)
        val testappname : TextView = testlyricview.findViewById(R.id.test_app_name_text)
        val testappicon : TextView = testlyricview.findViewById(R.id.test_app_icon_text)
        val testappcustomicon : TextView = testlyricview.findViewById(R.id.test_app_customIcon_text)
        val testappplay : TextView = testlyricview.findViewById(R.id.test_app_play_text)
        val testapplyric : TextView = testlyricview.findViewById(R.id.test_app_lyric_text)
        val testappdelay : TextView = testlyricview.findViewById(R.id.test_app_delay_text)
        val receiver = LyricReceiver(object : LyricListener() {
            override fun onUpdate(lyricData: LyricData) {
                testapplyric.text = lyricData.lyric
                testappicon.text = lyricData.extraData.base64Icon
                testappcustomicon.text = lyricData.extraData.customIcon.toString()
                testappplay.text = lyricData.type.toString()
                testappname.text = lyricData.extraData.packageName
                testappdelay.text = lyricData.extraData.delay.toString()

            }

            override fun onStop(lyricData: LyricData) {
                testapplyric.text = lyricData.lyric
                testappicon.text = lyricData.extraData.base64Icon
                testappcustomicon.text = lyricData.extraData.customIcon.toString()
                testappplay.text = lyricData.type.toString()
                testappname.text = lyricData.extraData.packageName
                testappdelay.text = lyricData.extraData.delay.toString()
            }
        })

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(R.string.testlyric)
            .setView(testlyricview)
            .create()

        dialog.setOnDismissListener {
            // 这里写关闭时需要执行的函数
            unregisterLyricListener(context, receiver)
        }
        registerLyricListener(context, API.API_VERSION, receiver)
        dialog.show()

    }
}