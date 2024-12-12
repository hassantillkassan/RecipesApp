package com.example.recipesapp.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.R
import com.example.recipesapp.common.loadImage
import com.example.recipesapp.databinding.ItemCategoryBinding
import com.example.recipesapp.model.Category

class CategoriesListAdapter(
    private var dataSet: List<Category>,
    private val onItemClick: (Int) -> Unit
) :
    RecyclerView.Adapter<CategoriesListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val binding = viewHolder.binding
        val category = dataSet[position]

        binding.tvTitle.text = category.title
        binding.tvDescription.text = category.description

        binding.ivCategoryImage.loadImage(category.imageUrl)

        binding.ivCategoryImage.contentDescription = binding.root.context.getString(
            R.string.text_category_image_description,
            category.title
        )

        binding.root.setOnClickListener {
            onItemClick(category.id)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(categories: List<Category>)  {
        this.dataSet = categories
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataSet.size

}

