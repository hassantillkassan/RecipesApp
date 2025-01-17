package com.example.recipesapp.data

import android.util.Log
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.Recipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class RecipesRepository @Inject constructor(
    private val recipesDao: RecipesDao,
    private val categoriesDao: CategoriesDao,
    private val recipeApiService: RecipeApiService,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun getCategoriesFromCache(): List<Category> = withContext(ioDispatcher) {
        categoriesDao.getAllCategories()
    }

    suspend fun getCategories(): List<Category>? = withContext(ioDispatcher) {
        try {
            recipeApiService.getCategories()
        } catch (e: HttpException) {
            Log.e("RecipesRepository", "HTTP ошибка ${e.code()} - ${e.message()}", e)
            null
        } catch (e: IOException) {
            Log.e("RecipesRepository", "IOException при получении категорий", e)
            null
        }
    }

    suspend fun getCategoryById(categoryId: Int): Category? = withContext(ioDispatcher) {
        try {
            recipeApiService.getCategoryById(categoryId)
        } catch (e: HttpException) {
            Log.e("RecipesRepository", "HTTP ошибка ${e.code()} - ${e.message()}", e)
            null
        } catch (e: IOException) {
            Log.e("RecipesRepository", "IOException при получении категории по ID", e)
            null
        }
    }

    suspend fun saveCategoriesToCache(categories: List<Category>) = withContext(ioDispatcher) {
        categoriesDao.addCategory(categories)
    }

    suspend fun getAllCachedRecipes(): List<Recipe> = withContext(ioDispatcher) {
        recipesDao.getAllRecipes()
    }

    suspend fun getRecipesByCategoryId(categoryId: Int): List<Recipe>? = withContext(ioDispatcher) {
        try {
            val response = recipeApiService.getRecipesByCategoryId(categoryId)
            Log.d("RecipesRepository", "Received recipes: $response")
            response
        } catch (e: HttpException) {
            Log.e("RecipesRepository", "HTTP ошибка ${e.code()} - ${e.message()}", e)
            null
        } catch (e: IOException) {
            Log.e("RecipesRepository", "IOException при получении рецептов по ID категории", e)
            null
        }
    }

    suspend fun getRecipesByIds(ids: Set<Int>): List<Recipe>? = withContext(ioDispatcher) {
        try {
            val idsString = ids.joinToString(",")
            recipeApiService.getRecipesByIds(idsString)
        } catch (e: HttpException) {
            Log.e("RecipesRepository", "HTTP ошибка ${e.code()} - ${e.message()}", e)
            null
        } catch (e: IOException) {
            Log.e("RecipesRepository", "IOException при получении рецептов по ID", e)
            null
        }
    }

    suspend fun getRecipeById(recipeId: Int): Recipe? = withContext(ioDispatcher) {
        try {
            val recipeFromApi = recipeApiService.getRecipeById(recipeId)

            val recipeFromDb = recipesDao.getRecipeById(recipeId)
            val isFavorite = recipeFromDb?.isFavorite ?: false

            recipeFromApi.copy(isFavorite = isFavorite)
        } catch (e: HttpException) {
            Log.e("RecipesRepository", "HTTP ошибка ${e.code()} - ${e.message()}", e)
            null
        } catch (e: IOException) {
            Log.e("RecipesRepository", "IOException при получении рецепта по ID", e)
            null
        }
    }

    suspend fun saveRecipesToCache(recipes: List<Recipe>) = withContext(ioDispatcher) {
        Log.d("RecipesRepository", "Saving ${recipes.size} recipes to cache")
        recipes.forEach { recipe ->
            Log.d("RecipesRepository", "Recipe ID: ${recipe.id}, Category ID: ${recipe.categoryId}")
        }
        recipesDao.addRecipes(recipes)
    }

    suspend fun getFavoriteRecipes(): List<Recipe> = withContext(ioDispatcher) {
        recipesDao.getFavoriteRecipes()
    }

    suspend fun updateRecipeFavoriteStatus(recipeId: Int, isFavorite: Boolean) = withContext(ioDispatcher) {
        recipesDao.updateRecipeFavoriteStatus(recipeId, isFavorite)
    }

}