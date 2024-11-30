package com.example.recipesapp.ui.recipes.list

import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipesapp.data.STUB
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.Recipe
import java.io.InputStream

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableLiveData(RecipesListState())
    val state: LiveData<RecipesListState>
        get() = _state

    data class RecipesListState(
        val recipes: List<Recipe> = emptyList(),
        val categoryName: String = "",
        val categoryImage: Drawable? = null,
    )

    fun loadRecipe(category: Category) {
        val recipe = STUB.getRecipesByCategoryId(category.id)

        val drawable = try {
            val inputStream: InputStream? = getApplication<Application>().assets?.open(category.imageUrl)
            val image = Drawable.createFromStream(inputStream, null)
            inputStream?.close()
            image
        } catch (e: Exception) {
            Log.e("RecipesListFragment", "Image not found ${category.imageUrl}", e)
            null
        }

        _state.value = _state.value?.copy(
            recipes = recipe,
            categoryName = category.title,
            categoryImage = drawable,
        )
    }
}