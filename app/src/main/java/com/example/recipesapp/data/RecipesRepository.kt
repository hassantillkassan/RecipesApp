package com.example.recipesapp.data

import android.util.Log
import com.example.recipesapp.common.Constants
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.Recipe
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
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

    fun getCategories(): List<Category>? {
        return try {
            val response: Response<List<Category>> = apiService.getCategories().execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("RecipesRepository", "Ошибка: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: IOException) {
            Log.e("RecipesRepository", "IOException при получении категорий", e)
            null
        }
    }

    fun getRecipesByCategoryId(categoryId: Int): List<Recipe>? {
        return try {
            val response: Response<List<Recipe>> = apiService.getRecipesByCategoryId(categoryId).execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("RecipesRepository", "Ошибка: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: IOException) {
            Log.e("RecipesRepository", "IOException при получении рецептов по ID категории", e)
            null
        }
    }

    fun getCategoryById(categoryId: Int): Category? {
        return try {
            val response: Response<Category> = apiService.getCategoryById(categoryId).execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("RecipesRepository", "Ошибка: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: IOException) {
            Log.e("RecipesRepository", "IOException при получении категории по ID", e)
            null
        }
    }

    fun getRecipesByIds(ids: Set<Int>): List<Recipe>? {
        return try {
            val idsString = ids.joinToString(",")
            val response: Response<List<Recipe>> = apiService.getRecipesByIds(idsString).execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("RecipesRepository", "Ошибка: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: IOException) {
            Log.e("RecipesRepository", "IOException при получении рецептов по ID", e)
            null
        }
    }

    fun getRecipeById (recipeId: Int): Recipe? {
        return try {
            val response: Response<Recipe> = apiService.getRecipeById(recipeId).execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("RecipesRepository", "Ошибка: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: IOException) {
            Log.e("RecipesRepository", "IOException при получении рецепта по ID", e)
            null
        }
    }

}