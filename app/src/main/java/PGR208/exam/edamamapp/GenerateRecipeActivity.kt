package PGR208.exam.edamamapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import PGR208.exam.edamamapp.databinding.ActivityGenerateRecipeBinding

interface CohereService {
    @POST("v1/generate")
    fun generate(
        @Header("Authorization") auth: String,
        @Body request: GenerateRequest
    ): Call<GenerateResponse>
}

data class GenerateRequest(
    val prompt: String,
    val model: String = "command-r-plus",
    val max_tokens: Int = 500,
    val temperature: Double = 0.3,
    val k: Int = 0,
    val p: Double = 0.75,
    val frequency_penalty: Double? = null,
    val presence_penalty: Double? = null
)

data class GenerateResponse(
    val generations: List<Generation>?,
    val message: String?
)

data class Generation(
    val text: String
)

class GenerateRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGenerateRecipeBinding
    private val TAG = "GenerateRecipeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerateRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set ActionBar title
        supportActionBar?.title = "Edamam App"

        binding.btnGenerate.setOnClickListener {
            val ingredients = binding.etIngredients.text.toString().trim()
            if (ingredients.isNotEmpty()) {
                generateRecipe(ingredients)
            } else {
                Toast.makeText(this, "Please enter ingredients", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etIngredients.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                actionId == android.view.inputmethod.EditorInfo.IME_ACTION_GO) {
                val ingredients = binding.etIngredients.text.toString().trim()
                if (ingredients.isNotEmpty()) {
                    generateRecipe(ingredients)
                    true
                } else {
                    Toast.makeText(this, "Please enter ingredients", Toast.LENGTH_SHORT).show()
                    false
                }
            } else {
                false
            }
        }
    }

    override fun onBackPressed() {
        if (binding.resultContainer.visibility == View.VISIBLE) {
            resetUI()
        } else {
            super.onBackPressed() // Navigate to MainActivity
        }
    }

    private fun resetUI() {
        binding.etIngredients.text.clear()
        binding.tvTitle.text = ""
        binding.tvIngredients.text = ""
        binding.tvSteps.text = ""
        binding.inputContainer.visibility = View.VISIBLE
        binding.resultContainer.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        // Show keyboard and focus on EditText
        binding.etIngredients.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etIngredients, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun generateRecipe(ingredients: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvTitle.text = ""
        binding.tvIngredients.text = ""
        binding.tvSteps.text = ""

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etIngredients.windowToken, 0)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.cohere.ai/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CohereService::class.java)

        val prompt = """
            Create a creative and delicious recipe using only these ingredients: $ingredients. 
            Format the recipe with the following sections:
            Title: [Recipe Name]
            Ingredients: [List each ingredient with quantities]
            Steps: [Numbered steps to prepare the recipe]
        """.trimIndent()

        val request = GenerateRequest(
            prompt = prompt,
            model = "command-r-plus",
            max_tokens = 500,
            temperature = 0.3,
            k = 0,
            p = 0.75,
            frequency_penalty = null,
            presence_penalty = null
        )

        Log.d(TAG, "Sending request: $request")

        service.generate("Bearer P5W53s7LOnq5pFwY2LB9B7YxuOToGN828atIGytT", request).enqueue(object : Callback<GenerateResponse> {
            override fun onResponse(call: Call<GenerateResponse>, response: Response<GenerateResponse>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val text = response.body()?.generations?.firstOrNull()?.text ?: ""
                    Log.d(TAG, "Response text: $text")
                    val recipe = parseRecipe(text)
                    binding.tvTitle.text = recipe["Title"] ?: ""
                    binding.tvIngredients.text = recipe["Ingredients"] ?: ""
                    binding.tvSteps.text = recipe["Steps"] ?: text

                    binding.inputContainer.visibility = View.GONE
                    binding.resultContainer.visibility = View.VISIBLE
                } else {
                    val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: response.message()
                    Log.e(TAG, "API error: $errorMessage")
                    Toast.makeText(this@GenerateRecipeActivity, "API error: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<GenerateResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e(TAG, "Request failed: ${t.message}")
                Toast.makeText(this@GenerateRecipeActivity, "Failed to generate recipe: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun parseRecipe(text: String): Map<String, String> {
        val sections = listOf("Title:", "Ingredients:", "Steps:")
        val result = mutableMapOf<String, String>()
        var currentSection: String? = null
        val lines = text.split("\n")

        for (line in lines) {
            val trimmed = line.trim()
            if (sections.any { trimmed.startsWith(it) }) {
                currentSection = trimmed.substringBefore(":").trim()
                result[currentSection] = trimmed.substringAfter(":").trim()
            } else if (currentSection != null) {
                result[currentSection] = (result[currentSection] ?: "") + "\n" + trimmed
            }
        }
        return result
    }
}