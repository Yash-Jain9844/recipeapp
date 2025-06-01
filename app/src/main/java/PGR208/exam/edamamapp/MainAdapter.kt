package PGR208.exam.edamamapp

import PGR208.exam.edamamapp.Database_Favorites.FavoritesDao
import PGR208.exam.edamamapp.Database_Favorites.FavoritesEntity
import PGR208.exam.edamamapp.Database_settings.SettingsDao
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import PGR208.exam.edamamapp.models.Meal
import PGR208.exam.edamamapp.databinding.RecipeItemBinding
import android.annotation.SuppressLint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// Adapter for displaying search results in a RecyclerView
class MainAdapter(
    private val mealsList: MutableList<Meal>, // List of meals to display
    val context: Context, // Context for starting intents and loading images
    val favoritesDao: FavoritesDao, // DAO for favorites database operations
    val settingsDao: SettingsDao // DAO for settings database operations
) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    // ViewHolder for individual meal items
    inner class MainViewHolder(private val itemBinding: RecipeItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        // References to UI elements
        val ibFavorite = itemBinding.ibFavorite
        val btnSelectRecipe = itemBinding.btnSelectRecipe
        val ibShare = itemBinding.ibShare // Share button

        // Bind data to the ViewHolder
        fun bindItem(meal: Meal) {
            // Load meal image using Glide
            Glide.with(context).load(meal.strMealThumb).into(itemBinding.ivDish)
            // Set meal details
            itemBinding.tvTitle.text = meal.strMeal
            itemBinding.tvDietLabel1.text = meal.strCategory
            itemBinding.tvHealthLabel1.text = meal.strArea
            itemBinding.tvMealLabel.text = meal.strCategory

            // Check if meal is a favorite and set icon
            GlobalScope.launch {
                val isFavorite = favoritesDao.isFavorite(meal.strMeal)
                itemBinding.ibFavorite.setImageResource(
                    if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
                )
            }
        }
    }

    // Create a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // Bind data to the ViewHolder at the specified position
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val meal = mealsList[position]
        holder.bindItem(meal)

        // Handle favorite button click to add/remove from favorites
        holder.ibFavorite.setOnClickListener {
            val recipeLabel = meal.strMeal
            GlobalScope.launch {
                val isFav = favoritesDao.isFavorite(recipeLabel)
                if (!isFav) {
                    // Add meal to favorites
                    favoritesDao.insert(
                        FavoritesEntity(
                            label = recipeLabel,
                            image = meal.strMealThumb,
                            dietLabel = meal.strCategory,
                            healthLabel = meal.strArea,
                            mealType = meal.strCategory,
                            url = meal.strSource ?: ""
                        )
                    )
                    holder.ibFavorite.setImageResource(R.drawable.ic_favorite_filled)
                } else {
                    // Remove meal from favorites
                    favoritesDao.delete(FavoritesEntity(label = recipeLabel))
                    holder.ibFavorite.setImageResource(R.drawable.ic_favorite_border)
                }
            }
        }

        // Handle select recipe button to open recipe URL
        holder.btnSelectRecipe.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(meal.strSource ?: ""))
            context.startActivity(browserIntent)
        }

        // Handle share button click
        holder.ibShare.setOnClickListener {
            // Create share intent with meal details
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Check out this recipe: ${meal.strMeal}")
                putExtra(Intent.EXTRA_TEXT, "${meal.strMeal}\n${meal.strSource ?: ""}")
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share recipe"))
        }
    }

    // Return the number of items in the list
    override fun getItemCount(): Int {
        return mealsList.size
    }
}