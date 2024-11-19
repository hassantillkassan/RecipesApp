package com.example.recipesapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.R
import com.example.recipesapp.databinding.ItemMethodBinding

class MethodAdapter(
    private var dataSet: List<String>
) : RecyclerView.Adapter<MethodAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMethodBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMethodBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val step = dataSet[position]
        with(viewHolder.binding) {
            tvMethodStep.text = root.context.getString(
                R.string.text_method_step,
                position + 1,
                step
            )
        }
    }

    fun updateData(method: List<String>) {
        this.dataSet = method
    }

    override fun getItemCount() = dataSet.size
}