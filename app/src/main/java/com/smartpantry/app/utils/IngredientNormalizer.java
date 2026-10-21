package com.smartpantry.app.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalises ingredient names and units so that matching is robust.
 * This ensures that a recipe requiring "2 cups" of "Potatoes" matches a pantry 
 * containing "5 cups" of "potato".
 */
public class IngredientNormalizer {

    private static final Map<String, String> SYNONYMS = new HashMap<>();
    private static final Map<String, String> UNIT_MAP = new HashMap<>();

    static {
        // Ingredient Synonyms (Plural to Singular mapping)
        SYNONYMS.put("tomatoes", "tomato");
        SYNONYMS.put("potatoes", "potato");
        SYNONYMS.put("onions", "onion");
        SYNONYMS.put("eggs", "egg");
        SYNONYMS.put("carrots", "carrot");
        SYNONYMS.put("chickens", "chicken");
        SYNONYMS.put("mushrooms", "mushroom");
        SYNONYMS.put("peppers", "pepper");
        SYNONYMS.put("garlic cloves", "garlic");
        SYNONYMS.put("cloves", "garlic");
        SYNONYMS.put("lemons", "lemon");
        SYNONYMS.put("bananas", "banana");
        SYNONYMS.put("apples", "apple");
        SYNONYMS.put("spinach leaves", "spinach");
        SYNONYMS.put("noodles", "noodle");
        SYNONYMS.put("wraps", "wrap");
        SYNONYMS.put("tortillas", "tortilla");

        // Unit Normalization (Abbreviation/Singular to Plural canonical form)
        // We normalize everything to plural for easy matching.
        addUnitGroup("grams", "g", "gram");
        addUnitGroup("kilograms", "kg", "kilogram");
        addUnitGroup("milliliters", "ml", "milliliter");
        addUnitGroup("liters", "l", "liter");
        addUnitGroup("pieces", "pc", "pcs", "piece");
        addUnitGroup("tablespoons", "tbsp", "tablespoon");
        addUnitGroup("teaspoons", "tsp", "teaspoon");
        addUnitGroup("cups", "cup");
        addUnitGroup("cans", "can");
        addUnitGroup("slices", "slice");
        addUnitGroup("bottles", "bottle");
        addUnitGroup("packets", "packet", "pack", "packs");
        addUnitGroup("bunches", "bunch");
    }

    private static void addUnitGroup(String canonical, String... variants) {
        UNIT_MAP.put(canonical.toLowerCase(), canonical.toLowerCase());
        for (String variant : variants) {
            UNIT_MAP.put(variant.toLowerCase(), canonical.toLowerCase());
        }
    }

    /**
     * Normalizes ingredient names.
     */
    public static String normalize(String name) {
        if (name == null) return "";
        String result = name.trim().toLowerCase();

        if (SYNONYMS.containsKey(result)) {
            return SYNONYMS.get(result);
        }

        // Basic plural stripping if not in map (e.g., "zucchinis" -> "zucchini")
        if (result.length() > 2 && result.endsWith("s") && !result.endsWith("ss")) {
            String singular = result.substring(0, result.length() - 1);
            if (SYNONYMS.containsKey(singular)) {
                return SYNONYMS.get(singular);
            }
            return singular;
        }

        return result;
    }

    /**
     * Normalizes units to a plural canonical form (e.g., "g" -> "grams").
     */
    public static String normalizeUnit(String unit) {
        if (unit == null) return "";
        String u = unit.trim().toLowerCase();
        if (UNIT_MAP.containsKey(u)) {
            return UNIT_MAP.get(u);
        }
        // Fallback: pluralize
        if (u.endsWith("s")) return u;
        return u + "s";
    }

    /**
     * Returns true if two ingredient names refer to the same thing.
     */
    public static boolean matches(String a, String b) {
        return normalize(a).equals(normalize(b));
    }

    /**
     * Returns true if two units are equivalent (e.g., "ml" and "milliliters").
     */
    public static boolean unitsMatch(String unitA, String unitB) {
        return normalizeUnit(unitA).equals(normalizeUnit(unitB));
    }
}
