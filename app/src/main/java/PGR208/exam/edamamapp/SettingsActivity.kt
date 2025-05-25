package PGR208.exam.edamamapp

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import PGR208.exam.edamamapp.databinding.ActivitySettingsBinding
import PGR208.exam.edamamapp.Database_settings.SettingsEntity

class SettingsActivity : AppCompatActivity() {
    private var binding: ActivitySettingsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()

        // Setup theme spinner
        val themeOptions = arrayOf("Light", "Dark", "System")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spinnerTheme?.adapter = adapter

        // Load current settings
        lifecycleScope.launch {
            settingsDao.fetchDesiredDietOnce()?.let {
                binding?.etDesiredDiet?.setText(it)
            }
            settingsDao.fetchMealPriorityOnce()?.let {
                binding?.etMealPriority?.setText(it)
            }
            settingsDao.fetchMaxSearchHistoryItems()?.collect { maxItems ->
                binding?.etMaxSearchHistoryItems?.setText(maxItems.toString())
            }
            settingsDao.fetchThemePreference()?.let { theme ->
                val position = when (theme) {
                    "light" -> 0
                    "dark" -> 1
                    else -> 2
                }
                binding?.spinnerTheme?.setSelection(position)
            }
        }

        // Save settings
        binding?.btnSaveSettings?.setOnClickListener {
            val desiredDiet = binding?.etDesiredDiet?.text.toString()
            val mealPriority = binding?.etMealPriority?.text.toString()
            val maxSearchHistoryItems = binding?.etMaxSearchHistoryItems?.text.toString().toIntOrNull() ?: 10
            val themePreference = when (binding?.spinnerTheme?.selectedItem.toString()) {
                "Light" -> "light"
                "Dark" -> "dark"
                else -> "system"
            }

            lifecycleScope.launch {
                settingsDao.insert(
                    SettingsEntity(
                        desiredDiet = desiredDiet,
                        mealPriority = mealPriority,
                        maxSearchHistoryItems = maxSearchHistoryItems,
                        themePreference = themePreference
                    )
                )
                applyTheme(themePreference)
                finish()
            }
        }
    }

    private fun applyTheme(themePreference: String) {
        when (themePreference) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}