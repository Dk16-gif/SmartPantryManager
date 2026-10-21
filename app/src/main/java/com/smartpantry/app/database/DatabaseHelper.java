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
    private static final int DATABASE_VERSION = 6; // Force fresh seed with simple test recipe

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

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_INGREDIENT_NAME, item.getIngredientName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY_DATE, item.getExpiryDate() != null ? item.getExpiryDate() : "");
        return db.insert(TABLE_PANTRY, null, values);
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
        return item;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_INGREDIENT_NAME, item.getIngredientName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY_DATE, item.getExpiryDate() != null ? item.getExpiryDate() : "");
        return db.update(TABLE_PANTRY, values, COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    public void deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
    }

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
        return recipes;
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
        return list;
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
        return recipe;
    }

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
                    if (IngredientNormalizer.isEnough(needed.getRequiredQuantity(), needed.getUnit(), 
                                                     available.getQuantity(), available.getUnit())) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) return false;
        }
        return true;
    }

    private void seedRecipes(SQLiteDatabase db) {
        String[][] recipes = {
            {"Butter Toast", "1. Toast the bread until golden brown.\n2. Spread butter evenly on the toast.\n3. Serve warm.", "🍞"},
            {"Chicken Fried Rice", "1. Cook rice.\n2. Fry chicken.\n3. Add eggs and veggies.\n4. Stir in soy sauce.", "🍚"},
            {"Tomato Pasta", "1. Boil pasta.\n2. Sauté garlic and tomatoes.\n3. Mix and serve.", "🍝"},
            {"Vegetable Omelette", "1. Whisk eggs.\n2. Sauté veggies.\n3. Pour eggs and fold.", "🍳"},
            {"Chicken Curry", "1. Brown chicken.\n2. Add spices and tomatoes.\n3. Simmer until tender.", "🍛"},
            {"Tuna Sandwich", "1. Mix tuna and mayo.\n2. Spread on bread.", "🥪"},
            {"Beef Stir Fry", "1. Sear beef.\n2. Add veggies and sauce.\n3. Stir fry.", "🥢"},
            {"Potato Omelette", "1. Fry potatoes.\n2. Add eggs and cook.", "🥘"},
            {"Tomato Rice", "1. Sauté onions.\n2. Cook rice with tomatoes.", "🥣"},
            {"Chicken Pasta", "1. Cook pasta.\n2. Fry chicken.\n3. Mix with cream.", "🍜"},
            {"Vegetable Rice", "1. Sauté veggies.\n2. Add rice and cook.", "🥗"},
            {"Beef Pasta", "1. Brown beef.\n2. Add sauce and pasta.", "🍝"},
            {"Egg Sandwich", "1. Mash eggs with mayo.\n2. Spread on bread.", "🥪"},
            {"Potato Curry", "1. Sauté onions.\n2. Add potatoes and simmer.", "🍛"},
            {"Chicken Wrap", "1. Grill chicken.\n2. Wrap with veggies.", "🌯"},
            {"Tuna Pasta", "1. Cook pasta.\n2. Mix with tuna and mayo.", "🍝"},
            {"Vegetable Soup", "1. Simmer veggies in stock.", "🍲"},
            {"Beef Rice", "1. Fry beef.\n2. Mix with rice.", "🥣"},
            {"Chicken Soup", "1. Simmer chicken and veggies.", "🍲"},
            {"Tomato Omelette", "1. Sauté tomatoes.\n2. Add eggs.", "🍳"},
            {"Potato and Egg Hash", "1. Fry potatoes.\n2. Crack eggs over top.", "🥘"}
        };

        Object[][][] recipeIngredients = {
            { {"Butter Toast"}, {"bread", 2.0, "slices"}, {"butter", 1.0, "tablespoons"} },
            { {"Chicken Fried Rice"}, {"chicken", 300.0, "grams"}, {"rice", 2.0, "cups"}, {"egg", 2.0, "pieces"}, {"soy sauce", 2.0, "tablespoons"}, {"onion", 1.0, "pieces"} },
            { {"Tomato Pasta"}, {"pasta", 200.0, "grams"}, {"tomato", 3.0, "pieces"}, {"garlic", 2.0, "pieces"}, {"olive oil", 2.0, "tablespoons"} },
            { {"Vegetable Omelette"}, {"egg", 3.0, "pieces"}, {"onion", 1.0, "pieces"}, {"tomato", 1.0, "pieces"}, {"green pepper", 1.0, "pieces"}, {"butter", 1.0, "tablespoons"} },
            { {"Chicken Curry"}, {"chicken", 500.0, "grams"}, {"onion", 1.0, "pieces"}, {"garlic", 2.0, "pieces"}, {"tomato", 2.0, "pieces"}, {"curry powder", 2.0, "tablespoons"}, {"rice", 2.0, "cups"} },
            { {"Tuna Sandwich"}, {"tuna", 1.0, "cans"}, {"bread", 4.0, "slices"}, {"mayonnaise", 2.0, "tablespoons"}, {"onion", 0.5, "pieces"} },
            { {"Beef Stir Fry"}, {"beef", 300.0, "grams"}, {"mixed vegetables", 200.0, "grams"}, {"soy sauce", 2.0, "tablespoons"}, {"oil", 2.0, "tablespoons"} },
            { {"Potato Omelette"}, {"potato", 2.0, "pieces"}, {"egg", 4.0, "pieces"}, {"oil", 3.0, "tablespoons"} },
            { {"Tomato Rice"}, {"rice", 2.0, "cups"}, {"tomato", 2.0, "pieces"}, {"onion", 1.0, "pieces"}, {"garlic", 1.0, "pieces"} },
            { {"Chicken Pasta"}, {"chicken", 300.0, "grams"}, {"pasta", 200.0, "grams"}, {"garlic", 2.0, "pieces"}, {"cream", 100.0, "milliliters"} },
            { {"Vegetable Rice"}, {"rice", 2.0, "cups"}, {"mixed vegetables", 200.0, "grams"}, {"onion", 1.0, "pieces"} },
            { {"Beef Pasta"}, {"beef", 300.0, "grams"}, {"pasta", 200.0, "grams"}, {"tomato", 2.0, "pieces"}, {"onion", 1.0, "pieces"}, {"garlic", 2.0, "pieces"} },
            { {"Egg Sandwich"}, {"egg", 3.0, "pieces"}, {"bread", 4.0, "slices"}, {"mayonnaise", 2.0, "tablespoons"} },
            { {"Potato Curry"}, {"potato", 3.0, "pieces"}, {"onion", 1.0, "pieces"}, {"curry powder", 2.0, "tablespoons"}, {"garlic", 2.0, "pieces"} },
            { {"Chicken Wrap"}, {"chicken", 300.0, "grams"}, {"tortilla", 2.0, "pieces"}, {"lettuce", 50.0, "grams"}, {"tomato", 1.0, "pieces"}, {"mayonnaise", 2.0, "tablespoons"} },
            { {"Tuna Pasta"}, {"tuna", 1.0, "cans"}, {"pasta", 200.0, "grams"}, {"mayonnaise", 2.0, "tablespoons"}, {"lemon", 1.0, "pieces"} },
            { {"Vegetable Soup"}, {"mixed vegetables", 300.0, "grams"}, {"onion", 1.0, "pieces"}, {"garlic", 2.0, "pieces"}, {"vegetable stock", 500.0, "milliliters"} },
            { {"Beef Rice"}, {"beef", 300.0, "grams"}, {"rice", 2.0, "cups"}, {"onion", 1.0, "pieces"}, {"tomato", 2.0, "pieces"} },
            { {"Chicken Soup"}, {"chicken", 500.0, "grams"}, {"mixed vegetables", 200.0, "grams"}, {"onion", 1.0, "pieces"}, {"garlic", 2.0, "pieces"} },
            { {"Tomato Omelette"}, {"egg", 3.0, "pieces"}, {"tomato", 2.0, "pieces"}, {"butter", 1.0, "tablespoons"} },
            { {"Potato and Egg Hash"}, {"potato", 2.0, "pieces"}, {"egg", 2.0, "pieces"}, {"onion", 1.0, "pieces"}, {"oil", 2.0, "tablespoons"} }
        };

        java.util.Map<String, Long> recipeIdMap = new java.util.HashMap<>();
        for (String[] r : recipes) {
            ContentValues cv = new ContentValues();
            cv.put(COL_RECIPE_NAME, r[0]);
            cv.put(COL_INSTRUCTIONS, r[1]);
            cv.put(COL_RECIPE_ICON, r[2]);
            long id = db.insert(TABLE_RECIPES, null, cv);
            recipeIdMap.put(r[0], id);
        }

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
