package com.smartpantry.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smartpantry.app.R;
import com.smartpantry.app.adapters.PantryAdapter;
import com.smartpantry.app.database.DatabaseHelper;
import com.smartpantry.app.models.PantryItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private PantryAdapter adapter;
    private RecyclerView recyclerView;
    private android.view.View tvEmptyMessage;
    private TextView tvStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewPantry);
        tvEmptyMessage = findViewById(R.id.tvEmptyPantry);
        tvStats = findViewById(R.id.tvPantryStats);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddIngredient);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PantryAdapter(dbHelper.getAllPantryItems(), new PantryAdapter.OnItemActionListener() {
            @Override
            public void onEditClick(PantryItem item) {
                Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
                intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(PantryItem item) {
                showDeleteConfirmation(item);
            }
        });
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddEditIngredientActivity.class)));

        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<PantryItem> items = dbHelper.getAllPantryItems();
        boolean showAlerts = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_expiry_alerts", false);
        adapter.updateList(items, showAlerts);
        
        int count = items.size();
        tvStats.setText("You have " + count + " item" + (count == 1 ? "" : "s") + " in your pantry.");

        if (items.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            if (findViewById(R.id.headerContainer) != null) {
                findViewById(R.id.headerContainer).setVisibility(View.GONE);
            }
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (findViewById(R.id.headerContainer) != null) {
                findViewById(R.id.headerContainer).setVisibility(View.VISIBLE);
            }
        }
    }

    private void showDeleteConfirmation(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Remove \"" + item.getIngredientName() + "\" from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deletePantryItem(item.getId());
                    refreshList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_pantry);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                return true;
            } else if (id == R.id.nav_recipes) {
                Intent intent = new Intent(this, SuggestedRecipesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
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
