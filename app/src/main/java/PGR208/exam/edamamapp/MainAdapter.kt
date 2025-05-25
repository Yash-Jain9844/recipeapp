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

class MainAdapter(
    private val mealsList: MutableList<Meal>,
    val context: Context,
    val favoritesDao: FavoritesDao,
    val settingsDao: SettingsDao
) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    inner class MainViewHolder(private val itemBinding: RecipeItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val ibFavorite = itemBinding.ibFavorite
        val btnSelectRecipe = itemBinding.btnSelectRecipe

        fun bindItem(meal: Meal) {
            Glide.with(context).load(meal.strMealThumb).into(itemBinding.ivDish)
            itemBinding.tvTitle.text = meal.strMeal
            itemBinding.tvDietLabel1.text = meal.strCategory
            itemBinding.tvHealthLabel1.text = meal.strArea
            itemBinding.tvMealLabel.text = meal.strCategory

            GlobalScope.launch {
                val isFavorite = favoritesDao.isFavorite(meal.strMeal)
                itemBinding.ibFavorite.setImageResource(
                    if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val meal = mealsList[position]
        holder.bindItem(meal)

        holder.ibFavorite.setOnClickListener {
            val recipeLabel = meal.strMeal

            GlobalScope.launch {
                val isFav = favoritesDao.isFavorite(recipeLabel)

                if (!isFav) {
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
                    favoritesDao.delete(FavoritesEntity(label = recipeLabel))
                    holder.ibFavorite.setImageResource(R.drawable.ic_favorite_border)
                }
            }
        }

        holder.btnSelectRecipe.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(meal.strSource ?: ""))
            context.startActivity(browserIntent)
        }
    }

    override fun getItemCount(): Int {
        return mealsList.size
    }
}