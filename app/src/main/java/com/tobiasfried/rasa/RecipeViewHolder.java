package com.tobiasfried.rasa;

import android.view.View;
import android.widget.TextView;

import com.tobiasfried.rasa.model.Recipe;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_view_name)
    TextView nameTextView;

    @BindView(R.id.text_view_tea)
    TextView teaTextView;

    @BindView(R.id.text_view_sugar)
    TextView sugarTextView;

    @BindView(R.id.text_view_flavor)
    TextView flavorTextView;

    public RecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Recipe recipe) {
        // Set fields
        nameTextView.setText(recipe.getName());
        if (!recipe.getTeas().isEmpty()) {
            teaTextView.setText(recipe.getTeas().get(0).getName());
        }
        sugarTextView.setText(itemView.getContext().getResources().getStringArray(R.array.array_sugar_types)[recipe.getPrimarySweetener()]);
        if (!recipe.getIngredients().isEmpty()) {
            flavorTextView.setText(recipe.getIngredients().get(0).getName());
        }
    }

}
