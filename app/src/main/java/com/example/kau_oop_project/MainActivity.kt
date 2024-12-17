package com.example.kau_oop_project

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kau_oop_project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.postListFragment, R.id.mypageFragment, R.id.chatMainFragment, R.id.loginFragment, R.id.registerFragment)
        )

        val navController = binding.fragmentContainerView.getFragment<NavHostFragment>().navController
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(navController)

        // Navigation Graph Change Listener
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // fragmentLogin과 fragmentRegister일 때만 bottomNavigationView 숨기기
            if (destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) {
                binding.bottomNavigationView.visibility = View.GONE  // 숨기기
            } else {
                binding.bottomNavigationView.visibility = View.VISIBLE  // 보이기
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = binding.fragmentContainerView.getFragment<NavHostFragment>().navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}