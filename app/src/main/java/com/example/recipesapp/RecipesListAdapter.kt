package com.example.recipesapp

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.databinding.ItemRecipeBinding
import java.io.InputStream

class RecipesListAdapter(
    private val dataSet: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit
) :
    RecyclerView.Adapter<RecipesListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRecipeBinding) :
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val recipe = dataSet[position]

        with(viewHolder.binding) {
            tvTitle.text = recipe.title

            try {
                val inputStream: InputStream = root.context.assets.open(recipe.imageUrl)
                val drawable = Drawable.createFromStream(inputStream, null)
                ivRecipeImage.setImageDrawable(drawable)
                inputStream.close()
            } catch (e: Exception) {
                Log.e("RecipesListAdapter", "Image not found ${recipe.imageUrl}", e)
            }

            ivRecipeImage.contentDescription = root.context.getString(
                R.string.text_recipe_image_description,
                recipe.title
            )

            root.setOnClickListener{
                onItemClick(recipe)
            }
        }
    }

    override fun getItemCount() = dataSet.size
}