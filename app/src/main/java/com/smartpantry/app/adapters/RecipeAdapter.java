package com.smartpantry.app.adapters;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        if (recipes == null || position >= recipes.size()) return;
        
        Recipe recipe = recipes.get(position);
        if (recipe == null) return;

        // Safely set recipe name
        if (holder.tvRecipeName != null) {
            holder.tvRecipeName.setText(recipe.getName() != null ? recipe.getName() : "Unknown Recipe");
        }
        
        // Safely set recipe icon
        if (holder.tvRecipeIcon != null) {
            holder.tvRecipeIcon.setText(IconUtils.getRecipeIcon(recipe.getName()));
        }

        // Safely set ingredient count
        if (holder.tvIngredientCount != null) {
            int total = (recipe.getIngredients() != null) ? recipe.getIngredients().size() : 0;
            holder.tvIngredientCount.setText(total + " ingredients");
        }

        // Safely set meal type
        if (holder.tvMealType != null) {
            holder.tvMealType.setText(getMealType(recipe.getName()));
        }

        // Display Match Status safely to prevent NPEs
        boolean fullyMatched = recipe.isFullyMatched();
        List<String> missing = recipe.getMissingIngredients();
        
        if (fullyMatched) {
            if (holder.tvMatchBadge != null) {
                holder.tvMatchBadge.setText("Ready!");
                holder.tvMatchBadge.setVisibility(View.VISIBLE);
                holder.tvMatchBadge.setBackgroundResource(R.drawable.badge_matched);
                holder.tvMatchBadge.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.status_green));
            }
            if (holder.tvMissingInfo != null) {
                holder.tvMissingInfo.setVisibility(View.GONE);
            }
        } else {
            int missingCount = (missing != null) ? missing.size() : 0;
            
            if (holder.tvMatchBadge != null) {
                holder.tvMatchBadge.setText("Missing " + missingCount);
                holder.tvMatchBadge.setVisibility(View.VISIBLE);
                holder.tvMatchBadge.setBackgroundResource(R.drawable.badge_matched);
                holder.tvMatchBadge.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.status_amber));
            }

            if (holder.tvMissingInfo != null) {
                holder.tvMissingInfo.setVisibility(View.VISIBLE);
                if (missing != null && !missing.isEmpty()) {
                    String missingList = TextUtils.join(", ", missing);
                    holder.tvMissingInfo.setText("Need: " + missingList);
                } else {
                    holder.tvMissingInfo.setText("Quantities low");
                }
            }
        }

        // IMPORTANT: Set click listener on the root itemView. 
        // This avoids NPEs if cardView wasn't found correctly via findViewById.
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });
    }

    private String getMealType(String name) {
        if (name == null) return "Meal";
        String n = name.toLowerCase();
        if (n.contains("rice") || n.contains("pasta") || n.contains("curry") || n.contains("fry") || n.contains("stir")) return "Dinner";
        if (n.contains("omelette") || n.contains("egg") || n.contains("hash")) return "Breakfast";
        if (n.contains("sandwich") || n.contains("wrap") || n.contains("soup")) return "Lunch";
        return "Main Course";
    }

    @Override
    public int getItemCount() {
        return (recipes != null) ? recipes.size() : 0;
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeName, tvIngredientCount, tvRecipeIcon, tvMealType, tvMatchBadge, tvMissingInfo;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            // Use findViewWithTag or similar if IDs are unstable, but standard findViewById is fine here.
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tvIngredientCount = itemView.findViewById(R.id.tvIngredientCount);
            tvRecipeIcon = itemView.findViewById(R.id.tvRecipeIcon);
            tvMealType = itemView.findViewById(R.id.tvMealType);
            tvMatchBadge = itemView.findViewById(R.id.tvMatchBadge);
            tvMissingInfo = itemView.findViewById(R.id.tvMissingInfo);
        }
    }
}
