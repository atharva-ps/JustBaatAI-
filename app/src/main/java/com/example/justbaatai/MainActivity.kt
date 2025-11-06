package com.example.justbaatai

import android.content.res.Configuration
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.justbaatai.databinding.ActivityMainBinding
import com.google.android.material.switchmaterial.SwitchMaterial
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbarAndNavigation()
        setupDrawerNavigation()
        setupOnBackPressed()
    }

    private fun setupToolbarAndNavigation() {
        setSupportActionBar(binding.appBarMain.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Only Home is the top-level destination now
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        // REMOVED: Bottom navigation setup
        // NavigationUI.setupWithNavController(binding.appBarMain.bottomNavView, navController)
    }

    private fun setupDrawerNavigation() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dark_theme -> {
                    // Dark theme is handled by switch, don't close drawer
                    false
                }
                R.id.nav_refer_earn -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    // TODO: Implement refer & earn functionality
                    Toast.makeText(this, "Refer & Earn - Coming Soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_language -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    // TODO: Show language selection dialog
                    Toast.makeText(this, "Language Selection - Coming Soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_support -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    // TODO: Navigate to support screen or open email
                    Toast.makeText(this, "Support - Coming Soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_about -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    // TODO: Navigate to about screen
                    Toast.makeText(this, "About Us - Coming Soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    // For all other items, use navigation component
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                }
            }
        }

        // Dark theme switch setup
        val darkThemeMenuItem = binding.navView.menu.findItem(R.id.nav_dark_theme)
        val switch = darkThemeMenuItem.actionView as SwitchMaterial
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        switch.isChecked = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        switch.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        // Profile button in header
        val headerView = binding.navView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.view_profile_button)?.setOnClickListener {
            binding.drawerLayout.closeDrawers()
            navController.navigate(R.id.nav_profile)
        }
    }


    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    // Let the NavController handle the back press
                    if (!navController.navigateUp(appBarConfiguration)) {
                        finish()
                    }
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
