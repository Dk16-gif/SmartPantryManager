package com.smartpantry.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.app.R;
import com.smartpantry.app.models.Recipe;
import com.smartpantry.app.utils.IconUtils;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    public void updateList(List<Recipe> newRecipes) {
        this.recipes = newRecipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.tvRecipeName.setText(recipe.getName());

        // Use IconUtils for consistent recipe icons
        holder.tvRecipeIcon.setText(IconUtils.getRecipeIcon(recipe.getName()));

        // Show ingredient count as subtitle
        int count = recipe.getIngredients() != null ? recipe.getIngredients().size() : 0;
        holder.tvIngredientCount.setText(count + " items");

        // Set Meal Type based on name
        holder.tvMealType.setText(getMealType(recipe.getName()));

        holder.cardView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    private String getMealType(String name) {
        String n = name.toLowerCase();
        if (n.contains("rice") || n.contains("pasta") || n.contains("curry") || n.contains("fry") || n.contains("stir")) return "Dinner";
        if (n.contains("omelette") || n.contains("egg") || n.contains("hash")) return "Breakfast";
        if (n.contains("sandwich") || n.contains("wrap") || n.contains("soup")) return "Lunch";
        return "Main Course";
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeName, tvIngredientCount, tvRecipeIcon, tvMealType;
        CardView cardView;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tvIngredientCount = itemView.findViewById(R.id.tvIngredientCount);
            tvRecipeIcon = itemView.findViewById(R.id.tvRecipeIcon);
            tvMealType = itemView.findViewById(R.id.tvMealType);
            cardView = itemView.findViewById(R.id.cardRecipe);
        }
    }
}
