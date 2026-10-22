package com.smartpantry.app.activities;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.smartpantry.app.R;
import com.smartpantry.app.database.DatabaseHelper;
import com.smartpantry.app.models.PantryItem;
import com.smartpantry.app.models.Recipe;
import com.smartpantry.app.models.RecipeIngredient;
import com.smartpantry.app.utils.IconUtils;
import com.smartpantry.app.utils.IngredientNormalizer;

import java.util.List;

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
        List<PantryItem> pantry = dbHelper.getAllPantryItems();

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
        tvIcon.setText(IconUtils.getRecipeIcon(recipe.getName()));

        // Build ingredients list with match status
        StringBuilder sb = new StringBuilder();
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ri : recipe.getIngredients()) {
                boolean hasIt = checkPantry(ri, pantry);
                String statusIcon = hasIt ? " ✅ " : " ❌ ";
                String color = hasIt ? "#43A047" : "#E53935"; // Green vs Red

                String qty = formatQty(ri.getRequiredQuantity());
                String icon = IconUtils.getIngredientIcon(ri.getIngredientName());
                String fullUnit = IconUtils.getFullUnitName(ri.getRequiredQuantity(), ri.getUnit());
                
                sb.append("<font color='").append(color).append("'><b>").append(statusIcon)
                        .append(icon).append(" ").append(ri.getIngredientName()).append("</b></font>")
                        .append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
                        .append("<font color='#546E7A'>").append(qty).append(" ").append(fullUnit).append("</font>")
                        .append("<br/><br/>");
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvIngredients.setText(Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvIngredients.setText(Html.fromHtml(sb.toString()));
        }
        tvInstructions.setText(recipe.getInstructions());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(recipe.getName());
        }
    }

    private boolean checkPantry(RecipeIngredient needed, List<PantryItem> pantry) {
        for (PantryItem available : pantry) {
            if (IngredientNormalizer.matches(needed.getIngredientName(), available.getIngredientName())) {
                if (IngredientNormalizer.isEnough(needed.getRequiredQuantity(), needed.getUnit(),
                        available.getQuantity(), available.getUnit())) {
                    return true;
                }
            }
        }
        return false;
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
