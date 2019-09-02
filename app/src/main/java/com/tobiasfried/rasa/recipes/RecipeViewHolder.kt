package com.tobiasfried.rasa.recipes

import android.view.View
import android.widget.TextView

import com.tobiasfried.rasa.domain.Recipe
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.tobiasfried.rasa.R

class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.text_view_name)
    internal var nameTextView: TextView? = null

    @BindView(R.id.text_view_tea)
    internal var teaTextView: TextView? = null

    @BindView(R.id.text_view_sugar)
    internal var sugarTextView: TextView? = null

    @BindView(R.id.text_view_flavor)
    internal var flavorTextView: TextView? = null

    init {
        ButterKnife.bind(this, itemView)
    }

    fun bind(recipe: Recipe) {
        // Set fields
        nameTextView!!.text = recipe.name
        if (!recipe.teas.isEmpty()) {
            teaTextView!!.text = recipe.teas[0].name
        }
        sugarTextView!!.text = itemView.context.resources.getStringArray(R.array.array_sugar_types)[recipe.primarySweetener]
        if (!recipe.ingredients.isEmpty()) {
            flavorTextView!!.text = recipe.ingredients[0].name
        }
    }

}
