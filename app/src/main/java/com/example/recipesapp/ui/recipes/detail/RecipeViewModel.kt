package com.example.recipesapp.ui.recipes.detail

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.Recipe
import java.io.InputStream
import java.util.concurrent.Executors

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val recipesRepository = RecipesRepository()

    private val executor = Executors.newSingleThreadExecutor()

    private val _state = MutableLiveData(RecipeState())
    val state: LiveData<RecipeState>
        get() = _state

    data class RecipeState(
        val recipe: Recipe? = null,
        val portionCount: Int = 1,
        val isFavorite: Boolean = false,
        val recipeImage: Drawable? = null,
    )

    fun loadRecipe(recipeId: Int) {
        executor.execute {
            val recipe = recipesRepository.getRecipeById(recipeId)
            val favorites = getFavorites()

            if (recipe != null) {
                val isFavorite = favorites.contains(recipeId.toString())
                val recipeImage = try {
                    val inputStream: InputStream = getApplication<Application>().assets.open(recipe.imageUrl)
                    val drawable = Drawable.createFromStream(inputStream, null)
                    inputStream.close()
                    drawable
                } catch (e: Exception) {
                    Log.e("RecipeViewModel", "Image not found ${recipe.imageUrl}", e)
                    null
                }

                Handler(Looper.getMainLooper()).post {
                    _state.value = _state.value?.copy(
                        recipe = recipe,
                        isFavorite = isFavorite,
                        recipeImage = recipeImage,
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

    private fun getFavorites(): MutableSet<String> {
        val sharedPrefs =
            getApplication<Application>().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val favorites =
            sharedPrefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()

        return HashSet(favorites)
    }

    private fun saveFavorites(favorites: Set<String>) {
        val sharedPrefs =
            getApplication<Application>().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().putStringSet(FAVORITES_KEY, favorites).apply()
    }

    fun onFavoritesClicked() {
        val recipeId = _state.value?.recipe?.id ?: return
        val favorites = getFavorites().toMutableSet()
        val isCurrentlyFavorite = state.value?.isFavorite ?: false

        if (isCurrentlyFavorite) {
            favorites.remove(recipeId.toString())
        } else {
            favorites.add(recipeId.toString())
        }

        saveFavorites(favorites)
        _state.value = _state.value?.copy(isFavorite = !isCurrentlyFavorite)
    }

    fun updatePortionCount(portionCount: Int) {
        _state.value = _state.value?.copy(portionCount = portionCount)
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }

    companion object {
        private const val SHARED_PREFS_NAME = "favorite_recipes_prefs"
        private const val FAVORITES_KEY = "favorites_recipes"
    }

}