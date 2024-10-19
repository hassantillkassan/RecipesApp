package com.example.recipesapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.databinding.ItemIngredientBinding
import java.util.Locale

class IngredientsAdapter(
    private val dataSet: List<Ingredient>
) :
    RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

    private var quantity: Int = 1

    class ViewHolder(val binding: ItemIngredientBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIngredientBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val ingredient = dataSet[position]
        with(viewHolder.binding) {
            tvIngredientDescription.text = ingredient.description

            val calculatedQuantity = ingredient.quantity.toDouble() * quantity.toDouble()
            val displayQuantity = if (calculatedQuantity % 1 == 0.0)
                calculatedQuantity.toInt().toString()
            else
                String.format(Locale.getDefault(),"%.1f", calculatedQuantity)

            tvIngredientQuantity.text = root.context.getString(
                R.string.text_ingredient_quantity,
                displayQuantity,
                ingredient.unitOfMeasure
            )
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateIngredients(progress: Int) {
        if (quantity != progress) {
            quantity = progress
            for (i in 0 until itemCount) {
                notifyItemChanged(i)
            }
        }
    }
}