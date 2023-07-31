package cn.lyric.getter.ui.fragment


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.lyric.getter.R
import cn.lyric.getter.data.AppInfo
import cn.lyric.getter.data.lyricType
import cn.lyric.getter.databinding.FragmentAppBinding
import cn.lyric.getter.tool.ActivityTools.getAppRules
import cn.lyric.getter.tool.Tools.goMainThread
import cn.lyric.getter.ui.dialog.MdProgressDialog
import cn.lyric.getter.ui.adapter.AppAdapter


class AppFragment : Fragment() {

    private var _binding: FragmentAppBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        val appAdapter = AppAdapter()
        binding.run {
            recyclerView.apply {
                layoutManager = mLayoutManager
                setItemViewCacheSize(2000)
                adapter = appAdapter
            }
        }

        val dialog = MdProgressDialog(requireContext()).apply {
            setTitle(getString(R.string.getting_app_information))
            setMessage(getString(R.string.getting_app_information_tips))
            show()
        }
        Thread {
            val appRules = getAppRules()
            val appInfosPackNames = appRules.map { it.packName }
            val manager = requireContext().packageManager
            manager.getInstalledPackages(0).forEach { packageInfo ->
                if (appInfosPackNames.contains(packageInfo.packageName)) {
                    val appVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        packageInfo.longVersionCode
                    } else {
                        packageInfo.versionCode.toLong()
                    }
                    appRules[appInfosPackNames.indexOf(packageInfo.packageName)].rules.forEach { rule ->
                        if ((rule.startVersionCode >= appVersionCode || rule.startVersionCode == 0L) && (rule.endVersionCode < appVersionCode || rule.endVersionCode == 0L)) {
                            val icon = packageInfo.applicationInfo.loadIcon(manager)
                            val name = packageInfo.applicationInfo.loadLabel(manager).toString()
                            val appInfoItem = AppInfo(name, appVersionCode, icon, rule.getLyricType.lyricType())
                            goMainThread { appAdapter.addData(appInfoItem) }
                        }
                    }
                }
            }
            dialog.dismiss()
        }.start()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}