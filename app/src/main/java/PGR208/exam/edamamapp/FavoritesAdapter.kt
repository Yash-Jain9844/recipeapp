package PGR208.exam.edamamapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import PGR208.exam.edamamapp.databinding.RecipeItemBinding
import PGR208.exam.edamamapp.Database_Favorites.FavoritesDao
import PGR208.exam.edamamapp.Database_Favorites.FavoritesEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// Adapter for displaying favorite recipes in a RecyclerView
class FavoritesAdapter(
    private val context: Context, // Context for starting intents and loading images
    private val favoritesDao: FavoritesDao // DAO for database operations
) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    // List to store favorite recipes
    private val favoritesList = mutableListOf<FavoritesEntity>()

    // Update the adapter with a new list of favorites
    fun updateData(newList: List<FavoritesEntity>) {
        favoritesList.clear()
        favoritesList.addAll(newList)
        notifyDataSetChanged()
    }

    // ViewHolder for individual favorite recipe items
    inner class FavoritesViewHolder(private val itemBinding: RecipeItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        // References to UI elements
        val ibFavorite = itemBinding.ibFavorite
        val btnSelectRecipe = itemBinding.btnSelectRecipe
        val ibShare = itemBinding.ibShare // Share button

        // Bind data to the ViewHolder
        fun bindItem(favorite: FavoritesEntity) {
            // Load recipe image using Glide
            Glide.with(context).load(favorite.image).into(itemBinding.ivDish)
            // Set recipe details
            itemBinding.tvTitle.text = favorite.label
            itemBinding.tvDietLabel1.text = favorite.dietLabel
            itemBinding.tvHealthLabel1.text = favorite.healthLabel
            itemBinding.tvMealLabel.text = favorite.mealType
            // Set favorite icon to filled state
            itemBinding.ibFavorite.setImageResource(R.drawable.ic_favorite_filled)

            // Enable select button only if URL is available
            btnSelectRecipe.isEnabled = favorite.url.isNotEmpty()
            btnSelectRecipe.visibility = ViewGroup.VISIBLE
            btnSelectRecipe.setOnClickListener {
                if (favorite.url.isNotEmpty()) {
                    // Open recipe URL in browser
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(favorite.url))
                    context.startActivity(browserIntent)
                }
            }

            // Handle favorite button click to remove from favorites
            ibFavorite.setOnClickListener {
                GlobalScope.launch {
                    favoritesDao.delete(favorite)
                    // Update favorite icon to unfilled state
                    itemBinding.ibFavorite.setImageResource(R.drawable.ic_favorite_border)
                }
            }

            // Handle share button click
            ibShare.setOnClickListener {
                // Create share intent with recipe details
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Check out this recipe: ${favorite.label}")
                    putExtra(Intent.EXTRA_TEXT, "${favorite.label}\n${favorite.url}")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share recipe"))
            }
        }
    }

    // Create a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        return FavoritesViewHolder(
            RecipeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // Bind data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bindItem(favoritesList[position])
    }

    // Return the number of items in the list
    override fun getItemCount(): Int = favoritesList.size
}