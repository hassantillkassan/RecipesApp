package com.example.recipesapp.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.R
import com.example.recipesapp.databinding.ItemIngredientBinding
import com.example.recipesapp.model.Ingredient
import java.math.BigDecimal
import java.math.RoundingMode

class IngredientsAdapter(
    private var dataSet: List<Ingredient>
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

            val displayQuantity: String = try {
                val ingredientQuantityBD = BigDecimal(ingredient.quantity)
                val multiplierBD = BigDecimal(quantity)

                val totalQuantity = ingredientQuantityBD.multiply(multiplierBD)

                totalQuantity
                    .setScale(1, RoundingMode.HALF_UP)
                    .stripTrailingZeros()
                    .toPlainString()
            } catch (e: NumberFormatException) {
                ingredient.quantity
            }

            val unit = ingredient.unitOfMeasure
            val quantityText = if (displayQuantity.isNotBlank() && unit.isNotBlank()) {
                root.context.getString(
                    R.string.text_ingredient_quantity,
                    displayQuantity,
                    unit
                )
            } else {
                root.context.getString(
                    R.string.text_ingredient_quantity_no_unit,
                    displayQuantity
                )
            }
            tvIngredientQuantity.text = quantityText
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(ingredients: List<Ingredient>) {
        this.dataSet = ingredients
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataSet.size

    @Suppress("NotifyDataSetChanged")
    fun updateIngredients(progress: Int) {
        quantity = progress
        notifyDataSetChanged()
    }
}