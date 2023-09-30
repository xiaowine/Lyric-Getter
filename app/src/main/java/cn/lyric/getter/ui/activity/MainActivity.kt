package cn.lyric.getter.ui.activity


import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import cn.lyric.getter.R
import cn.lyric.getter.databinding.ActivityMainBinding
import cn.lyric.getter.tool.ActivityTools.checkUpdate
import cn.lyric.getter.tool.ActivityTools.updateAppRules
import cn.lyric.getter.tool.Tools.activation
import cn.lyric.getter.ui.viewmodel.ShareViewModel


class MainActivity : AppCompatActivity() {
    private val shareViewModel: ShareViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareViewModel.activated = checkLSPosed()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.run {
            if (!shareViewModel.activated) {
                nav.visibility = View.GONE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.nav.setupWithNavController(findNavController(R.id.nav_host_fragment))
        checkUpdate()
        updateAppRules()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun checkLSPosed(): Boolean {
        return try {
            activation(this, "config")
            true
        } catch (_: Exception) {
            false
        }
    }

}