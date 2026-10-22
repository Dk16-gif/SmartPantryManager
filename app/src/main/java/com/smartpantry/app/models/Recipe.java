package com.smartpantry.app.models;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private String instructions;
    private String iconEmoji;
    private List<RecipeIngredient> ingredients;

    // Fields for matching logic
    private List<String> missingIngredients = new ArrayList<>();
    private int matchCount = 0;

    public Recipe() {}

    public Recipe(int id, String name, String instructions, String iconEmoji) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.iconEmoji = iconEmoji;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getIconEmoji() { return iconEmoji; }
    public void setIconEmoji(String iconEmoji) { this.iconEmoji = iconEmoji; }

    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }

    public List<String> getMissingIngredients() { return missingIngredients; }
    public void setMissingIngredients(List<String> missingIngredients) { this.missingIngredients = missingIngredients; }

    public int getMatchCount() { return matchCount; }
    public void setMatchCount(int matchCount) { this.matchCount = matchCount; }

    public boolean isFullyMatched() {
        return missingIngredients.isEmpty();
    }
}
