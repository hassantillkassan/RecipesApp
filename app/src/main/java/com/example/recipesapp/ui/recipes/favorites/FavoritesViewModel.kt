package com.example.recipesapp.ui.recipes.favorites

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.Recipe
import java.util.concurrent.Executors

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val recipesRepository = RecipesRepository()

    private val executor = Executors.newSingleThreadExecutor()

    private val _state = MutableLiveData(FavoritesState())
    val state: LiveData<FavoritesState>
        get() = _state

    data class FavoritesState(
        val recipes: List<Recipe> = emptyList(),
        val isEmpty: Boolean = true,
    )

    fun loadFavorites() {
        executor.execute{
            val favoritesIds = getFavorites()
            val favoriteRecipes = recipesRepository.getRecipesByIds(favoritesIds)

            if (favoriteRecipes != null) {
                Handler(Looper.getMainLooper()).post {
                    _state.value = _state.value?.copy(
                        recipes = favoriteRecipes,
                        isEmpty = favoriteRecipes.isEmpty(),
                    )
                }
            } else {
                Handler(Looper.getMainLooper()).post{
                    Toast.makeText(
                        getApplication(),
                        "Ошибка получения данных",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getFavorites(): Set<Int> {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences("favorite_recipes_prefs", Context.MODE_PRIVATE)
        val favoriteIdsStringSet = sharedPreferences.getStringSet("favorites_recipes", emptySet()) ?: emptySet()

        return favoriteIdsStringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }
}