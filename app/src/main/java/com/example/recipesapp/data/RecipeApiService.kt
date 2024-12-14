package com.example.recipesapp.data

import com.example.recipesapp.model.Category
import com.example.recipesapp.model.Recipe
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApiService {

    @GET("category")
    suspend fun getCategories(): List<Category>

    @GET("category/{id}/recipes")
    suspend fun getRecipesByCategoryId(@Path("id") categoryId: Int): List<Recipe>

    @GET("category/{id}")
    suspend fun getCategoryById(@Path("id") categoryId: Int): Category

    @GET("recipes")
    suspend fun getRecipesByIds(@Query("ids") ids: String): List<Recipe>

    @GET("recipe/{id}")
    suspend fun getRecipeById(@Path("id") recipeId: Int): Recipe

}