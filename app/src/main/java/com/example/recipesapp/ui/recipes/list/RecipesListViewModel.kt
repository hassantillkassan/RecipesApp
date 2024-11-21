package com.example.recipesapp.ui.recipes.list

import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipesapp.data.STUB
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

    fun loadRecipe(categoryId: Int, categoryName: String, categoryImageUrl: String) {
        val recipe = STUB.getRecipesByCategoryId(categoryId)

        val drawable = try {
            val inputStream: InputStream? = getApplication<Application>().assets?.open(categoryImageUrl)
            val image = Drawable.createFromStream(inputStream, null)
            inputStream?.close()
            image
        } catch (e: Exception) {
            Log.e("RecipesListFragment", "Image not found $categoryImageUrl", e)
            null
        }

        _state.value = _state.value?.copy(
            recipes = recipe,
            categoryName = categoryName,
            categoryImage = drawable,
        )
    }
}