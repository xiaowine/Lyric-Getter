package cn.lyric.getter.ui.fragment


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.R
import cn.lyric.getter.data.AppInfo
import cn.lyric.getter.data.AppStatus
import cn.lyric.getter.data.lyricType
import cn.lyric.getter.databinding.FragmentAppRulesBinding
import cn.lyric.getter.tool.ActivityTools.getAppRules
import cn.lyric.getter.tool.ActivityTools.showToast
import cn.lyric.getter.tool.LogTools.log
import cn.lyric.getter.tool.Tools.goMainThread
import cn.lyric.getter.ui.adapter.AppAdapter
import cn.lyric.getter.ui.dialog.MdProgressDialog


class AppRulesFragment : Fragment() {

    private lateinit var appAdapter: AppAdapter
    private var _binding: FragmentAppRulesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppRulesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        appAdapter = AppAdapter()
        binding.run {
            recyclerView.apply {
                layoutManager = mLayoutManager
                setItemViewCacheSize(2000)
                adapter = appAdapter
            }
            swipeRefreshLayout.setOnRefreshListener {
                loadAppRules()
            }
        }
        loadAppRules()
    }

    private fun loadAppRules() {
        appAdapter.removeAllData()
        val dialog = MdProgressDialog(requireContext()).apply {
            setTitle(getString(R.string.getting_app_information))
            setMessage(getString(R.string.getting_app_information_tips))
            show()
        }
        Thread {
            val appRules = getAppRules().appRules
            val appInfosPackNames = appRules.map { it.packName }
            val manager = requireContext().packageManager
            val packageInfos = manager.getInstalledPackages(0)
            packageInfos.forEach { packageInfo ->
                val packageName = packageInfo.packageName
                if (appInfosPackNames.contains(packageName)) {
                    val appVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        packageInfo.longVersionCode
                    } else {
                        @Suppress("DEPRECATION")
                        packageInfo.versionCode.toLong()
                    }
                    appRules[appInfosPackNames.indexOf(packageName)].rules.forEach { rule ->
                        val status = if (rule.useApi) {
                            if (rule.apiVersion < BuildConfig.API_VERSION) {
                                AppStatus.LowApi
                            } else if (rule.apiVersion > BuildConfig.API_VERSION) {
                                AppStatus.MoreAPI

                            } else {
                                AppStatus.API
                            }
                        } else {
                            if (rule.startVersionCode == 0L && rule.endVersionCode == 0L) {
                                AppStatus.UnKnow
                            } else {
                                if (rule.endVersionCode == 0L || rule.endVersionCode > appVersionCode) {
                                    if (rule.startVersionCode <= appVersionCode) AppStatus.Hook else AppStatus.NoSupport
                                } else {
                                    AppStatus.NoSupport
                                }
                            }
                        }
                        goMainThread {
                            if (appAdapter.data.none { it.packageName == packageName }) {
                                val icon = packageInfo.applicationInfo.loadIcon(manager)
                                val name = packageInfo.applicationInfo.loadLabel(manager).toString()
                                val description = when (status) {
                                    AppStatus.API -> getString(R.string.api)
                                    AppStatus.Hook -> getString(R.string.hook).format(rule.getLyricType.lyricType())
                                    AppStatus.LowApi -> getString(R.string.low_api)
                                    AppStatus.MoreAPI -> getString(R.string.more_api)
                                    AppStatus.UnKnow -> getString(R.string.un_know)
                                    AppStatus.NoSupport -> getString(R.string.no_support)
                                }
                                val appInfoItem = AppInfo(name, packageName, appVersionCode, icon, description)
                                appAdapter.addData(appInfoItem)
                            }
                        }
                    }
                }
            }
            dialog.dismiss()
            binding.swipeRefreshLayout.isRefreshing = false
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}