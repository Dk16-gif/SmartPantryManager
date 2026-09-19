package com.smartpantry.app.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalises ingredient names so that "Tomatoes", "TOMATO", " tomato " all match.
 */
public class IngredientNormalizer {

    // Map plural/variant forms to a canonical singular form
    private static final Map<String, String> SYNONYMS = new HashMap<>();

    static {
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
    }

    /**
     * Normalise: trim whitespace, lowercase, strip trailing 's' for simple plurals,
     * then check the synonym map.
     */
    public static String normalize(String name) {
        if (name == null) return "";
        String result = name.trim().toLowerCase();

        // Check synonym map first (catches irregular plurals)
        if (SYNONYMS.containsKey(result)) {
            return SYNONYMS.get(result);
        }

        // Strip a trailing 's' for simple plurals (e.g. "cups" → "cup" not needed for units,
        // but handles ingredient names like "carrots" → "carrot" when not in map)
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
     * Returns true when two ingredient name strings refer to the same ingredient.
     */
    public static boolean matches(String a, String b) {
        return normalize(a).equals(normalize(b));
    }
}
