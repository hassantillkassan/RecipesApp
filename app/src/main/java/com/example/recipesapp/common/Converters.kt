package com.example.recipesapp.common

import androidx.room.TypeConverter
import com.example.recipesapp.model.Ingredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromIngredientList(ingredients: List<Ingredient>): String {
        return gson.toJson(ingredients)
    }

    @TypeConverter
    fun toIngredientList(data: String): List<Ingredient> {
        val listType = object : TypeToken<List<Ingredient>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromStringList(method: List<String>): String {
        return gson.toJson(method)
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }

}