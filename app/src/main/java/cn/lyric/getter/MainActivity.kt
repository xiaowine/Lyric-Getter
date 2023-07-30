package cn.lyric.getter



import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import cn.lyric.getter.config.ActivityOwnSP
import cn.lyric.getter.databinding.ActivityMainBinding
import cn.lyric.getter.tool.ActivityTools
import cn.lyric.getter.tool.ActivityTools.activated


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activated=checkLSPosed()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightNavigationBars = true
        }
        applicationContext.theme.applyStyle(rikka.material.preference.R.style.ThemeOverlay_Rikka_Material3_Preference, true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.run {
            if (!activated) {
                nav.visibility = View.GONE
            } else {
                nav.setOnItemSelectedListener {
                    if (!this@MainActivity::navController.isInitialized) {
                        navController = findNavController(R.id.nav_host_fragment)
                    }
                    runCatching {
                        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
                        navController.setGraph(navGraph, Bundle())
                        when (it.itemId) {
                            R.id.home_nav -> navController.popBackStack()
                            R.id.settings_nav -> navController.navigate(R.id.action_HomeFragment_to_SettingsFragment)
                            else -> {}
                        }
                    }
                    true
                }
            }
        }
        setContentView(binding.root)
    }
    override fun onSupportNavigateUp(): Boolean {
        if (!this@MainActivity::navController.isInitialized) {
            navController = findNavController(R.id.nav_host_fragment)
        }
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun checkLSPosed(): Boolean {
       return try {
            ActivityTools.context = this
            ActivityOwnSP.ownSP
            true
        } catch (_: Exception) {
//            MaterialAlertDialogBuilder(this)
//                .setTitle("错误")
//                .setMessage("请安装并启用LSPosed框架")
//                .setPositiveButton("确定") { _, _ -> finish() }
//                .setCancelable(false)
//                .show()
            false
        }
    }
}