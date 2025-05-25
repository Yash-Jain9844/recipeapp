package PGR208.exam.edamamapp

import PGR208.exam.edamamapp.Database_settings.SettingsDao
import PGR208.exam.edamamapp.Database_settings.SettingsEntity
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import PGR208.exam.edamamapp.databinding.ActivitySettingsBinding
import PGR208.exam.edamamapp.databinding.DialogDietTypeBinding
import PGR208.exam.edamamapp.databinding.DialogMealPriorityBinding
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private var binding: ActivitySettingsBinding? = null
    private var saveBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()

        lifecycleScope.launch {
            settingsDao.fetchDesiredDiet().collect {
                if (it != null) {
                    binding?.tvDropdownDiet?.text = it
                }
            }
        }
        lifecycleScope.launch {
            settingsDao.fetchMealPriority().collect {
                if (it != null) {
                    binding?.tvDropdownPriority?.text = it
                }
            }
        }
        lifecycleScope.launch {
            settingsDao.fetchMaxSearchHistoryItems().collect {
                if (it != null) {
                    binding?.etHistoryItemsInput?.setText(it.toString())
                }
            }
        }

        val tvDropdownDiet = binding?.tvDropdownDiet
        tvDropdownDiet?.setOnClickListener {
            binding?.tvDropdownDiet?.let { updateDesiredDietTypeDialog(it) }
        }
        val tvDropdownPriority = binding?.tvDropdownPriority
        tvDropdownPriority?.setOnClickListener {
            binding?.tvDropdownPriority?.let { updateMealPriorityDialog(it) }
        }

        saveBtn = binding?.btnSave
        saveBtn?.setOnClickListener {
            binding?.etHistoryItemsInput?.let { etHistory ->
                binding?.tvDropdownDiet?.let { tvDiet ->
                    binding?.tvDropdownPriority?.let { tvPriority ->
                        updateSettings(settingsDao, etHistory, tvDiet, tvPriority)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun updateMaxSearchHistoryItems(editText: EditText): Int {
        return Integer.parseInt(editText.text.toString())
    }

    private fun updateDesiredDietTypeDialog(textView: TextView) {
        val updateDesiredDietDialog = Dialog(this)
        updateDesiredDietDialog.setCancelable(true)
        updateDesiredDietDialog.show()
        val binding = DialogDietTypeBinding.inflate(layoutInflater)
        updateDesiredDietDialog.setContentView(binding.root)
        updateDesiredDietDialog.setTitle("Diet type: ")

        val categories = listOf("Beef", "Chicken", "Seafood", "Vegetarian", "Dessert") // Example categories from TheMealDB
        // Update dialog_diet_type.xml to match these categories
        binding.tvDietBalanced.text = categories[0]
        binding.tvDietHighFiber.text = categories[1]
        binding.tvDietHighProtein.text = categories[2]
        binding.tvDietLowCarb.text = categories[3]
        binding.tvDietLowFat.text = categories[4]


        binding.tvDietBalanced.setOnClickListener {
            textView.text = categories[0]
            updateDesiredDietDialog.dismiss()
        }
        binding.tvDietHighFiber.setOnClickListener {
            textView.text = categories[1]
            updateDesiredDietDialog.dismiss()
        }
        binding.tvDietHighProtein.setOnClickListener {
            textView.text = categories[2]
            updateDesiredDietDialog.dismiss()
        }
        binding.tvDietLowCarb.setOnClickListener {
            textView.text = categories[3]
            updateDesiredDietDialog.dismiss()
        }
        binding.tvDietLowFat.setOnClickListener {
            textView.text = categories[4]
            updateDesiredDietDialog.dismiss()
        }

    }

    private fun updateMealPriorityDialog(textView: TextView) {
        val updateMealPriorityDialog = Dialog(this)
        updateMealPriorityDialog.setCancelable(true)
        updateMealPriorityDialog.show()
        val binding = DialogMealPriorityBinding.inflate(layoutInflater)
        updateMealPriorityDialog.setContentView(binding.root)
        updateMealPriorityDialog.setTitle("Meal: ")

        binding.tvMealBreakfast.setOnClickListener {
            textView.text = "Breakfast"
            updateMealPriorityDialog.dismiss()
        }
        binding.tvMealBrunch.setOnClickListener {
            textView.text = "Brunch"
            updateMealPriorityDialog.dismiss()
        }
        binding.tvMealLunchDinner.setOnClickListener {
            textView.text = "Lunch/Dinner"
            updateMealPriorityDialog.dismiss()
        }
        binding.tvMealSnack.setOnClickListener {
            textView.text = "Snack"
            updateMealPriorityDialog.dismiss()
        }
        binding.tvMealTeatime.setOnClickListener {
            textView.text = "Teatime"
            updateMealPriorityDialog.dismiss()
        }

    }

    private fun updateSettings(settingsDao: SettingsDao, editText: EditText, textView1: TextView, textView2: TextView) {
        val maxSearchHistoryItems = updateMaxSearchHistoryItems(editText)
        val desiredDiet = textView1.text.toString()
        val mealPriority = textView2.text.toString()

        if (maxSearchHistoryItems > 0 && desiredDiet.isNotEmpty() && mealPriority.isNotEmpty()) {
            lifecycleScope.launch {
                settingsDao.update(
                    SettingsEntity(
                        id = 1,
                        maxSearchHistoryItems = maxSearchHistoryItems,
                        desiredDiet = desiredDiet,
                        mealPriority = mealPriority
                    )
                )
                Toast.makeText(applicationContext, "Settings updated", Toast.LENGTH_LONG).show()
            }
        }
    }

}