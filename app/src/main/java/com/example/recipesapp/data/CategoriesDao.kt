package com.example.recipesapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.recipesapp.model.Category

@Dao
interface CategoriesDao {

    @Query("SELECT * FROM category")
    fun getAllCategories() : List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCategory(category: List<Category>)
}