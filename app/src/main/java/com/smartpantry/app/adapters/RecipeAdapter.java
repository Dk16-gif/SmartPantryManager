package com.smartpantry.app.adapters;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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

        holder.tvRecipeIcon.setText(IconUtils.getRecipeIcon(recipe.getName()));

        int total = recipe.getIngredients() != null ? recipe.getIngredients().size() : 0;
        holder.tvIngredientCount.setText(total + " items total");

        holder.tvMealType.setText(getMealType(recipe.getName()));

        // Display Match Status
        if (recipe.isFullyMatched()) {
            holder.tvMatchBadge.setText("Ready to Cook!");
            holder.tvMatchBadge.setTextColor(Color.WHITE);
            holder.tvMatchBadge.setBackgroundResource(R.drawable.badge_matched);
            holder.tvMatchBadge.setBackgroundTintList(null);
            holder.tvMissingInfo.setVisibility(View.GONE);
        } else {
            int missingCount = recipe.getMissingIngredients().size();
            holder.tvMatchBadge.setText("Missing " + missingCount);
            holder.tvMatchBadge.setTextColor(Color.WHITE);
            holder.tvMatchBadge.setBackgroundResource(R.drawable.badge_matched);
            holder.tvMatchBadge.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.status_amber));

            holder.tvMissingInfo.setVisibility(View.VISIBLE);
            String missingList = TextUtils.join(", ", recipe.getMissingIngredients());
            holder.tvMissingInfo.setText("Need: " + missingList);
        }

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
        TextView tvRecipeName, tvIngredientCount, tvRecipeIcon, tvMealType, tvMatchBadge, tvMissingInfo;
        CardView cardView;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tvIngredientCount = itemView.findViewById(R.id.tvIngredientCount);
            tvRecipeIcon = itemView.findViewById(R.id.tvRecipeIcon);
            tvMealType = itemView.findViewById(R.id.tvMealType);
            tvMatchBadge = itemView.findViewById(R.id.tvMatchBadge);
            tvMissingInfo = new TextView(itemView.getContext()); // Fallback or find in layout

            // Check if tvMissingInfo exists in item_recipe.xml, if not we need to add it.
            tvMissingInfo = itemView.findViewById(R.id.tvMissingInfo);
        }
    }
}
