package cn.lyric.getter.ui.fragment


import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.R
import cn.lyric.getter.config.ActivityOwnSP.config
import cn.lyric.getter.databinding.FragmentHomeBinding
import cn.lyric.getter.tool.ActivityTools
import cn.lyric.getter.tool.ActivityTools.activated
import cn.lyric.getter.tool.ActivityTools.getAppRules
import cn.lyric.getter.tool.Tools.restartTheScopedSoftware
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            if (!activated) {
                statusIcon.setImageResource(R.drawable.ic_round_error_outline)
                statusTitle.text = getString(R.string.unactivated)
                statusSummary.text = getString(R.string.unactivated_summary)
                status.apply {
                    setBackgroundColor(MaterialColors.getColor(requireContext(), android.R.attr.colorError, Color.RED))
                    setOnClickListener {
                        ActivityTools.restartApp()
                    }
                }
                floatingActionButton.visibility = View.GONE
            } else {
                floatingActionButton.setOnClickListener { view ->
                    Snackbar.make(view, getString(R.string.restart_the_scoped_software), Snackbar.LENGTH_LONG).apply {
                        anchorView = view
                        setAction(getString(R.string.restart)) {
                            restartTheScopedSoftware(context)
                        }
                    }.show()
                }
                toolbar.apply {
                    inflateMenu(R.menu.home_menu)
                    menu.findItem(R.id.show_hide_desktop_icons).apply {
                        isChecked = config.hideDesktopIcons
                    }
                    setOnMenuItemClickListener {
                        if  (it.itemId == R.id.show_hide_desktop_icons) {
                            it.isChecked = !it.isChecked
                            config.hideDesktopIcons = it.isChecked
                            requireContext().packageManager.setComponentEnabledSetting(
                                ComponentName(requireContext(), "${BuildConfig.APPLICATION_ID}.launcher"), if (it.isChecked) {
                                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                                } else {
                                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                                }, PackageManager.DONT_KILL_APP
                            )
                        }
                        true
                    }
                }
            }
            versionLabelValue.text = BuildConfig.VERSION_NAME
            versionCodeValue.text = BuildConfig.VERSION_CODE.toString()
            versionTypeValue.text = BuildConfig.BUILD_TYPE.uppercase()
            buildTimeValue.text = SimpleDateFormat("yyyy-MM-dd HH:mm z", Locale.getDefault()).format(BuildConfig.BUILD_TIME)
            apiVersionValue.text = BuildConfig.API_VERSION.toString()
            configVersionValue.text = BuildConfig.CONFIG_VERSION.toString()
           appRulesVersionValue.text= getAppRules().appRulesVersion.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}