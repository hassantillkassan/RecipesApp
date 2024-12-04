package com.example.recipesapp.data

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
        private const val BASE_URL = "https://recipes.androidsprint.ru/api/"
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
            .baseUrl(BASE_URL)
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
                println("Error: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getRecipesByCategoryId(categoryId: Int): List<Recipe>? {
        return try {
            val response: Response<List<Recipe>> = apiService.getRecipesByCategoryId(categoryId).execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getCategoryById(categoryId: Int): Category? {
        return try {
            val response: Response<Category> = apiService.getCategoryById(categoryId).execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getRecipesByIds(ids: Set<Int>): List<Recipe>? {
        return try {
            val listOfIds= ids.toList()
            val response: Response<List<Recipe>> = apiService.getRecipesByIds(listOfIds).execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getRecipeById (recipeId: Int): Recipe? {
        return try {
            val response: Response<Recipe> = apiService.getRecipeById(recipeId).execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}