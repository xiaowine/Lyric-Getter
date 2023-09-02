package cn.lyric.getter.ui.fragment


import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.R
import cn.lyric.getter.config.ActivityOwnSP.config
import cn.lyric.getter.data.AppInfos
import cn.lyric.getter.data.AppRule
import cn.lyric.getter.databinding.FragmentAppRulesBinding
import cn.lyric.getter.databinding.ItemsAppBinding
import cn.lyric.getter.tool.ActivityTools.getAppRules
import cn.lyric.getter.tool.ActivityTools.updateAppRules
import cn.lyric.getter.tool.JsonTools.toJSON
import cn.lyric.getter.ui.adapter.AppRulesAdapter
import cn.lyric.getter.ui.dialog.MaterialProgressDialog
import cn.lyric.getter.ui.viewmodel.AppRulesViewModel
import cn.xiaowine.xkt.AcTool.openURL
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.goMainThread


class AppRulesFragment : Fragment() {

    private lateinit var appAdapter: AppRulesAdapter

    private val appRulesViewModel: AppRulesViewModel by viewModels()

    private val appRules: List<AppRule> by lazy { getAppRules().appRules }
    private val manager: PackageManager by lazy { requireContext().packageManager }
    private val packageManager: PackageManager by lazy { requireContext().packageManager }
    private var _binding: FragmentAppRulesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppRulesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appAdapter = AppRulesAdapter().apply {
            expandedList = appRulesViewModel.expandedList
            setOnItemClickListener(object : AppRulesAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, viewBinding: ItemsAppBinding) {
                    viewBinding.apply {
                        appRulesCardView.apply {
                            visibility = if (visibility == View.VISIBLE) {
                                expandedList.remove(dataLists[position].packageName)
                                View.GONE
                            } else {
                                expandedList.add(dataLists[position].packageName)
                                View.VISIBLE
                            }
                        }
                        appRulesTextView.apply {
                            if (text.isEmpty()) {
                                text = appRules.filter { it.packageName == dataLists[position].packageName }.toJSON(true)
                            }
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
                            appAdapter.expandedList.clear()
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
                appAdapter.expandedList.clear()
                loadAppRules(isFirst = false, isSwipeRefresh = true)
            }
            nestedScrollView.scrollY = appRulesViewModel.scrollY.log()!!
            loadAppRules(isFirst = true, isSwipeRefresh = false)
        }
    }


    private fun loadAppRules(isFirst: Boolean = false, isSwipeRefresh: Boolean = false) {
        appAdapter.removeAllData()
        if (isFirst && appRulesViewModel.dataLists.isNotEmpty()) {
            appRulesViewModel.dataLists.forEach {
                appAdapter.addData(it)
            }
            goMainThread {
                binding.toolbar.title = "${getString(R.string.app_rules_fragment_label)}(${appRulesViewModel.dataLists.size})"
            }
        } else {
            val dialog = MaterialProgressDialog(requireContext()).apply {
                setTitle(getString(R.string.getting_app_information))
                setMessage(getString(R.string.getting_app_information_tips))
                show()
            }
            Thread {
                val installedPackages = manager.getInstalledPackages(0)
                val appInfosPackNames = installedPackages.map { it.packageName }
                appRules.forEach { appRule ->
                    var appInfos: AppInfos? = null
                    if (appInfosPackNames.contains(appRule.packageName)) {
                        val packageInfo = installedPackages.filter { it.packageName == appRule.packageName }[0]
                        val applicationInfo = packageInfo.applicationInfo
                        appInfos = AppInfos(applicationInfo.loadLabel(packageManager).toString(), applicationInfo.loadIcon(packageManager), packageInfo.packageName, packageInfo.versionCode, appRule)

                    } else if (config.showAllRules) {
                        val packageInfo = installedPackages.filter { it.packageName == "com.android.systemui" }[0]
                        appInfos = AppInfos(appRule.name, packageInfo.applicationInfo.loadIcon(packageManager), appRule.packageName, 0, appRule, false)
                    }
                    goMainThread { appInfos?.let { appAdapter.addData(it) } }
                }
                dialog.dismiss()
                goMainThread {
                    binding.toolbar.title = "${getString(R.string.app_rules_fragment_label)}(${appAdapter.dataLists.size})"
                }
            }.start()
        }
        if (isSwipeRefresh) binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        appAdapter.expandedList.size.log()
        appRulesViewModel.apply {
            dataLists = appAdapter.dataLists
            scrollY = binding.nestedScrollView.scrollY
            expandedList = appAdapter.expandedList
        }
        _binding = null
    }
}