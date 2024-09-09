package com.example.recipesapp
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.databinding.ItemCategoryBinding

import java.io.InputStream

class CategoriesListAdapter(private val dataSet: List<Category>) :
    RecyclerView.Adapter<CategoriesListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var _binding: ItemCategoryBinding? = null
        private val binding: ItemCategoryBinding
            get() = _binding ?:
            throw IllegalStateException("Binding for ItemCategoryBinding must not be null")

        init {
            _binding = ItemCategoryBinding.bind(view)
        }

        fun bind(category: Category) {
            binding.tvTitle.text = category.title
            binding.tvDescription.text = category.description

            try {
                val inputStream: InputStream? = itemView.context?.assets?.open(category.imageUrl)
                val drawable = Drawable.createFromStream(inputStream, null)
                binding.ivCategoryImage.setImageDrawable(drawable)
                inputStream?.close()
            } catch (e: Exception) {
                Log.e("!!!", "Image not found ${category.imageUrl}", e)
            }
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_category, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

}