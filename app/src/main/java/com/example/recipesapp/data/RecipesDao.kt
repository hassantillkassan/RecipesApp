package com.example.recipesapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.Recipe

@Dao
interface RecipesDao {

    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): List<Recipe>

    @Query("SELECT * FROM recipe WHERE category_id = :categoryId")
    fun getRecipesByCategoryId(categoryId: Int): List<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRecipes(recipes: List<Recipe>)

    @Query("SELECT * FROM recipe WHERE is_favorite = 1")
    fun getFavoriteRecipes(): List<Recipe>

    @Update
    fun updateRecipeFavoriteStatus(recipe: Recipe)

    @Query("UPDATE recipe SET is_favorite = :isFavorite WHERE id = :recipeId")
    fun updateRecipeFavoriteStatus(recipeId: Int, isFavorite: Boolean)

}