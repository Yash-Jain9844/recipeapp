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
import PGR208.exam.edamamapp.Database_searchHistory.SearchHistoryEntity
import PGR208.exam.edamamapp.Database_settings.SettingsDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// Adapter for displaying search history in a RecyclerView
class SearchHistoryAdapter(
    val context: Context, // Context for starting intents and loading images
    private val favoritesDao: FavoritesDao, // DAO for favorites database operations
    private val settingsDao: SettingsDao // DAO for settings database operations
) : RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder>() {

    // List to store search history entities
    private val searchHistoryEntityList = mutableListOf<SearchHistoryEntity>()

    // Update the adapter with a new list of search history entities
    fun updateData(newList: List<SearchHistoryEntity>) {
        searchHistoryEntityList.clear()
        searchHistoryEntityList.addAll(newList)
        notifyDataSetChanged()
    }

    // ViewHolder for individual search history items
    inner class SearchHistoryViewHolder(private val itemBinding: RecipeItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        // References to UI elements
        val ibFavorite = itemBinding.ibFavorite
        val btnSelectRecipe = itemBinding.btnSelectRecipe
        val ibShare = itemBinding.ibShare // Share button

        // Bind data to the ViewHolder
        fun bindItem(searchHistoryEntity: SearchHistoryEntity) {
            // Load image using Glide
            Glide.with(context).load(searchHistoryEntity.image).into(itemBinding.ivDish)
            // Set recipe details
            itemBinding.tvTitle.text = searchHistoryEntity.label
            itemBinding.tvDietLabel1.text = searchHistoryEntity.dietLabel
            itemBinding.tvHealthLabel1.text = searchHistoryEntity.healthLabel
            itemBinding.tvMealLabel.text = searchHistoryEntity.mealType

            // Check if item is a favorite and set icon
            GlobalScope.launch {
                val isFavorite = favoritesDao.isFavorite(searchHistoryEntity.label)
                itemBinding.ibFavorite.setImageResource(
                    if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
                )
            }

            // Handle select recipe button to open URL
            btnSelectRecipe.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(searchHistoryEntity.url))
                context.startActivity(browserIntent)
            }

            // Handle favorite button click to add/remove from favorites
            ibFavorite.setOnClickListener {
                GlobalScope.launch {
                    val entity = FavoritesEntity(
                        label = searchHistoryEntity.label,
                        image = searchHistoryEntity.image,
                        dietLabel = searchHistoryEntity.dietLabel,
                        healthLabel = searchHistoryEntity.healthLabel,
                        mealType = searchHistoryEntity.mealType,
                        url = searchHistoryEntity.url
                    )
                    val isFav = favoritesDao.isFavorite(searchHistoryEntity.label)
                    if (!isFav) {
                        // Add to favorites
                        favoritesDao.insert(entity)
                        ibFavorite.setImageResource(R.drawable.ic_favorite_filled)
                    } else {
                        // Remove from favorites
                        favoritesDao.delete(entity)
                        ibFavorite.setImageResource(R.drawable.ic_favorite_border)
                    }
                }
            }

            // Handle share button click
            ibShare.setOnClickListener {
                // Create share intent with recipe details
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Check out this recipe: ${searchHistoryEntity.label}")
                    putExtra(Intent.EXTRA_TEXT, "${searchHistoryEntity.label}\n${searchHistoryEntity.url}")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share recipe"))
            }
        }
    }

    // Create a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        return SearchHistoryViewHolder(
            RecipeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // Bind data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        holder.bindItem(searchHistoryEntityList[position])
    }

    // Return the number of items in the list
    override fun getItemCount(): Int = searchHistoryEntityList.size
}