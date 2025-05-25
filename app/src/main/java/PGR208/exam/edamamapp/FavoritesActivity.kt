package PGR208.exam.edamamapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import PGR208.exam.edamamapp.databinding.ActivityFavoritesBinding
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {
    private var binding: ActivityFavoritesBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val favoritesDao = (application as DatabaseApp).dbFavorites.favoritesDao()

        binding?.rvFavorites?.layoutManager = LinearLayoutManager(this)

        val adapter = FavoritesAdapter(
            context = this@FavoritesActivity,
            favoritesDao = favoritesDao
        )
        binding?.rvFavorites?.adapter = adapter

        // Observe favorites
        lifecycleScope.launch {
            favoritesDao.getAllFavorites().let { favorites ->
                adapter.updateData(favorites)
            }
        }

        // Clear favorites button logic
        binding?.btnClearFavorites?.setOnClickListener {
            lifecycleScope.launch {
                favoritesDao.deleteAll()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}