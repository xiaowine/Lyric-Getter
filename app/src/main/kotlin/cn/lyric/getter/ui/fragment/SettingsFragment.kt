package cn.lyric.getter.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import cn.lyric.getter.ui.dialog.EditTextDialogHelper
import cn.lyric.getter.ui.dialog.MD3SwitchHelp
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
        /** 椒盐魅族接口  */
        val saltUseFlymeOnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            config.saltUseFlyme = isChecked
        }
        val saltUseFlymeView = context?.let {
            diyview(
                it,
                R.string.salt_use_flyme,
                R.string.salt_use_flyme_summary,
                config.saltUseFlyme,
                saltUseFlymeOnCheckedChangeListener)
        }

        /** 歌词屏蔽  */
        val regex_replaceOnClickListener = View.OnClickListener{
            context?.let {
                context?.getString(R.string.regex_replace)?.let { it1 ->
                    EditTextDialogHelper(it).setText(config.regexReplace).setHint(it1)
                    val dialog = EditTextDialogHelper(it).show { inputText ->
                        config.regexReplace = inputText.toString()
                    }
                    dialog.setTitle(R.string.regex_replace)
                    dialog.show()
                    dialog.window?.apply { // After the window is created, get the SoftInputMode
                        clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                        clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
                        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                    }
                }
            }
        }
        val regex_replaceView = context?.let { diyview(
            it,
            R.string.regex_replace,
            0,
            false,
            null,
            true,
            true,
            regex_replaceOnClickListener
        ) }

        /** 歌词获取设置  */
        val lyricsettingOnClickListener = View.OnClickListener{
            context?.let { showlyricSwitchDialog(it) }
        }
        val lyricsettingView = context?.let { diyview(
            it,
            R.string.lyricsetting,
            0,
            false,
            null,
            true,
            true,
            lyricsettingOnClickListener
        ) }

        /** fuckwyy  */
        val fuckwyyaboutOnClickListener = View.OnClickListener{
            context?.let { showfuckwyySwitchDialog(it) }
        }
        val fuckwyyaboutview = context?.let { diyview(
            it,
            R.string.fuckwyyabout,
            0,
            false,
            null,
            true,
            true,
            fuckwyyaboutOnClickListener
        ) }

        /** testlyric  */
        val testlyricOnCListener = View.OnClickListener{
            context?.let { showtestlyric(it) }
        }
        val testlyricview = context?.let { diyview(
            it,
            R.string.testlyric,
            0,
            false,
            null,
            true,
            true,
            testlyricOnCListener
        ) }


        binding.fragmentSettingLinearlayout.addView(saltUseFlymeView)
        binding.fragmentSettingLinearlayout.addView(regex_replaceView)
        binding.fragmentSettingLinearlayout.addView(lyricsettingView)
        binding.fragmentSettingLinearlayout.addView(fuckwyyaboutview)
        binding.fragmentSettingLinearlayout.addView(testlyricview)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showlyricSwitchDialog(context: Context) {
        /** all view */
       val (scrollview, allview)  = allscrollview(context)

        /** 增强隐藏歌词 */
        val enhanced_hidden_lyricsCheckedChange = CompoundButton.OnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            config.enhancedHiddenLyrics = isChecked
        }
        val enhanced_hidden_lyricsView = diyview(
            context,
            R.string.enhanced_hidden_lyrics,
            R.string.enhanced_hidden_lyrics_summary,
            config.enhancedHiddenLyrics,
            enhanced_hidden_lyricsCheckedChange)

        /** 输出重复歌词 */
        val output_repeated_lyricsCheckedChange = CompoundButton.OnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            config.outputRepeatedLyrics = isChecked
        }
        val output_repeated_lyricsView = diyview(
            context,
            R.string.output_repeated_lyrics,
            0,
            config.outputRepeatedLyrics,
            output_repeated_lyricsCheckedChange
        )

        /** 息屏输出歌词 */
        val allow_some_software_to_output_after_the_screen = CompoundButton.OnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            config.allowSomeSoftwareToOutputAfterTheScreen = isChecked
        }
        val allow_some_software_to_output_after_the_screenView = diyview(
            context,
            R.string.allow_some_software_to_output_after_the_screen,
            0,
            config.allowSomeSoftwareToOutputAfterTheScreen,
            allow_some_software_to_output_after_the_screen
        )

        /** 输出重复歌词 */
        val show_title = CompoundButton.OnCheckedChangeListener{ _: CompoundButton?, isChecked : Boolean ->
            config.showTitle = isChecked
        }
        val SwitchdialogView = diyview(
            context,
            R.string.show_title,
            0,
            config.showTitle,
            show_title
        )

        allview.addView(enhanced_hidden_lyricsView)
        allview.addView(output_repeated_lyricsView)
        allview.addView(allow_some_software_to_output_after_the_screenView)
        allview.addView(SwitchdialogView)

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.lyricsetting)
            .setView(scrollview)
            .show()
    }

    private fun showfuckwyySwitchDialog(context: Context) {
        /** all view */
        val (scrollview, allview)  = allscrollview(context)
        /** 网易云检测 */
        val fuckwyy2 = CompoundButton.OnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            config.fuckfuckwyysb163 = isChecked
        }
        val fuckwyy2View = diyview(
            context,
            R.string.fuckfuckwyy,
            R.string.fuckfuckwyy_tips,
            config.fuckfuckwyysb163,
            fuckwyy2
        )


        /** 网易云强开 */
        val fuckwyyCheckedChange = CompoundButton.OnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            config.fuckwyysb163 = isChecked
        }
        val fuckwyyView = diyview(context, R.string.fuckwyy, R.string.fuckwyy_tips, config.fuckwyysb163,fuckwyyCheckedChange)

        allview.addView(fuckwyyView)
        allview.addView(fuckwyy2View)

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.fuckwyyabout)
            .setView(scrollview)
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


    private fun allscrollview(context: Context): Pair<ScrollView, LinearLayout> {
        val scrollView = ScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val allview = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 24, 0, 0)
        }
        scrollView.addView(allview)
        return Pair(scrollView, allview)
    }


    /** 开关界面简单封装定制
     * @param context Context
     * @param title 标题文本
     * @param tips 提示文本
     * @param iscleck 是否开启开关
     * @param CheckedChange OnCheckedChangeListener
     * @param hidetips 是否隐藏提示
     * @param hideswich 是否隐藏开关
     * @param setOnClickListener View点击监听器
     * @return 配置好的界面 */
    private fun diyview(context: Context,
                        title:Int = 0,
                        tips:Int = 0,
                        iscleck:Boolean = false,
                        CheckedChange:CompoundButton.OnCheckedChangeListener? = null,
                        hidetips:Boolean = false,
                        hideswich:Boolean = false,
                        setOnClickListener: View.OnClickListener? = null): View {
        val Switch = MD3SwitchHelp(context)
        Switch.setViewClickToggleSwitch()
        val SwitchdialogView = Switch.getView()
        if (title == 0 ){
            Switch.setTitle("Test")
        } else{
            Switch.switchTitle.setText(title)
        }

        if (hidetips || tips == 0){
            Switch.switchtips.visibility = View.GONE
        } else {
            Switch.setTips(tips)
        }
        if (hideswich || CheckedChange == null){
            Switch.switchButton.visibility = View.GONE
        } else{
            Switch.switchButton.isChecked = (iscleck)
            Switch.switchButton.setOnCheckedChangeListener(CheckedChange)
        }
        if (setOnClickListener != null){
            SwitchdialogView.setOnClickListener(setOnClickListener)
        }
        return SwitchdialogView
    }
}