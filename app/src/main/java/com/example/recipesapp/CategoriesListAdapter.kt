package com.example.recipesapp

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.databinding.ItemCategoryBinding

import java.io.InputStream

class CategoriesListAdapter(private val dataSet: List<Category>) :
    RecyclerView.Adapter<CategoriesListAdapter.ViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(category: Category)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

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

        try {
            val inputStream: InputStream? = binding.root.context?.assets?.open(category.imageUrl)
            val drawable = Drawable.createFromStream(inputStream, null)
            binding.ivCategoryImage.setImageDrawable(drawable)
            inputStream?.close()
        } catch (e: Exception) {
            Log.e("CategoriesListAdapter", "Image not found ${category.imageUrl}", e)
        }

        binding.ivCategoryImage.contentDescription = binding.root.context.getString(
            R.string.category_image_description,
            category.title
        )

        binding.root.setOnClickListener {
            itemClickListener?.onItemClick(category)
        }
    }

    override fun getItemCount() = dataSet.size

}

