package com.smartpantry.app.activities;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.smartpantry.app.R;
import com.smartpantry.app.database.DatabaseHelper;
import com.smartpantry.app.models.Recipe;
import com.smartpantry.app.models.RecipeIngredient;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        if (recipeId == -1) {
            finish();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Recipe recipe = dbHelper.getRecipeById(recipeId);

        if (recipe == null) {
            finish();
            return;
        }

        TextView tvName = findViewById(R.id.tvRecipeDetailName);
        TextView tvIcon = findViewById(R.id.tvRecipeDetailIcon);
        TextView tvIngredients = findViewById(R.id.tvRecipeDetailIngredients);
        TextView tvInstructions = findViewById(R.id.tvRecipeDetailInstructions);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvName.setText(recipe.getName());
        if (recipe.getIconEmoji() != null) {
            tvIcon.setText(recipe.getIconEmoji());
        }

        // Build ingredients list
        StringBuilder sb = new StringBuilder();
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ri : recipe.getIngredients()) {
                String qty = formatQty(ri.getRequiredQuantity());
                sb.append("<b>• ").append(ri.getIngredientName()).append("</b>")
                        .append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;")
                        .append("<font color='#1976D2'>").append(qty).append(" ").append(ri.getUnit()).append("</font>")
                        .append("<br/><br/>");
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvIngredients.setText(Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvIngredients.setText(Html.fromHtml(sb.toString()));
        }
        tvInstructions.setText(recipe.getInstructions());

        // Back button in toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(recipe.getName());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private String formatQty(double qty) {
        if (qty == (long) qty) return String.valueOf((long) qty);
        return String.valueOf(qty);
    }
}
