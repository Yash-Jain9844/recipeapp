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

class SearchHistoryAdapter(
    val context: Context,
    private val favoritesDao: FavoritesDao,
    private val settingsDao: SettingsDao
) : RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder>() {

    private val searchHistoryEntityList = mutableListOf<SearchHistoryEntity>()

    fun updateData(newList: List<SearchHistoryEntity>) {
        searchHistoryEntityList.clear()
        searchHistoryEntityList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class SearchHistoryViewHolder(private val itemBinding: RecipeItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        val ibFavorite = itemBinding.ibFavorite
        val btnSelectRecipe = itemBinding.btnSelectRecipe

        fun bindItem(searchHistoryEntity: SearchHistoryEntity) {
            Glide.with(context).load(searchHistoryEntity.image).into(itemBinding.ivDish)
            itemBinding.tvTitle.text = searchHistoryEntity.label
            itemBinding.tvDietLabel1.text = searchHistoryEntity.dietLabel
            itemBinding.tvHealthLabel1.text = searchHistoryEntity.healthLabel
            itemBinding.tvMealLabel.text = searchHistoryEntity.mealType

            btnSelectRecipe.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(searchHistoryEntity.url))
                context.startActivity(browserIntent)
            }

            ibFavorite.setOnClickListener {
                GlobalScope.launch {
                    val entity = FavoritesEntity(0, searchHistoryEntity.label)
                    val isFav = favoritesDao.isFavorite(searchHistoryEntity.label)
                    if (!isFav) {
                        favoritesDao.insert(entity)
                        ibFavorite.setColorFilter(android.graphics.Color.RED)
                    } else {
                        favoritesDao.delete(entity)
                        ibFavorite.setColorFilter(android.graphics.Color.GRAY)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        return SearchHistoryViewHolder(
            RecipeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        holder.bindItem(searchHistoryEntityList[position])
    }

    override fun getItemCount(): Int = searchHistoryEntityList.size
}
