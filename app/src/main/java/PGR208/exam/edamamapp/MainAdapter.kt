package PGR208.exam.edamamapp


import PGR208.exam.edamamapp.Database_Favorites.FavoritesDao
import PGR208.exam.edamamapp.Database_Favorites.FavoritesEntity
import PGR208.exam.edamamapp.Database_remainingCalories.RemainingCaloriesDao
import PGR208.exam.edamamapp.Database_remainingCalories.RemainingCaloriesEntity
import PGR208.exam.edamamapp.Database_settings.SettingsDao
import PGR208.exam.edamamapp.Database_settings.SettingsEntity
import PGR208.exam.edamamapp.databinding.RecipeItemBinding
import PGR208.exam.edamamapp.models.Hit
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
import kotlinx.coroutines.launch


class MainAdapter(private val recipeResponseHitsList: MutableList<Hit>, val context: Context, val favoritesDao: FavoritesDao, val settingsDao: SettingsDao, val remainingCaloriesDao: RemainingCaloriesDao): RecyclerView.Adapter<MainAdapter.MainViewHolder>(){

    inner class MainViewHolder(private val itemBinding: RecipeItemBinding): RecyclerView.ViewHolder(itemBinding.root){

        val ibFavorite = itemBinding.ibFavorite
        val btnSelectRecipe = itemBinding.btnSelectRecipe


        fun bindItem(hit: Hit){
            /** Glide library used to upload the recipe's image from url */
                Glide.with(context).load(hit.recipe.image).into(itemBinding.ivDish)

                itemBinding.tvTitle.text = hit.recipe.label
                Log.e("Recipe name", hit.recipe.label)
                itemBinding.tvMealLabel.text = hit.recipe.mealType[0]
                itemBinding.tvDietLabel1.text = hit.recipe.dietLabels[0]
                itemBinding.tvHealthLabel1.text = hit.recipe.healthLabels[0]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val hit = recipeResponseHitsList[position]
        holder.bindItem(hit)

        /** Function to add or remove recipe's label to/from Favorites database */
        holder.ibFavorite.setOnClickListener{
            if(holder.ibFavorite.background.equals(R.color.main_screen_content_background)) {
                GlobalScope.launch {
                    favoritesDao.insert(FavoritesEntity(0, hit.recipe.label))
                    holder.ibFavorite.setBackgroundColor(R.color.red)
                }
            }else if(holder.ibFavorite.background.equals(R.color.red)){
                GlobalScope.launch {
                    favoritesDao.delete(FavoritesEntity(0, hit.recipe.label))
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
                    remainingCaloriesDao.fetchRemainingCalories().collect {
                        maxCalories = it
                        val singlePortion: Int = hit.recipe.calories.toInt() / hit.recipe.yield
                        remainingCaloriesDao.update(RemainingCaloriesEntity(1, maxCalories-singlePortion))
                        /** Opening recipe's webpage in browser */
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(hit.recipe.url))
                        context.startActivity(browserIntent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return recipeResponseHitsList.size
    }



}