package PGR208.exam.edamamapp

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import PGR208.exam.edamamapp.databinding.ActivitySettingsBinding
import PGR208.exam.edamamapp.Database_settings.SettingsEntity

// Activity for managing app settings such as diet, meal priority, and theme
class SettingsActivity : AppCompatActivity() {
    // View binding for the settings layout
    private var binding: ActivitySettingsBinding? = null

    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using view binding
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Access settings DAO
        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()

        // Set up theme spinner with options
        val themeOptions = arrayOf("Light", "Dark", "System")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spinnerTheme?.adapter = adapter

        // Load current settings from database
        lifecycleScope.launch {
            // Set desired diet
            settingsDao.fetchDesiredDietOnce()?.let {
                binding?.etDesiredDiet?.setText(it)
            }
            // Set meal priority
            settingsDao.fetchMealPriorityOnce()?.let {
                binding?.etMealPriority?.setText(it)
            }
            // Set max search history items
            settingsDao.fetchMaxSearchHistoryItems()?.collect { maxItems ->
                binding?.etMaxSearchHistoryItems?.setText(maxItems.toString())
            }
            // Set theme preference
            settingsDao.fetchThemePreference()?.let { theme ->
                val position = when (theme) {
                    "light" -> 0
                    "dark" -> 1
                    else -> 2
                }
                binding?.spinnerTheme?.setSelection(position)
            }
        }

        // Set up save settings button
        binding?.btnSaveSettings?.setOnClickListener {
            // Get user input
            val desiredDiet = binding?.etDesiredDiet?.text.toString()
            val mealPriority = binding?.etMealPriority?.text.toString()
            val maxSearchHistoryItems = binding?.etMaxSearchHistoryItems?.text.toString().toIntOrNull() ?: 10
            val themePreference = when (binding?.spinnerTheme?.selectedItem.toString()) {
                "Light" -> "light"
                "Dark" -> "dark"
                else -> "system"
            }

            // Save settings to database
            lifecycleScope.launch {
                settingsDao.insert(
                    SettingsEntity(
                        desiredDiet = desiredDiet,
                        mealPriority = mealPriority,
                        maxSearchHistoryItems = maxSearchHistoryItems,
                        themePreference = themePreference
                    )
                )
                // Apply theme immediately
                applyTheme(themePreference)
                // Recreate activity to reflect theme changes
                recreate()
            }
        }
    }

    // Apply the selected theme
    private fun applyTheme(themePreference: String) {
        when (themePreference) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    // Clean up resources when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}