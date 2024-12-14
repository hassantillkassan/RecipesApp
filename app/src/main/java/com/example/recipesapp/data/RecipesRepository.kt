package com.example.recipesapp.data

import android.util.Log
import com.example.recipesapp.common.Constants
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class RecipesRepository {

    companion object {
        private const val CONNECTION_TIMEOUT = 10_000L
        private const val READ_TIMEOUT = 10_000L
    }

    private val apiService: RecipeApiService

    init {
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

        apiService = retrofit.create(RecipeApiService::class.java)
    }

    suspend fun getCategories(): List<Category>? = withContext(Dispatchers.IO) {
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

    suspend fun getRecipesByCategoryId(categoryId: Int): List<Recipe>? = withContext(Dispatchers.IO) {
        try {
            apiService.getRecipesByCategoryId(categoryId)
        } catch (e: HttpException) {
            Log.e("RecipesRepository", "HTTP ошибка ${e.code()} - ${e.message()}", e)
            null
        } catch (e: IOException) {
            Log.e("RecipesRepository", "IOException при получении рецептов по ID категории", e)
            null
        }
    }

    suspend fun getCategoryById(categoryId: Int): Category? = withContext(Dispatchers.IO) {
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

    suspend fun getRecipesByIds(ids: Set<Int>): List<Recipe>? = withContext(Dispatchers.IO) {
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

    suspend fun getRecipeById(recipeId: Int): Recipe? = withContext(Dispatchers.IO) {
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

}