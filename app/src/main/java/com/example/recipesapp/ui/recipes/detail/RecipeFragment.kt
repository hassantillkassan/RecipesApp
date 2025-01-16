package com.example.recipesapp.ui.recipes.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesapp.R
import com.example.recipesapp.RecipeApplication
import com.example.recipesapp.common.loadImage
import com.example.recipesapp.databinding.FragmentRecipeBinding
import com.example.recipesapp.di.AppContainer
import com.example.recipesapp.model.ErrorType
import com.example.recipesapp.ui.IngredientsAdapter
import com.example.recipesapp.ui.MethodAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration


class RecipeFragment : Fragment() {

    private var _recipeBinding: FragmentRecipeBinding? = null
    private val recipeBinding: FragmentRecipeBinding
        get() = _recipeBinding
            ?: throw IllegalStateException("Binding for FragmentRecipeBinding must not be null")

    private lateinit var recipeViewModel: RecipeViewModel

    private val args: RecipeFragmentArgs by navArgs()

    private var ingredientsAdapter = IngredientsAdapter(emptyList())
    private var methodAdapter = MethodAdapter(emptyList())

    class PortionSeekBarListener(
        private val onChangeIngredients: (Int) -> Unit
    ) : OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onChangeIngredients(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer: AppContainer = (requireActivity().application as RecipeApplication).appContainer
        recipeViewModel = appContainer.recipeViewModelFactory.create()

        recipeViewModel.loadRecipe(args.recipeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _recipeBinding = FragmentRecipeBinding.inflate(inflater, container, false)
        return recipeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeBinding.btnFavorite.setOnClickListener {
            recipeViewModel.onFavoritesClicked()
        }

        initUI()
    }

    private fun initUI() {
        val dividerColor = ContextCompat.getColor(requireContext(), R.color.dividerColor)
        val divider = MaterialDividerItemDecoration(
            requireContext(),
            LinearLayoutManager.VERTICAL
        ).apply {
            dividerThickness = resources.getDimensionPixelSize(R.dimen.divider_thickness)
            this.dividerColor = dividerColor
            dividerInsetStart = resources.getDimensionPixelSize(R.dimen.main_space_12)
            dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.main_space_12)
            isLastItemDecorated = false
        }

        recipeBinding.rvIngredients.apply {
            adapter = ingredientsAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(divider)
        }

        recipeBinding.rvMethod.apply {
            adapter = methodAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(divider)
        }

        recipeBinding.seekBarPortions.setOnSeekBarChangeListener(
            PortionSeekBarListener { progress ->
                recipeViewModel.updatePortionCount(progress)
            }
        )

        recipeViewModel.state.observe(viewLifecycleOwner) { state ->
            state.recipe?.let { recipe ->
                recipeBinding.tvRecipe.text = recipe.title

                state.recipeImage?.let { imageUrl ->
                    recipeBinding.ivRecipeImage.loadImage(imageUrl)
                }


                recipeBinding.ivRecipeImage.contentDescription = getString(
                    R.string.text_recipe_image_description,
                    recipe.title
                )

                updateFavoriteIcon(state.isFavorite)

                ingredientsAdapter.updateData(recipe.ingredients)
                methodAdapter.updateData(recipe.method)

                recipeBinding.tvPortionQuantity.text =
                    getString(R.string.text_portions, state.portionCount)
                ingredientsAdapter.updateIngredients(state.portionCount)
            }

            state.error?.let { error ->
                val errorMessage = when (error) {
                    ErrorType.DATA_FETCH_ERROR -> getString(R.string.error_data_fetch)
                    ErrorType.UNKNOWN_ERROR -> getString(R.string.error_unknown)
                }
                Toast.makeText(
                    requireContext(),
                    errorMessage,
                    Toast.LENGTH_SHORT
                ).show()

                recipeViewModel.clearError()
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite)
            recipeBinding.btnFavorite.setImageResource(R.drawable.ic_heart_filled)
        else
            recipeBinding.btnFavorite.setImageResource(R.drawable.ic_heart_empty)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _recipeBinding = null
    }
}