package com.example.recipesapp.data

import android.util.Log
import com.example.recipesapp.common.Constants
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.Recipe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class RecipesRepository(
    private val apiService: RecipeApiService = createApiService(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    database: AppDatabase,
) {

    private val categoriesDao: CategoriesDao = database.categoriesDao()
    private val recipesDao: RecipesDao = database.recipesDao()

    companion object {
        private const val CONNECTION_TIMEOUT = 10_000L
        private const val READ_TIMEOUT = 10_000L

        private fun createApiService(): RecipeApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }


            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .build()


            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(RecipeApiService::class.java)
        }
    }

    suspend fun getCategoriesFromCache(): List<Category> = withContext(ioDispatcher) {
        categoriesDao.getAllCategories()
    }

    suspend fun getCategories(): List<Category>? = withContext(ioDispatcher) {
        try {
            apiService.getCategories()
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
            apiService.getCategoryById(categoryId)
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

    suspend fun getRecipesFromCache(categoryId: Int): List<Recipe> = withContext(ioDispatcher) {
        recipesDao.getRecipesByCategoryId(categoryId)
    }

    suspend fun getAllCachedRecipes(): List<Recipe> = withContext(ioDispatcher) {
        recipesDao.getAllRecipes()
    }

    suspend fun getRecipesByCategoryId(categoryId: Int): List<Recipe>? = withContext(ioDispatcher) {
        try {
            val response = apiService.getRecipesByCategoryId(categoryId)
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
            apiService.getRecipesByIds(idsString)
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
            apiService.getRecipeById(recipeId)
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

}