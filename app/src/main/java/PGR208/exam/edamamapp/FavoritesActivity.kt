package PGR208.exam.edamamapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import PGR208.exam.edamamapp.databinding.ActivityFavoritesBinding
import kotlinx.coroutines.launch

// Activity for displaying and managing favorite recipes
class FavoritesActivity : AppCompatActivity() {
    // View binding for the favorites activity layout
    private var binding: ActivityFavoritesBinding? = null
    // Adapter for the RecyclerView to display favorite recipes
    private var adapter: FavoritesAdapter? = null

    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using view binding
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Access the favorites DAO from the application database
        val favoritesDao = (application as DatabaseApp).dbFavorites.favoritesDao()

        // Set up RecyclerView with a linear layout manager
        binding?.rvFavorites?.layoutManager = LinearLayoutManager(this)

        // Initialize the FavoritesAdapter
        adapter = FavoritesAdapter(
            context = this@FavoritesActivity,
            favoritesDao = favoritesDao
        )
        binding?.rvFavorites?.adapter = adapter

        // Observe and display favorite recipes
        lifecycleScope.launch {
            favoritesDao.getAllFavorites().let { favorites ->
                adapter?.updateData(favorites)
            }
        }

        // Set up clear favorites button to delete all favorites
        binding?.btnClearFavorites?.setOnClickListener {
            lifecycleScope.launch {
                // Clear all favorites from the database
                favoritesDao.deleteAll()
                // Update the adapter with an empty list
                adapter?.updateData(emptyList())
            }
        }
    }

    // Clean up resources when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        binding = null
        adapter = null
    }
}