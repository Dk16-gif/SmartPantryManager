package com.smartpantry.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smartpantry.app.R;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREF_EXPIRY_ALERTS = "pref_expiry_alerts";
    private static final String PREF_UNITS = "pref_units";
    private static final String UNITS_METRIC = "metric";
    private static final String UNITS_CUPS = "cups";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SwitchCompat switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        RadioGroup rgUnits = findViewById(R.id.rgUnits);
        RadioButton rbMetric = findViewById(R.id.rbMetric);
        RadioButton rbCups = findViewById(R.id.rbCups);

        // Load saved preferences
        switchExpiryAlerts.setChecked(prefs.getBoolean(PREF_EXPIRY_ALERTS, false));
        String savedUnits = prefs.getString(PREF_UNITS, UNITS_METRIC);
        if (UNITS_CUPS.equals(savedUnits)) {
            rbCups.setChecked(true);
        } else {
            rbMetric.setChecked(true);
        }

        // Save on change
        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(PREF_EXPIRY_ALERTS, isChecked).apply());

        rgUnits.setOnCheckedChangeListener((group, checkedId) -> {
            String units = (checkedId == R.id.rbCups) ? UNITS_CUPS : UNITS_METRIC;
            prefs.edit().putString(PREF_UNITS, units).apply();
        });

        setupBottomNav();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_settings);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_recipes) {
                Intent intent = new Intent(this, SuggestedRecipesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_settings) {
                return true;
            }
            return false;
        });
    }
}
