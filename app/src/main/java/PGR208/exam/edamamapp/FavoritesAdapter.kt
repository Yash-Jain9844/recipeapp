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

class FavoritesAdapter(
    private val context: Context,
    private val favoritesDao: FavoritesDao
) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    private val favoritesList = mutableListOf<FavoritesEntity>()

    fun updateData(newList: List<FavoritesEntity>) {
        favoritesList.clear()
        favoritesList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class FavoritesViewHolder(private val itemBinding: RecipeItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        val ibFavorite = itemBinding.ibFavorite
        val btnSelectRecipe = itemBinding.btnSelectRecipe

        fun bindItem(favorite: FavoritesEntity) {
            Glide.with(context).load(favorite.image).into(itemBinding.ivDish)
            itemBinding.tvTitle.text = favorite.label
            itemBinding.tvDietLabel1.text = favorite.dietLabel
            itemBinding.tvHealthLabel1.text = favorite.healthLabel
            itemBinding.tvMealLabel.text = favorite.mealType
            itemBinding.ibFavorite.setImageResource(R.drawable.ic_favorite_filled)

            btnSelectRecipe.isEnabled = favorite.url.isNotEmpty()
            btnSelectRecipe.visibility = ViewGroup.VISIBLE
            btnSelectRecipe.setOnClickListener {
                if (favorite.url.isNotEmpty()) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(favorite.url))
                    context.startActivity(browserIntent)
                }
            }

            ibFavorite.setOnClickListener {
                GlobalScope.launch {
                    favoritesDao.delete(favorite)
                    itemBinding.ibFavorite.setImageResource(R.drawable.ic_favorite_border)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        return FavoritesViewHolder(
            RecipeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bindItem(favoritesList[position])
    }

    override fun getItemCount(): Int = favoritesList.size
}