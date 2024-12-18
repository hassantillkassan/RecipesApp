package com.example.recipesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipesapp.model.Category

@Database(entities = [Category::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun categoriesDao() : CategoriesDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipes_database"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }

}