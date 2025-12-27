package com.dicoding.moviecatalog

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.dicoding.moviecatalog.databinding.ActivityMainBinding
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val splitInstallManager by lazy {
        SplitInstallManagerFactory.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle window insets for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply padding untuk AppBar agar tidak tertutup status bar
            binding.appBarLayout.updatePadding(top = insets.top)

            // Apply padding untuk bottom navigation agar tidak tertutup navigation bar
            binding.bottomNavigation.updatePadding(bottom = insets.bottom)

            WindowInsetsCompat.CONSUMED
        }

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.favoriteFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        // Setup manual bottom navigation untuk handle dynamic feature module
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    if (navController.currentDestination?.id != R.id.homeFragment) {
                        navController.navigate(R.id.homeFragment)
                    }
                    true
                }
                R.id.favoriteFragment -> {
                    // Cek apakah module favorite sudah terinstall
                    if (splitInstallManager.installedModules.contains("favorite")) {
                        try {
                            if (navController.currentDestination?.id != R.id.favoriteFragment) {
                                navController.navigate(R.id.favoriteFragment)
                            }
                            true
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Error navigating to favorite: ${e.message}")
                            Toast.makeText(this, "Error opening favorites", Toast.LENGTH_SHORT).show()
                            false
                        }
                    } else {
                        // Install module favorite jika belum terinstall
                        installFavoriteModule()
                        false
                    }
                }
                else -> false
            }
        }

        // Update selected item ketika destination berubah
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> binding.bottomNavigation.selectedItemId = R.id.homeFragment
                R.id.favoriteFragment -> binding.bottomNavigation.selectedItemId = R.id.favoriteFragment
            }
        }
    }

    private fun installFavoriteModule() {
        val request = SplitInstallRequest.newBuilder()
            .addModule("favorite")
            .build()

        val listener = SplitInstallStateUpdatedListener { state ->
            when (state.status()) {
                SplitInstallSessionStatus.INSTALLED -> {
                    Toast.makeText(this, "Favorite module installed", Toast.LENGTH_SHORT).show()
                    // Navigate to favorite after installation
                    val navHostFragment = supportFragmentManager
                        .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    val navController = navHostFragment.navController
                    navController.navigate(R.id.favorite_nav_graph)
                }
                SplitInstallSessionStatus.INSTALLING -> {
                    Toast.makeText(this, "Installing favorite module...", Toast.LENGTH_SHORT).show()
                }
                SplitInstallSessionStatus.FAILED -> {
                    Toast.makeText(this, "Failed to install favorite module", Toast.LENGTH_SHORT).show()
                }
            }
        }

        splitInstallManager.registerListener(listener)
        splitInstallManager.startInstall(request)
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error installing module: ${exception.message}")
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}