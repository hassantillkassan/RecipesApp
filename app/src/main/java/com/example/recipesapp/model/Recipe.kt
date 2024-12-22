package com.example.recipesapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recipesapp.common.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Recipe(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "recipe_title") val title: String,
    @ColumnInfo(name = "recipe_ingredients") val ingredients: List<Ingredient>,
    @ColumnInfo(name = "recipe_method") val method: List<String>,
    @ColumnInfo(name = "recipe_image_url") val imageUrl: String,
    @ColumnInfo(name = "category_id") val categoryId: Int = -1,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
) : Parcelable {
    val updatedImageUrl: String
        get() = "${Constants.BASE_URL}${Constants.IMAGES_PATH}$imageUrl"
}