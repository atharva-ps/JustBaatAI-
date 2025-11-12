package com.justbaat.mindoro

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
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
import com.justbaat.mindoro.catprofile.SharedViewModel
import com.justbaat.mindoro.databinding.ActivityMainBinding
import com.google.android.material.switchmaterial.SwitchMaterial
import com.justbaat.mindoro.utils.ThemeManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    // Add ViewModel with correct import
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbarAndNavigation()
        setupDrawerNavigation()
        setupNavigationDrawerHeader()
        setupOnBackPressed()
    }

    private fun setupToolbarAndNavigation() {
        setSupportActionBar(binding.appBarMain.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupDrawerNavigation() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dark_theme -> {
                    // Theme toggle is handled by switch, don't close drawer
                    false
                }
                R.id.nav_refer_earn -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    Toast.makeText(this, "Refer & Earn - Coming Soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_language -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    Toast.makeText(this, "Language Selection - Coming Soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_support -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    Toast.makeText(this, "Support - Coming Soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_about -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    Toast.makeText(this, "About Us - Coming Soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                }
            }
        }

        // Setup Dark Theme Switch with persistence
        setupDarkThemeSwitch()

        // Profile button in header
        val headerView = binding.navView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.view_profile_button)?.setOnClickListener {
            binding.drawerLayout.closeDrawers()
            navController.navigate(R.id.nav_profile)
        }
    }

    private fun setupDarkThemeSwitch() {
        val darkThemeMenuItem = binding.navView.menu.findItem(R.id.nav_dark_theme)
        val themeSwitch = darkThemeMenuItem.actionView as? SwitchMaterial

        themeSwitch?.let { switch ->
            // Set initial state from saved preference
            switch.isChecked = ThemeManager.isDarkThemeOn(this)

            // Remove previous listeners to avoid multiple triggers
            switch.setOnCheckedChangeListener(null)

            // Set new listener
            switch.setOnCheckedChangeListener { _, isChecked ->
                val newTheme = if (isChecked) {
                    ThemeManager.THEME_DARK
                } else {
                    ThemeManager.THEME_LIGHT
                }

                // Save preference and apply theme
                ThemeManager.saveThemeMode(this, newTheme)
            }
        }
    }


    private fun setupNavigationDrawerHeader() {
        val headerView = binding.navView.getHeaderView(0)

        val headerInitials = headerView.findViewById<TextView>(R.id.header_initials)
        val headerUserName = headerView.findViewById<TextView>(R.id.header_user_name)
        val headerUserEmail = headerView.findViewById<TextView>(R.id.header_user_email)
        val headerUserMobile = headerView.findViewById<TextView>(R.id.header_user_mobile)
        val mobileContainer = headerView.findViewById<View>(R.id.mobile_container)
        val viewProfileButton = headerView.findViewById<TextView>(R.id.view_profile_button)

        // Observe profile changes
        sharedViewModel.userProfile.observe(this) { profile ->
            // Update initials
            val initials = sharedViewModel.getInitials(profile.userName)
            headerInitials?.text = initials

            // Update name
            headerUserName?.text = profile.userName

            // Update email
            if (profile.userEmail.isNotEmpty()) {
                headerUserEmail?.text = profile.userEmail
                headerUserEmail?.visibility = View.VISIBLE
            } else {
                headerUserEmail?.visibility = View.GONE
            }

            // Update mobile
            if (profile.userMobile.isNotEmpty()) {
                headerUserMobile?.text = profile.userMobile
                mobileContainer?.visibility = View.VISIBLE
            } else {
                mobileContainer?.visibility = View.GONE
            }
        }

        viewProfileButton?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(R.id.nav_profile)
        }
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
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
