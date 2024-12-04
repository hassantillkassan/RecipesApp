package com.example.recipesapp.ui.recipes.list

import android.app.Application
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.Recipe
import java.io.InputStream
import java.util.concurrent.Executors

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {

    private val recipesRepository = RecipesRepository()

    private val executor = Executors.newSingleThreadExecutor()

    private val _state = MutableLiveData(RecipesListState())
    val state: LiveData<RecipesListState>
        get() = _state

    data class RecipesListState(
        val recipes: List<Recipe> = emptyList(),
        val categoryName: String = "",
        val categoryImage: Drawable? = null,
    )

    fun loadRecipe(category: Category) {
        executor.execute {
            val recipes = recipesRepository.getRecipesByCategoryId(category.id)

            val drawable = try {
                val inputStream: InputStream? = getApplication<Application>().assets?.open(category.imageUrl)
                val image = Drawable.createFromStream(inputStream, null)
                inputStream?.close()
                image
            } catch (e: Exception) {
                Log.e("RecipesListFragment", "Image not found ${category.imageUrl}", e)
                null
            }

            if (recipes != null) {
                Handler(Looper.getMainLooper()).post {
                    _state.value = _state.value?.copy(
                        recipes = recipes,
                        categoryName = category.title,
                        categoryImage = drawable,
                    )
                }
            } else {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        getApplication(),
                        "Ошибка получения данных",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }
}