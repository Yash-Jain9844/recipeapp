package PGR208.exam.edamamapp

import PGR208.exam.edamamapp.Database_Favorites.FavoritesDao
import PGR208.exam.edamamapp.Database_Favorites.FavoritesEntity
import PGR208.exam.edamamapp.Database_remainingCalories.RemainingCaloriesDao
import PGR208.exam.edamamapp.Database_remainingCalories.RemainingCaloriesEntity
import PGR208.exam.edamamapp.Database_searchHistory.SearchHistoryEntity
import PGR208.exam.edamamapp.Database_settings.SettingsDao
import PGR208.exam.edamamapp.databinding.RecipeItemBinding
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchHistoryAdapter(private val searchHistoryEntityList: MutableList<SearchHistoryEntity>, val context: Context, private val favoritesDao: FavoritesDao, private val settingsDao: SettingsDao, private val remainingCaloriesDao: RemainingCaloriesDao): RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder>(){

    inner class SearchHistoryViewHolder(private val itemBinding: RecipeItemBinding): RecyclerView.ViewHolder(itemBinding.root){

        val ibFavorite = itemBinding.ibFavorite
        val btnSelectRecipe = itemBinding.btnSelectRecipe

        fun bindItem(searchHistoryEntity: SearchHistoryEntity){
            Glide.with(context).load(searchHistoryEntity.image).into(itemBinding.ivDish)
            itemBinding.tvTitle.text = searchHistoryEntity.label
            itemBinding.tvDietLabel1.text = searchHistoryEntity.dietLabel
            itemBinding.tvHealthLabel1.text = searchHistoryEntity.healthLabel
            itemBinding.tvMealLabel.text = searchHistoryEntity.mealType
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        return SearchHistoryViewHolder(RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        val searchHistoryEntity = searchHistoryEntityList[position]
        holder.bindItem(searchHistoryEntity)

        /** Function to add or remove recipe's label to/from Favorites database */
        holder.ibFavorite.setOnClickListener{
            if(holder.ibFavorite.background.equals(R.color.main_screen_content_background)) {
                GlobalScope.launch {
                    favoritesDao.insert(FavoritesEntity(0, searchHistoryEntity.label))
                    holder.ibFavorite.setBackgroundColor(R.color.red)
                }
            }else if(holder.ibFavorite.background.equals(R.color.red)){
                GlobalScope.launch {
                    favoritesDao.delete(FavoritesEntity(0, searchHistoryEntity.label))
                    holder.ibFavorite.setBackgroundColor(R.color.main_screen_content_background)
                }
            }
        }

        /** Variable to be used in fetching daily calorie intake */
        var maxCalories = 0

        /** Function to open the recipe's webpage in browser and to deduct recipe's calories from daily calorie intake */
        holder.btnSelectRecipe.setOnClickListener {

            GlobalScope.launch {
                /** Deduction from remaining daily calorie intake */
                remainingCaloriesDao.fetchRemainingCalories().collect{
                    maxCalories = it
                    val singlePortion: Int = searchHistoryEntity.calories.toInt() / searchHistoryEntity.yield
                    remainingCaloriesDao.update(RemainingCaloriesEntity(1, maxCalories-singlePortion))
                    /** Opening recipe's webpage in browser */
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(searchHistoryEntity.url))
                    context.startActivity(browserIntent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return searchHistoryEntityList.size
    }
}