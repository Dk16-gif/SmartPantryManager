package com.smartpantry.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smartpantry.app.models.PantryItem;
import com.smartpantry.app.models.Recipe;
import com.smartpantry.app.models.RecipeIngredient;
import com.smartpantry.app.utils.IngredientNormalizer;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2; // Incremented for icons

    // Pantry table
    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_INGREDIENT_NAME = "ingredient_name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY_DATE = "expiry_date";

    // Recipes table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_INSTRUCTIONS = "instructions";
    public static final String COL_RECIPE_ICON = "icon_emoji";

    // Recipe ingredients table
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_INGREDIENT_NAME = "ingredient_name";
    public static final String COL_RI_REQUIRED_QUANTITY = "required_quantity";
    public static final String COL_RI_UNIT = "unit";

    private static final String CREATE_PANTRY_TABLE =
            "CREATE TABLE " + TABLE_PANTRY + " (" +
                    COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_INGREDIENT_NAME + " TEXT NOT NULL, " +
                    COL_QUANTITY + " REAL NOT NULL, " +
                    COL_UNIT + " TEXT NOT NULL, " +
                    COL_EXPIRY_DATE + " TEXT);";

    private static final String CREATE_RECIPES_TABLE =
            "CREATE TABLE " + TABLE_RECIPES + " (" +
                    COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_RECIPE_NAME + " TEXT NOT NULL, " +
                    COL_INSTRUCTIONS + " TEXT NOT NULL, " +
                    COL_RECIPE_ICON + " TEXT);";

    private static final String CREATE_RECIPE_INGREDIENTS_TABLE =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                    COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                    COL_RI_INGREDIENT_NAME + " TEXT NOT NULL, " +
                    COL_RI_REQUIRED_QUANTITY + " REAL NOT NULL, " +
                    COL_RI_UNIT + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_RECIPE_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PANTRY_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_RECIPE_INGREDIENTS_TABLE);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PANTRY CRUD
    // ─────────────────────────────────────────────────────────────────────────

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_INGREDIENT_NAME, item.getIngredientName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY_DATE, item.getExpiryDate() != null ? item.getExpiryDate() : "");
        long id = db.insert(TABLE_PANTRY, null, values);
        db.close();
        return id;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PANTRY + " ORDER BY " + COL_INGREDIENT_NAME + " ASC", null);
        if (cursor.moveToFirst()) {
            do {
                PantryItem item = new PantryItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANTRY_ID)));
                item.setIngredientName(cursor.getString(cursor.getColumnIndexOrThrow(COL_INGREDIENT_NAME)));
                item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
                item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)));
                item.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY_DATE)));
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    public PantryItem getPantryItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PANTRY + " WHERE " + COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)});
        PantryItem item = null;
        if (cursor.moveToFirst()) {
            item = new PantryItem();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANTRY_ID)));
            item.setIngredientName(cursor.getString(cursor.getColumnIndexOrThrow(COL_INGREDIENT_NAME)));
            item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
            item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)));
            item.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY_DATE)));
        }
        cursor.close();
        db.close();
        return item;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_INGREDIENT_NAME, item.getIngredientName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY_DATE, item.getExpiryDate() != null ? item.getExpiryDate() : "");
        int rows = db.update(TABLE_PANTRY, values, COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
        return rows;
    }

    public void deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RECIPE READS
    // ─────────────────────────────────────────────────────────────────────────

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);
        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                recipe.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)));
                recipe.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)));
                recipe.setInstructions(cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)));
                recipe.setIconEmoji(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_ICON)));
                recipe.setIngredients(getRecipeIngredients(recipe.getId()));
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recipes;
    }

    public Recipe getRecipeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RECIPES + " WHERE " + COL_RECIPE_ID + " = ?",
                new String[]{String.valueOf(id)});
        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            recipe = new Recipe();
            recipe.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)));
            recipe.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)));
            recipe.setInstructions(cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)));
            recipe.setIconEmoji(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_ICON)));
            recipe.setIngredients(getRecipeIngredients(recipe.getId()));
        }
        cursor.close();
        db.close();
        return recipe;
    }

    public List<RecipeIngredient> getRecipeIngredients(int recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RECIPE_INGREDIENTS + " WHERE " + COL_RI_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)});
        if (cursor.moveToFirst()) {
            do {
                RecipeIngredient ri = new RecipeIngredient();
                ri.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RI_ID)));
                ri.setRecipeId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RI_RECIPE_ID)));
                ri.setIngredientName(cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_INGREDIENT_NAME)));
                ri.setRequiredQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RI_REQUIRED_QUANTITY)));
                ri.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_UNIT)));
                list.add(ri);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STRICT RECIPE MATCHING
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns only the recipes for which EVERY required ingredient is present
     * in the pantry in AT LEAST the required quantity.
     * Ingredient name comparison uses IngredientNormalizer.
     */
    public List<Recipe> getSuggestedRecipes() {
        List<PantryItem> pantry = getAllPantryItems();
        List<Recipe> allRecipes = getAllRecipes();
        List<Recipe> suggested = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (recipeMatchesPantry(recipe, pantry)) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    private boolean recipeMatchesPantry(Recipe recipe, List<PantryItem> pantry) {
        List<RecipeIngredient> required = recipe.getIngredients();
        if (required == null || required.isEmpty()) return false;

        for (RecipeIngredient needed : required) {
            boolean found = false;
            for (PantryItem available : pantry) {
                if (IngredientNormalizer.matches(needed.getIngredientName(), available.getIngredientName())) {
                    if (available.getQuantity() >= needed.getRequiredQuantity()) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) return false; // strict: every ingredient must be present
        }
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEED 20 RECIPES
    // ─────────────────────────────────────────────────────────────────────────

    private void seedRecipes(SQLiteDatabase db) {

        // Helper lambdas replaced with simple arrays for Java 8 compat
        // Format: { name, instructions, emoji }
        String[][] recipes = {
            {
                "Chicken Fried Rice",
                "1. Cook rice until fluffy.\n2. Dice chicken and fry in oil for 8 minutes.\n3. Push chicken to one side, scramble 2 eggs on the other side.\n4. Add rice, soy sauce, and spring onions.\n5. Stir-fry everything together for 3 minutes. Serve hot.",
                "🍚"
            },
            {
                "Tomato Pasta",
                "1. Boil pasta in salted water until al dente.\n2. Fry garlic in olive oil for 1 minute.\n3. Add diced tomatoes and season with salt and pepper.\n4. Simmer sauce for 10 minutes.\n5. Toss pasta with sauce. Serve with cheese if desired.",
                "🍝"
            },
            {
                "Vegetable Omelette",
                "1. Beat eggs with a pinch of salt.\n2. Dice onion, tomato, and green pepper.\n3. Fry vegetables in butter for 3 minutes.\n4. Pour eggs over vegetables and cook on low heat.\n5. Fold omelette in half and slide onto a plate.",
                "🍳"
            },
            {
                "Chicken Curry",
                "1. Fry onion until golden.\n2. Add garlic and curry powder, fry 1 minute.\n3. Add diced chicken and brown all sides.\n4. Add tomatoes and simmer 20 minutes.\n5. Stir in cream, adjust seasoning. Serve with rice.",
                "🍛"
            },
            {
                "Tuna Sandwich",
                "1. Drain tuna and mix with mayonnaise.\n2. Add diced onion and season with salt and pepper.\n3. Toast bread slices lightly.\n4. Spread tuna mixture on one slice.\n5. Top with lettuce, close sandwich, and serve.",
                "🥪"
            },
            {
                "Beef Stir Fry",
                "1. Slice beef thinly against the grain.\n2. Heat oil in wok until smoking.\n3. Stir-fry beef for 3 minutes, remove from wok.\n4. Fry mixed vegetables for 2 minutes.\n5. Return beef, add soy sauce and oyster sauce, toss and serve.",
                "🥢"
            },
            {
                "Potato Omelette",
                "1. Slice potatoes thinly and fry in oil until tender.\n2. Beat eggs with salt and pepper.\n3. Add cooked potatoes to eggs and mix gently.\n4. Cook mixture in a pan on low heat until set.\n5. Flip carefully and cook the other side. Slice to serve.",
                "🥘"
            },
            {
                "Tomato Rice",
                "1. Fry onion and garlic until soft.\n2. Add diced tomatoes and cook 5 minutes.\n3. Add rice and stir to coat.\n4. Pour in water or stock, bring to boil.\n5. Cover and simmer 18 minutes until rice is cooked.",
                "🥣"
            },
            {
                "Chicken Pasta",
                "1. Boil pasta until al dente.\n2. Fry chicken strips in oil until cooked through.\n3. Add garlic and fry 1 minute.\n4. Stir in cream and season well.\n5. Toss pasta in sauce and top with cheese.",
                "🍜"
            },
            {
                "Vegetable Rice",
                "1. Wash and dice all vegetables.\n2. Fry onion until soft.\n3. Add remaining vegetables and stir-fry 5 minutes.\n4. Add rice and water, bring to boil.\n5. Cover and cook on low until rice is fluffy.",
                "🥗"
            },
            {
                "Beef Pasta",
                "1. Brown minced beef in a pan.\n2. Add onion and garlic, cook until soft.\n3. Stir in tomatoes and season well.\n4. Simmer sauce for 15 minutes.\n5. Cook pasta, drain, and serve with beef sauce on top.",
                "🍝"
            },
            {
                "Egg Sandwich",
                "1. Hard-boil eggs for 10 minutes, then cool and peel.\n2. Mash eggs with mayonnaise and mustard.\n3. Season with salt, pepper, and a pinch of paprika.\n4. Toast bread until golden.\n5. Spread egg mixture on toast and serve.",
                "🥪"
            },
            {
                "Potato Curry",
                "1. Peel and cube potatoes.\n2. Fry onion until golden, add curry powder.\n3. Add potatoes and coat in spice mix.\n4. Pour in water or stock and bring to boil.\n5. Simmer 20 minutes until potatoes are soft. Serve with rice or bread.",
                "🍛"
            },
            {
                "Chicken Wrap",
                "1. Season chicken strips with spices and fry until cooked.\n2. Warm tortilla wraps in a dry pan.\n3. Spread mayonnaise on wrap.\n4. Add chicken, lettuce, and diced tomato.\n5. Roll wrap tightly and slice diagonally to serve.",
                "🌯"
            },
            {
                "Tuna Pasta",
                "1. Cook pasta in salted boiling water.\n2. Drain tuna and flake into a bowl.\n3. Mix tuna with mayonnaise, lemon juice, and seasoning.\n4. Drain pasta, allow to cool slightly.\n5. Toss pasta with tuna mixture and serve.",
                "🍝"
            },
            {
                "Vegetable Soup",
                "1. Chop all vegetables into small chunks.\n2. Fry onion and garlic in oil.\n3. Add remaining vegetables and stir.\n4. Pour in vegetable stock, bring to boil.\n5. Simmer 25 minutes until vegetables are tender. Season and serve.",
                "🍲"
            },
            {
                "Beef Rice",
                "1. Fry diced beef until browned.\n2. Add onion and garlic, fry until soft.\n3. Stir in tomatoes and season well.\n4. Add rice and stock, bring to boil.\n5. Cover and simmer until rice absorbs all liquid. Fluff and serve.",
                "🥣"
            },
            {
                "Chicken Soup",
                "1. Place chicken pieces in a pot with water.\n2. Bring to boil and skim foam from surface.\n3. Add diced vegetables and seasoning.\n4. Simmer for 40 minutes until chicken is tender.\n5. Remove chicken, shred meat, return to pot. Serve hot.",
                "🍲"
            },
            {
                "Tomato Omelette",
                "1. Beat eggs with salt and pepper.\n2. Dice tomatoes and fry briefly in butter.\n3. Pour eggs over tomatoes in the pan.\n4. Cook on medium-low heat until edges set.\n5. Fold omelette, cook 1 more minute, and serve.",
                "🍳"
            },
            {
                "Potato and Egg Hash",
                "1. Dice potatoes and fry in oil until golden and crispy.\n2. Add diced onion and cook until soft.\n3. Season with salt, pepper, and paprika.\n4. Create wells in the potato mixture and crack in eggs.\n5. Cover pan and cook until eggs are set. Serve immediately.",
                "🥘"
            }
        };

        // Recipe ingredients: { recipeName, ingredientName, quantity, unit }
        Object[][][] recipeIngredients = {
            // 1. Chicken Fried Rice
            { {"Chicken Fried Rice"}, {"chicken", 300.0, "g"}, {"rice", 2.0, "cups"}, {"egg", 2.0, "pcs"}, {"soy sauce", 2.0, "tbsp"}, {"onion", 1.0, "pcs"} },
            // 2. Tomato Pasta
            { {"Tomato Pasta"}, {"pasta", 200.0, "g"}, {"tomato", 3.0, "pcs"}, {"garlic", 2.0, "pcs"}, {"olive oil", 2.0, "tbsp"} },
            // 3. Vegetable Omelette
            { {"Vegetable Omelette"}, {"egg", 3.0, "pcs"}, {"onion", 1.0, "pcs"}, {"tomato", 1.0, "pcs"}, {"green pepper", 1.0, "pcs"}, {"butter", 1.0, "tbsp"} },
            // 4. Chicken Curry
            { {"Chicken Curry"}, {"chicken", 500.0, "g"}, {"onion", 1.0, "pcs"}, {"garlic", 2.0, "pcs"}, {"tomato", 2.0, "pcs"}, {"curry powder", 2.0, "tbsp"}, {"rice", 2.0, "cups"} },
            // 5. Tuna Sandwich
            { {"Tuna Sandwich"}, {"tuna", 1.0, "can"}, {"bread", 4.0, "slices"}, {"mayonnaise", 2.0, "tbsp"}, {"onion", 0.5, "pcs"} },
            // 6. Beef Stir Fry
            { {"Beef Stir Fry"}, {"beef", 300.0, "g"}, {"mixed vegetables", 200.0, "g"}, {"soy sauce", 2.0, "tbsp"}, {"oil", 2.0, "tbsp"} },
            // 7. Potato Omelette
            { {"Potato Omelette"}, {"potato", 2.0, "pcs"}, {"egg", 4.0, "pcs"}, {"oil", 3.0, "tbsp"} },
            // 8. Tomato Rice
            { {"Tomato Rice"}, {"rice", 2.0, "cups"}, {"tomato", 2.0, "pcs"}, {"onion", 1.0, "pcs"}, {"garlic", 1.0, "pcs"} },
            // 9. Chicken Pasta
            { {"Chicken Pasta"}, {"chicken", 300.0, "g"}, {"pasta", 200.0, "g"}, {"garlic", 2.0, "pcs"}, {"cream", 100.0, "ml"} },
            // 10. Vegetable Rice
            { {"Vegetable Rice"}, {"rice", 2.0, "cups"}, {"mixed vegetables", 200.0, "g"}, {"onion", 1.0, "pcs"} },
            // 11. Beef Pasta
            { {"Beef Pasta"}, {"beef", 300.0, "g"}, {"pasta", 200.0, "g"}, {"tomato", 2.0, "pcs"}, {"onion", 1.0, "pcs"}, {"garlic", 2.0, "pcs"} },
            // 12. Egg Sandwich
            { {"Egg Sandwich"}, {"egg", 3.0, "pcs"}, {"bread", 4.0, "slices"}, {"mayonnaise", 2.0, "tbsp"} },
            // 13. Potato Curry
            { {"Potato Curry"}, {"potato", 3.0, "pcs"}, {"onion", 1.0, "pcs"}, {"curry powder", 2.0, "tbsp"}, {"garlic", 2.0, "pcs"} },
            // 14. Chicken Wrap
            { {"Chicken Wrap"}, {"chicken", 300.0, "g"}, {"tortilla", 2.0, "pcs"}, {"lettuce", 50.0, "g"}, {"tomato", 1.0, "pcs"}, {"mayonnaise", 2.0, "tbsp"} },
            // 15. Tuna Pasta
            { {"Tuna Pasta"}, {"tuna", 1.0, "can"}, {"pasta", 200.0, "g"}, {"mayonnaise", 2.0, "tbsp"}, {"lemon", 1.0, "pcs"} },
            // 16. Vegetable Soup
            { {"Vegetable Soup"}, {"mixed vegetables", 300.0, "g"}, {"onion", 1.0, "pcs"}, {"garlic", 2.0, "pcs"}, {"vegetable stock", 500.0, "ml"} },
            // 17. Beef Rice
            { {"Beef Rice"}, {"beef", 300.0, "g"}, {"rice", 2.0, "cups"}, {"onion", 1.0, "pcs"}, {"tomato", 2.0, "pcs"} },
            // 18. Chicken Soup
            { {"Chicken Soup"}, {"chicken", 500.0, "g"}, {"mixed vegetables", 200.0, "g"}, {"onion", 1.0, "pcs"}, {"garlic", 2.0, "pcs"} },
            // 19. Tomato Omelette
            { {"Tomato Omelette"}, {"egg", 3.0, "pcs"}, {"tomato", 2.0, "pcs"}, {"butter", 1.0, "tbsp"} },
            // 20. Potato and Egg Hash
            { {"Potato and Egg Hash"}, {"potato", 2.0, "pcs"}, {"egg", 2.0, "pcs"}, {"onion", 1.0, "pcs"}, {"oil", 2.0, "tbsp"} }
        };

        // Insert all recipes and build a name→id map
        java.util.Map<String, Long> recipeIdMap = new java.util.HashMap<>();
        for (String[] r : recipes) {
            ContentValues cv = new ContentValues();
            cv.put(COL_RECIPE_NAME, r[0]);
            cv.put(COL_INSTRUCTIONS, r[1]);
            cv.put(COL_RECIPE_ICON, r[2]);
            long id = db.insert(TABLE_RECIPES, null, cv);
            recipeIdMap.put(r[0], id);
        }

        // Insert recipe ingredients
        for (Object[][] entry : recipeIngredients) {
            String recipeName = (String) ((Object[]) entry[0])[0];
            Long recipeId = recipeIdMap.get(recipeName);
            if (recipeId == null) continue;

            for (int i = 1; i < entry.length; i++) {
                Object[] ing = entry[i];
                ContentValues cv = new ContentValues();
                cv.put(COL_RI_RECIPE_ID, recipeId);
                cv.put(COL_RI_INGREDIENT_NAME, (String) ing[0]);
                cv.put(COL_RI_REQUIRED_QUANTITY, (Double) ing[1]);
                cv.put(COL_RI_UNIT, (String) ing[2]);
                db.insert(TABLE_RECIPE_INGREDIENTS, null, cv);
            }
        }
    }
}
