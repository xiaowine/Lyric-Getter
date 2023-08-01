package cn.lyric.getter.ui


import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import cn.lyric.getter.R
import cn.lyric.getter.config.ActivityOwnSP
import cn.lyric.getter.databinding.ActivityMainBinding
import cn.lyric.getter.tool.ActivityTools
import cn.lyric.getter.tool.ActivityTools.activated
import cn.lyric.getter.tool.ActivityTools.updateAppRules


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activated = checkLSPosed()
        applicationContext.theme.applyStyle(rikka.material.preference.R.style.ThemeOverlay_Rikka_Material3_Preference, true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.run {
            if (!activated) {
                nav.visibility = View.GONE
            } else {
                nav.apply {
                    selectedItemId = menu.findItem(R.id.home_nav).itemId
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.nav.setupWithNavController(findNavController(R.id.nav_host_fragment))
        updateAppRules()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun checkLSPosed(): Boolean {
        return try {
            ActivityTools.context = this
            ActivityOwnSP.ownSP
            true
        } catch (_: Exception) {
            false
        }
    }

}