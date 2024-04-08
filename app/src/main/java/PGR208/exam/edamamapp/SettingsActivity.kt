package PGR208.exam.edamamapp

import PGR208.exam.edamamapp.Database_remainingCalories.RemainingCaloriesDao
import PGR208.exam.edamamapp.Database_remainingCalories.RemainingCaloriesEntity
import PGR208.exam.edamamapp.Database_settings.SettingsDao
import PGR208.exam.edamamapp.Database_settings.SettingsEntity
import PGR208.exam.edamamapp.databinding.ActivitySettingsBinding
import PGR208.exam.edamamapp.databinding.DialogDietTypeBinding
import PGR208.exam.edamamapp.databinding.DialogMealPriorityBinding
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private var binding: ActivitySettingsBinding? = null
    private var saveBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** Setting up binding */
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        /** Setting up database connection */
        val settingsDao = (application as DatabaseApp).dbSettings.settingsDao()
        val remainingCaloriesDao = (application as DatabaseApp).dbRemainingCalories.remainingCaloriesDao()

        /** Applying preferences from Database settings onto SettingsActivity's textViews/editTexts */
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
            settingsDao.fetchCalorieIntake().collect {
                binding?.etCalInput?.setText(it.toString())
            }
        }
        lifecycleScope.launch {
            settingsDao.fetchMaxSearchHistoryItems().collect {
                if (it != null) {
                    binding?.etHistoryItemsInput?.setText(it.toString())
                }
            }
        }

        /** Connecting Dialog functions to particular settings */
        val tvDropdownDiet = binding?.tvDropdownDiet
        tvDropdownDiet?.setOnClickListener {
            binding?.tvDropdownDiet?.let { it1 -> updateDesiredDietTypeDialog(it1) }
        }
        val tvDropdownPriority = binding?.tvDropdownPriority
        tvDropdownPriority?.setOnClickListener {
            binding?.tvDropdownPriority?.let { it1 -> updateMealPriorityDialog(it1) }
        }

        /** Applying Settings database-updating function onto Save Button */
        saveBtn = binding?.btnSave
        saveBtn?.setOnClickListener {
            binding?.etCalInput?.let { it1 ->
                binding?.etHistoryItemsInput?.let { it2 ->
                    binding?.tvDropdownDiet?.let { it3 ->
                        binding?.tvDropdownPriority?.let { it4 ->
                            binding?.btnSave?.let {
                                updateSettings(settingsDao, remainingCaloriesDao,
                                    it1, it2, it3, it4
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    /** Helping updating functions to be used in the main updating function */
    private fun updateCalorieIntake(editText: EditText): Int {
        return Integer.parseInt(editText.text.toString())
    }

    private fun updateMaxSearchHistoryItems(editText: EditText): Int {
        return Integer.parseInt(editText.text.toString())
    }

    private fun updateDesiredDietTypeDialog(textView: TextView): String {
        val updateDesiredDietDialog = Dialog(this)
        updateDesiredDietDialog.setCancelable(true)
        updateDesiredDietDialog.show()

        /** Setting up binding to Dialog's layout */
        val binding = DialogDietTypeBinding.inflate(layoutInflater)
        updateDesiredDietDialog.setContentView(binding?.root)
        updateDesiredDietDialog.setTitle("Diet type: ")

        /** Adding an onClick function to every possible Dialog's option */
        val balancedDietTv = binding.tvDietBalanced
        balancedDietTv.setOnClickListener {
            textView.text = "Balanced"
            updateDesiredDietDialog.dismiss()
        }
        val highFiberDietTv = binding.tvDietHighFiber
        highFiberDietTv.setOnClickListener {
            textView.text = "High-Fiber"
            updateDesiredDietDialog.dismiss()
        }
        val highProteinDietTv = binding.tvDietHighProtein
        highProteinDietTv.setOnClickListener {
            textView.text = "High-Protein"
            updateDesiredDietDialog.dismiss()
        }
        val lowCarbDietTv = binding.tvDietLowCarb
        lowCarbDietTv.setOnClickListener {
            textView.text = "Low-Carb"
            updateDesiredDietDialog.dismiss()
        }
        val lowFatDietTv = binding.tvDietLowFat
        lowFatDietTv.setOnClickListener {
            textView.text = "Low-Fat"
            updateDesiredDietDialog.dismiss()
        }
        val lowSodiumDietTv = binding.tvDietLowSodium
        lowSodiumDietTv.setOnClickListener {
            textView.text = "Low-sodium"
            updateDesiredDietDialog.dismiss()
        }
        return when (textView.text.toString()) {
            "Balanced" -> "balanced"
            "High-Fiber" -> "high-fiber"
            "High-Protein" -> "high-protein"
            "Low-Carb" -> "low-carb"
            "Low-Fat" -> "low-fat"
            "Low-Sodium" -> "low-sodium"

            else -> ""
        }
    }

    private fun updateMealPriorityDialog(textView: TextView): String {
        val updateMealPriorityDialog = Dialog(this)
        updateMealPriorityDialog.setCancelable(true)
        updateMealPriorityDialog.show()

        /** Setting up binding to Dialog's layout */
        val binding = DialogMealPriorityBinding.inflate(layoutInflater)
        updateMealPriorityDialog.setContentView(binding?.root)
        updateMealPriorityDialog.setTitle("Meal: ")

        /** Adding an onClick function to every possible Dialog's option */
        val breakfastTv = binding.tvMealBreakfast
        breakfastTv.setOnClickListener {
            textView.text = "Breakfast"
            updateMealPriorityDialog.dismiss()
        }
        val brunchTv = binding.tvMealBrunch
        brunchTv.setOnClickListener {
            textView.text = "Brunch"
            updateMealPriorityDialog.dismiss()
        }
        val lunchDinnerTv = binding.tvMealLunchDinner
        lunchDinnerTv.setOnClickListener {
            textView.text = "Lunch/Dinner"
            updateMealPriorityDialog.dismiss()
        }
        val snackTv = binding.tvMealSnack
        snackTv.setOnClickListener {
            textView.text = "Snack"
            updateMealPriorityDialog.dismiss()
        }
        val teatimeTv = binding.tvMealTeatime
        teatimeTv.setOnClickListener {
            textView.text = "Teatime"
            updateMealPriorityDialog.dismiss()
        }

        return when (textView.text.toString()) {
            "Breakfast" -> "Breakfast"
            "Brunch" -> "Brunch"
            "Lunch/Dinner" -> "Lunch/Dinner"
            "Snack" -> "Snack"
            "Teatime" -> "Teatime"

            else -> {
                ""
            }
        }
    }

    /** Main updating function that accesses the Settings Database */
    private fun updateSettings(settingsDao: SettingsDao, remainingCaloriesDao: RemainingCaloriesDao, editText1: EditText, editText2: EditText, textView1: TextView, textView2: TextView) {
            val calorieIntake = updateCalorieIntake(editText1)
            val maxSearchHistoryItems = updateMaxSearchHistoryItems(editText2)
            val desiredDiet = updateDesiredDietTypeDialog(textView1)
            val mealPriority = updateMealPriorityDialog(textView2)

            if (calorieIntake != null && maxSearchHistoryItems != null && desiredDiet != "" && mealPriority != "") {
                lifecycleScope.launch {
                    settingsDao.update(
                        SettingsEntity(
                            1,
                            calorieIntake,
                            maxSearchHistoryItems,
                            desiredDiet,
                            mealPriority
                        )
                    )
                    lifecycleScope.launch {
                        remainingCaloriesDao.update(RemainingCaloriesEntity(1, calorieIntake))
                        Toast.makeText(applicationContext, "Settings updated", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


}