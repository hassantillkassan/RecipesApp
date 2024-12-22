package com.example.recipesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.recipesapp.common.Converters
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.Recipe

@Database(entities = [Category::class, Recipe::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun categoriesDao() : CategoriesDao

    abstract fun recipesDao(): RecipesDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipes_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                instance = newInstance
                newInstance
            }
        }
    }

}