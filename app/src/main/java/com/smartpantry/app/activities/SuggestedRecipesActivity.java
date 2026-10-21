package com.smartpantry.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smartpantry.app.R;
import com.smartpantry.app.adapters.RecipeAdapter;
import com.smartpantry.app.database.DatabaseHelper;
import com.smartpantry.app.models.Recipe;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecipeAdapter adapter;
    private RecyclerView recyclerView;
    private android.view.View tvNoMatch;
    private TextView tvRecipeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewRecipes);
        tvNoMatch = findViewById(R.id.tvNoMatch);
        tvRecipeCount = findViewById(R.id.tvRecipeCount);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecipeAdapter(dbHelper.getSuggestedRecipes(), recipe -> {
            Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        showOrHideEmptyMessage();
        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Recipe> suggested = dbHelper.getSuggestedRecipes();
        adapter.updateList(suggested);
        showOrHideEmptyMessage();
    }

    private void showOrHideEmptyMessage() {
        int count = adapter.getItemCount();
        if (count == 0) {
            tvNoMatch.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            if (findViewById(R.id.recipeHeader) != null) {
                findViewById(R.id.recipeHeader).setVisibility(View.GONE);
            }
        } else {
            tvNoMatch.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (findViewById(R.id.recipeHeader) != null) {
                findViewById(R.id.recipeHeader).setVisibility(View.VISIBLE);
            }
            tvRecipeCount.setText("You can cook " + count + " delicious recipe" + (count == 1 ? "" : "s") + " right now!");
        }
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_recipes);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_recipes) {
                return true;
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}
