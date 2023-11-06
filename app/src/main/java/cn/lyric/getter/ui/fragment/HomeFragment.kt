package cn.lyric.getter.ui.fragment


import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.R
import cn.lyric.getter.databinding.FragmentHomeBinding
import cn.lyric.getter.tool.ActivityTools.getAppRules
import cn.lyric.getter.tool.BackupTools
import cn.lyric.getter.tool.ConfigTools.config
import cn.lyric.getter.tool.Tools.restartTheScopedSoftware
import cn.lyric.getter.ui.adapter.NoticeAdapter
import cn.lyric.getter.ui.viewmodel.HomeViewModel
import cn.lyric.getter.ui.viewmodel.ShareViewModel
import cn.xiaowine.dsp.DSP
import cn.xiaowine.xkt.AcTool.restartApp
import cn.xiaowine.xkt.Tool.isNotNull
import cn.xiaowine.xkt.Tool.toUpperFirstCaseAndLowerOthers
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale


class HomeFragment : Fragment() {
    private var recoveryPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it.isNotNull { uri ->
            BackupTools.handleReadDocument(requireActivity(), DSP.sharedPreferences, uri)
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(R.string.recovery)
                setCancelable(false)
                setMessage(R.string.recovery_summary)
                setPositiveButton(R.string.restart) { _, _ ->
                    restartApp()
                }
                show()
            }
        }
    }
    private var backupPickerLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) {
        it.isNotNull { uri ->
            BackupTools.handleCreateDocument(requireActivity(), DSP.sharedPreferences, uri)
        }
    }

    private val shareViewModel: ShareViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private var verticalOffset: Int = 0
    private var scrollRange: Int = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            appbarLayout.setExpanded(homeViewModel.expanded)
            //监听AppBarLayout偏移量
            appbarLayout.addOnOffsetChangedListener { appbarLayout, verticalOffset ->
                this@HomeFragment.verticalOffset = verticalOffset
                scrollRange = appbarLayout.totalScrollRange
            }
            nestedScrollView.scrollY = homeViewModel.scrollY
            if (!shareViewModel.activated) {
                statusIcon.setImageResource(R.drawable.ic_round_error_outline)
                statusTitle.text = getString(R.string.unactivated)
                statusSummary.text = getString(R.string.unactivated_summary)
                status.apply {
                    setBackgroundColor(MaterialColors.getColor(requireContext(), android.R.attr.colorError, Color.RED))
                    setOnClickListener {
                        restartApp()
                    }
                }
                floatingActionButton.visibility = View.GONE
            } else {
                homeViewModel.noticeList.observe(viewLifecycleOwner) {
                    if (it.isEmpty()) {
                        notice.visibility = View.GONE
                    } else {
                        viewPager.adapter = NoticeAdapter(it)
                        notice.visibility = View.VISIBLE
                    }
                }
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
                        when (it.itemId) {
                            R.id.backup -> {
                                backupPickerLauncher.launch("LyricGetter_Backup_${SimpleDateFormat("yyyy-MM-dd-HH:mm", Locale.getDefault()).format(System.currentTimeMillis())}.json")
                            }

                            R.id.recovery -> {
                                recoveryPickerLauncher.launch("application/json")
                            }

                            R.id.show_hide_desktop_icons -> {
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
                        }
                        true
                    }
                }
            }
            deviceValue.text = "${Build.BRAND} ${Build.MODEL} Android${Build.VERSION.RELEASE}"
            versionLabelValue.text = BuildConfig.VERSION_NAME
            versionCodeValue.text = BuildConfig.VERSION_CODE.toString()
            versionTypeValue.text = BuildConfig.BUILD_TYPE.toUpperFirstCaseAndLowerOthers()
            buildTimeValue.text = homeViewModel.buildTimeValue ?: SimpleDateFormat("yyyy-MM-dd HH:mm z", Locale.getDefault()).format(BuildConfig.BUILD_TIME)
            apiVersionValue.text = BuildConfig.API_VERSION.toString()
            configVersionValue.text = BuildConfig.CONFIG_VERSION.toString()
            appRulesVersionValue.text = homeViewModel.appRulesVersionValue ?: getAppRules().appRulesVersion.toString()
            appRulesApiVersionValue.text = BuildConfig.APP_RULES_API_VERSION.toString()
        }
    }

    override fun onStart() {
        super.onStart()
        homeViewModel.getNotice()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.apply {
            scrollY = binding.nestedScrollView.scrollY
            expanded = if (verticalOffset == 0) true else scrollRange < verticalOffset
            appRulesVersionValue = binding.appRulesVersionValue.text.toString()
            homeViewModel.buildTimeValue = binding.buildTimeValue.text.toString()
        }
        _binding = null
    }
}