package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.DietPlannerRepository
import com.example.ui.DietPlannerDashboard
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.DietPlannerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Core data layer initialization
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = DietPlannerRepository(database.dietPlannerDao())
        
        val viewModelFactory = DietPlannerViewModel.Factory(repository, applicationContext)
        val viewModel = ViewModelProvider(this, viewModelFactory)[DietPlannerViewModel::class.java]

        // Load theme configurations from persistent SharedPreferences
        val sharedPrefs = getSharedPreferences("niljori_settings", MODE_PRIVATE)
        val isSavedDark = sharedPrefs.getBoolean("dark_mode", false)
        viewModel.setInitialDarkTheme(isSavedDark)

        val savedTargetWeight = sharedPrefs.getFloat("target_weight", 0.0f)
        viewModel.setInitialTargetWeight(savedTargetWeight.toDouble())

        setContent {
            val isDarkTheme = viewModel.isDarkTheme.collectAsState().value

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DietPlannerDashboard(viewModel = viewModel)
                }
            }
        }
    }
}
