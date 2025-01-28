package cn.lyric.getter.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.R
import cn.lyric.getter.databinding.FragmentSettingsBinding
import cn.lyric.getter.tool.ConfigTools.config
import de.Maxr1998.modernpreferences.PreferencesAdapter
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
            switch("output_repeated_lyrics") {
                titleRes = R.string.output_repeated_lyrics
                onClick {
                    config.outputRepeatedLyrics = checked
                    false
                }
            }
            switch("enhanced_hidden_lyrics") {
                titleRes = R.string.enhanced_hidden_lyrics
                summaryRes = R.string.enhanced_hidden_lyrics_summary
                onClick {
                    config.enhancedHiddenLyrics = checked
                    false
                }
            }
            switch("allow_some_software_to_output_after_the_screen") {
                titleRes = R.string.allow_some_software_to_output_after_the_screen
                onClick {
                    config.allowSomeSoftwareToOutputAfterTheScreen = checked
                    false
                }
            }
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
            switch("show_title") {
                titleRes = R.string.show_title
                defaultValue = config.showTitle
                onClick {
                    config.showTitle = checked
                    false
                }
            }
            switch("fuckwyy") {
                titleRes = R.string.fuckwyy
                summaryRes = R.string.fuckwyy_tips
                defaultValue = config.fuckwyysb163
                onClick {
                    config.fuckwyysb163 = checked
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
}