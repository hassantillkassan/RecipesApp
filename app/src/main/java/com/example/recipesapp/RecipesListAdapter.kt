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
    private val onItemClick: (Int) -> Unit
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
        val binding = viewHolder.binding
        val recipe = dataSet[position]

        binding.tvTitle.text = recipe.title

        try {
            val inputStream: InputStream = binding.root.context.assets.open(recipe.imageUrl)
            val drawable = Drawable.createFromStream(inputStream, null)
            binding.ivRecipeImage.setImageDrawable(drawable)
            inputStream.close()
        } catch (e: Exception) {
            Log.e("RecipesListAdapter", "Image not found ${recipe.imageUrl}", e)
        }

        binding.ivRecipeImage.contentDescription = binding.root.context.getString(
            R.string.recipe_image_description,
            recipe.title
        )

        binding.root.setOnClickListener{
            onItemClick(recipe.id)
        }
    }

    override fun getItemCount() = dataSet.size
}