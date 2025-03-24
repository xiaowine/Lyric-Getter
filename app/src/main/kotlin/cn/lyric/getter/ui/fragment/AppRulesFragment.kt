@file:Suppress("DEPRECATION")

package cn.lyric.getter.ui.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.R
import cn.lyric.getter.data.AppInfos
import cn.lyric.getter.data.AppRule
import cn.lyric.getter.data.Rule
import cn.lyric.getter.databinding.FragmentAppRulesBinding
import cn.lyric.getter.databinding.ItemsAppBinding
import cn.lyric.getter.tool.ActivityTools.getAppRules
import cn.lyric.getter.tool.ActivityTools.updateAppRules
import cn.lyric.getter.tool.AppRulesTools.getAppStatus
import cn.lyric.getter.tool.AppRulesTools.getAppStatusDescription
import cn.lyric.getter.tool.AppRulesTools.lyricType
import cn.lyric.getter.tool.ConfigTools.config
import cn.lyric.getter.ui.adapter.AppRulesAdapter
import cn.lyric.getter.ui.dialog.MaterialProgressDialog
import cn.lyric.getter.ui.viewmodel.AppRulesViewModel
import cn.xiaowine.xkt.AcTool.openURL
import cn.xiaowine.xkt.Tool.goMainThread
import cn.xiaowine.xkt.Tool.toUpperFirstCaseAndLowerOthers
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AppRulesFragment : Fragment() {

    private lateinit var appAdapter: AppRulesAdapter

    private val appRulesViewModel: AppRulesViewModel by viewModels()

    private val appRules: List<AppRule> by lazy { getAppRules().appRules }
    private val manager: PackageManager by lazy { requireContext().packageManager }
    private val packageManager: PackageManager by lazy { requireContext().packageManager }
    private var _binding: FragmentAppRulesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppRulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!config.alreadyShowWarning) {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(R.string.remarks)
                setMessage(R.string.rules_are_only_for_viewing_supported_versions)
                setNegativeButton(R.string.ok) { _, _ ->
                    config.alreadyShowWarning = true
                }
                setCancelable(false)
                show()
            }
        }
        appAdapter = AppRulesAdapter().apply {
            setOnItemClickListener(object : AppRulesAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, viewBinding: ItemsAppBinding) {
                    val versionCode = viewBinding.appVersionCodeView.text.let {
                        if (it.isEmpty()) {
                            0
                        } else {
                            it.toString().toInt()
                        }
                    }
                    val packageName = dataLists[position].packageName
                    val appRule = appRules.filter { it.packageName == packageName }[0]
                    if (appRule.rules.size == 1) {
                        showRuleDialog(appRule.rules[0], appRule.name, versionCode)
                    } else {
                        MaterialAlertDialogBuilder(requireContext()).apply {
                            setTitle(R.string.select_the_mode_you_want_to_view)
                            setItems(Array(appRule.rules.size) {
                                getString(
                                    R.string.mode_num,
                                    (it + 1).toString()
                                )
                            }) { _, which ->
                                showRuleDialog(
                                    appRule.rules[which],
                                    "${appRule.name}: ${
                                        getString(
                                            R.string.mode_num,
                                            (which + 1).toString()
                                        )
                                    }",
                                    versionCode
                                )
                            }
                            setNegativeButton(R.string.cancel, null)
                            show()
                        }
                    }
                }
            })
        }
        binding.apply {
            toolbar.apply {
                inflateMenu(R.menu.app_rules_menu)
                menu.findItem(R.id.show_all_rules).apply {
                    isChecked = config.showAllRules
                }
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.show_all_rules -> {
                            it.isChecked = !it.isChecked
                            config.showAllRules = it.isChecked
                            loadAppRules(isFirst = false, isSwipeRefresh = false)
                        }

                        R.id.update_app_rules -> {
                            updateAppRules()
                        }
                    }
                    true
                }
            }
            card.setOnClickListener {
                "https://github.com/xiaowine/Lyric-Getter/issues/new?assignees=&labels=&projects=&template=feature_request.md&title=App%E8%A7%84%E5%88%99%E6%94%B9%E5%8A%A8".openURL()
            }
            recyclerView.apply {
                layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                adapter = appAdapter
            }
            swipeRefreshLayout.setOnRefreshListener {
                loadAppRules(isFirst = false, isSwipeRefresh = true)
            }
            nestedScrollView.scrollY = appRulesViewModel.scrollY
            loadAppRules(isFirst = true, isSwipeRefresh = false)
        }
    }

    fun showRuleDialog(rule: Rule, title: String, versionCode: Int) {
        val items = arrayOf(
            "${getString(R.string.current_status)}：${
                getAppStatusDescription(
                    getAppStatus(
                        rule,
                        versionCode
                    ), rule, false
                )
            }",
            "${getString(R.string.use_api)}：${
                rule.useApi.toString().toUpperFirstCaseAndLowerOthers()
            }",
            "${getString(R.string.api_version)}：${
                rule.useApi.takeIf { it }?.let { rule.apiVersion } ?: getString(R.string.no_have)
            }",
            "${getString(R.string.start_version_code)}：${rule.startVersionCode}",
            "${getString(R.string.end_version_code)}：${rule.endVersionCode}",
            "${getString(R.string.exclude_versions)}：${rule.excludeVersions.ifEmpty { getString(R.string.no_have) }}",
            "${getString(R.string.get_lyric_type)}：${
                rule.useApi.takeIf { !it }
                    ?.let { rule.getLyricType.lyricType() } ?: getString(R.string.api_pattern)
            }",
            "${getString(R.string.remarks)}：${rule.remarks.ifEmpty { getString(R.string.no_have) }}"
        )
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(title)
            setItems(items, null)
            setNegativeButton(R.string.ok, null)
            show()
        }
    }

    private fun loadAppRules(isFirst: Boolean = false, isSwipeRefresh: Boolean = false) {
        appAdapter.removeAllData()
        if (isFirst && appRulesViewModel.dataLists.isNotEmpty()) {
            appRulesViewModel.dataLists.forEach {
                appAdapter.addData(it)
            }
            goMainThread {
                binding.toolbar.title =
                    "${getString(R.string.app_rules_fragment_label)} (${appRulesViewModel.dataLists.size})"
            }
        } else {
            val dialog = MaterialProgressDialog(requireContext()).apply {
                setTitle(getString(R.string.getting_app_information))
                setMessage(getString(R.string.getting_app_information_tips))
                show()
            }
            Thread {
                val installedPackages = manager.getInstalledPackages(0)

                if (installedPackages.isEmpty() || installedPackages.size < 10) {
                    goMainThread {
                        context?.let {
                            MaterialAlertDialogBuilder(it)
                                .setTitle(R.string.app_no_permissions)
                                .setMessage(R.string.app_get_permissions)
                                .setPositiveButton(R.string.app_go_setting) { _, _ ->
                                    val intent =
                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package", "cn.lyric.getter", null)
                                    intent.setData(uri)
                                    startActivity(intent)

                                }
                                .setNegativeButton(R.string.cancel, null)
                                .show()
                        }
                        dialog.dismiss()
                    }
                    return@Thread
                }
                val appInfosPackNames = installedPackages.map { it.packageName }
                appRules.forEach { appRule ->
                    var appInfos: AppInfos? = null
                    if (appInfosPackNames.contains(appRule.packageName)) {
                        val packageInfo =
                            installedPackages.firstOrNull { it.packageName == appRule.packageName }
                        packageInfo?.let {
                            val applicationInfo = packageInfo.applicationInfo ?: return@let
                            appInfos = AppInfos(
                                applicationInfo.loadLabel(packageManager).toString(),
                                applicationInfo.loadIcon(packageManager),
                                packageInfo.packageName,
                                packageInfo.versionCode,
                                appRule
                            )
                        }
                    } else if (config.showAllRules) {
                        val packageInfo =
                            installedPackages.firstOrNull { it.packageName == "com.android.systemui" }
                        packageInfo?.let {
                            val applicationInfo = packageInfo.applicationInfo ?: return@let
                            appInfos = AppInfos(
                                appRule.name,
                                applicationInfo.loadIcon(packageManager),
                                appRule.packageName,
                                0,
                                appRule,
                                false
                            )
                        }
                    }
                    goMainThread { appInfos?.let { appAdapter.addData(it) } }
                }
                dialog.dismiss()
                goMainThread {
                    binding.toolbar.title =
                        "${getString(R.string.app_rules_fragment_label)} (${appAdapter.dataLists.size})"
                }
            }.start()
        }
        if (isSwipeRefresh) binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        appRulesViewModel.apply {
            dataLists = appAdapter.dataLists
            scrollY = binding.nestedScrollView.scrollY
        }
        _binding = null
    }
}
