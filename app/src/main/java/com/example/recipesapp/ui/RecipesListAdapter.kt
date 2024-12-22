package com.example.recipesapp.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.R
import com.example.recipesapp.common.Constants
import com.example.recipesapp.common.loadImage
import com.example.recipesapp.databinding.ItemRecipeBinding
import com.example.recipesapp.model.Recipe

class RecipesListAdapter(
    private var dataSet: List<Recipe>,
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

            ivRecipeImage.loadImage(recipe.updatedImageUrl)

            ivRecipeImage.contentDescription = root.context.getString(
                R.string.text_recipe_image_description,
                recipe.title
            )

            root.setOnClickListener{
                onItemClick(recipe)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(recipes: List<Recipe>) {
        this.dataSet = recipes
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataSet.size
}