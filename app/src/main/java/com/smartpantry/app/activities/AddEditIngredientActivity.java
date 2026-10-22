package com.smartpantry.app.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputLayout;
import com.smartpantry.app.R;
import com.smartpantry.app.database.DatabaseHelper;
import com.smartpantry.app.models.PantryItem;

import java.util.Calendar;

public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";
    private static final int NO_ID = -1;

    private EditText etName, etQuantity, etExpiry;
    private AutoCompleteTextView acUnit;
    private TextInputLayout tilName, tilQuantity, tilUnit;
    private Button btnSave;
    private TextView tvTitle;

    private DatabaseHelper dbHelper;
    private int editItemId = NO_ID;

    private static final String[] METRIC_UNITS = {
            "grams", "kilograms", "milliliters", "liters", "pieces", "eggs", "cans", "bunches", "packets"
    };
    private static final String[] IMPERIAL_UNITS = {
            "cups", "tablespoons", "teaspoons", "ounces", "pounds", "eggs", "slices", "pieces", "cans", "bunches", "packets"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);

        tvTitle = findViewById(R.id.tvFormTitle);
        etName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        acUnit = findViewById(R.id.acUnit);
        etExpiry = findViewById(R.id.etExpiryDate);
        tilName = findViewById(R.id.tilIngredientName);
        tilQuantity = findViewById(R.id.tilQuantity);
        tilUnit = findViewById(R.id.tilUnit);
        btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        // Set up unit dropdown
        String savedUnits = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("pref_units", "metric");
        String[] unitsToDisplay = "cups".equals(savedUnits) ? IMPERIAL_UNITS : METRIC_UNITS;

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item, unitsToDisplay);
        acUnit.setAdapter(unitAdapter);

        // Ensure dropdown shows on click even with inputType="none"
        acUnit.setOnClickListener(v -> acUnit.showDropDown());
        acUnit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) acUnit.showDropDown();
        });

        // Date picker for expiry
        etExpiry.setOnClickListener(v -> showDatePicker());
        etExpiry.setFocusable(false);

        // Check if editing an existing item
        editItemId = getIntent().getIntExtra(EXTRA_ITEM_ID, NO_ID);
        if (editItemId != NO_ID) {
            tvTitle.setText("Edit Ingredient");
            btnSave.setText("Update");
            loadItem();
        } else {
            tvTitle.setText("Add Ingredient");
            // Set a default unit for new items so the field isn't blank
            if (unitsToDisplay.length > 0) {
                acUnit.setText(unitsToDisplay[0], false);
            }
        }

        btnSave.setOnClickListener(v -> saveItem());
        btnCancel.setOnClickListener(v -> handleDiscard());

        // Handle Back Navigation with Safety Check
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleDiscard();
            }
        });
    }

    private void handleDiscard() {
        String name = etName.getText().toString().trim();
        String qty = etQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(qty)) {
            finish();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Discard Changes?")
                    .setMessage("Are you sure you want to stop? Your changes will be lost.")
                    .setPositiveButton("Discard", (dialog, which) -> finish())
                    .setNegativeButton("Keep Editing", null)
                    .show();
        }
    }

    private void loadItem() {
        PantryItem item = dbHelper.getPantryItemById(editItemId);
        if (item != null) {
            etName.setText(item.getIngredientName());
            etQuantity.setText(formatQty(item.getQuantity()));
            acUnit.setText(item.getUnit(), false);
            etExpiry.setText(item.getExpiryDate() != null ? item.getExpiryDate() : "");
        }
    }

    private String formatQty(double qty) {
        if (qty == (long) qty) return String.valueOf((long) qty);
        return String.valueOf(qty);
    }

    private void saveItem() {
        // Clear previous errors
        tilName.setError(null);
        tilQuantity.setError(null);
        tilUnit.setError(null);

        String name = etName.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String unit = acUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        boolean valid = true;

        if (TextUtils.isEmpty(name)) {
            tilName.setError("Ingredient name cannot be empty");
            valid = false;
        }

        if (TextUtils.isEmpty(quantityStr)) {
            tilQuantity.setError("Quantity is required");
            valid = false;
        } else {
            try {
                double qty = Double.parseDouble(quantityStr);
                if (qty <= 0) {
                    tilQuantity.setError("Quantity must be greater than 0");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                tilQuantity.setError("Please enter a valid number");
                valid = false;
            }
        }

        if (TextUtils.isEmpty(unit)) {
            tilUnit.setError("Please select or enter a unit");
            valid = false;
        }

        if (!valid) return;

        double quantity = Double.parseDouble(quantityStr);
        PantryItem item = new PantryItem(name, quantity, unit, expiry.isEmpty() ? null : expiry);

        if (editItemId == NO_ID) {
            dbHelper.addPantryItem(item);
        } else {
            item.setId(editItemId);
            dbHelper.updatePantryItem(item);
        }

        finish();
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(java.util.Locale.getDefault(),
                            "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etExpiry.setText(date);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }
}
