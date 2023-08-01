package cn.lyric.getter.ui.fragment


import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.R
import cn.lyric.getter.data.AppRule
import cn.lyric.getter.data.AppRuleInfo
import cn.lyric.getter.databinding.FragmentAppRulesBinding
import cn.lyric.getter.tool.ActivityTools.getAppRules
import cn.lyric.getter.tool.ActivityTools.openUrl
import cn.lyric.getter.tool.JsonTools.toJSON
import cn.lyric.getter.tool.Tools.goMainThread
import cn.lyric.getter.ui.adapter.AppAdapter
import cn.lyric.getter.ui.dialog.MaterialProgressDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class AppRulesFragment : Fragment() {

    private lateinit var appAdapter: AppAdapter

    private val appRules: List<AppRule> by lazy { getAppRules().appRules }
    private val manager: PackageManager by lazy { requireContext().packageManager }

    private var _binding: FragmentAppRulesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppRulesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        appAdapter = AppAdapter().apply {
            setOnItemClickListener(object : AppAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, view: View) {
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setTitle("Rule")
                        setMessage(appRules.filter { it.packName == dataLists[position].packageInfo.packageName }.toJSON(true))
                        setPositiveButton("确定") { _, _ -> }
                        show()
                    }
                }
            })
        }
        binding.apply {
            card.setOnClickListener {
                openUrl("https://github.com/xiaowine/Lyric-Getter/issues/new?assignees=&labels=&projects=&template=feature_request.md&title=App%E8%A7%84%E5%88%99%E6%94%B9%E5%8A%A8")
            }
            recyclerView.apply {
                layoutManager = mLayoutManager
                setItemViewCacheSize(2000)
                adapter = appAdapter
            }
            swipeRefreshLayout.setOnRefreshListener {
                loadAppRules()
            }
            loadAppRules()
        }
    }


    private fun loadAppRules() {
        appAdapter.removeAllData()
        val dialog = MaterialProgressDialog(requireContext()).apply {
            setTitle(getString(R.string.getting_app_information))
            setMessage(getString(R.string.getting_app_information_tips))
            show()
        }
        Thread {
            val appInfosPackNames = appRules.map { it.packName }
            manager.getInstalledPackages(0).forEach { packageInfo ->
                val packageName = packageInfo.packageName
                if (appInfosPackNames.contains(packageName)) {
                    val filter = appRules.filter { it.packName == packageName }[0]
                    goMainThread { appAdapter.addData(AppRuleInfo(packageInfo, "", filter)) }
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