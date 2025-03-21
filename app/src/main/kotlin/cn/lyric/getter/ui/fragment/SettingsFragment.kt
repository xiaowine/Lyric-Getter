package cn.lyric.getter.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.R
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
            switch("salt_use_flyme") {
                titleRes = R.string.salt_use_flyme
                summaryRes = R.string.salt_use_flyme_summary
                onClick {
                    config.saltUseFlyme = checked
                    false
                }
            }
            editText("regex_replace") {
                titleRes = R.string.regex_replace
                defaultValue = config.regexReplace
                textChangeListener = EditTextPreference.OnTextChangeListener { _, text ->
                    config.regexReplace = text.toString()
                    false
                }
            }
            accentButtonPref("lyricsetting") {
                titleRes = R.string.lyricsetting
                onClick {
                    context?.let { showlyricSwitchDialog(it) }
                    false
                }
            }
            accentButtonPref("fuckwyyabout") {
                titleRes = R.string.fuckwyyabout
                onClick {
                    context?.let { showfuckwyySwitchDialog(it) }
                    false
                }
            }
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = PreferencesAdapter(screen)
        }
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
}