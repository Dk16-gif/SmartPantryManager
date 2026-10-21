package com.smartpantry.app.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalises ingredient names and units, and handles unit conversions
 * to ensure strict matching works across different unit types.
 */
public class IngredientNormalizer {

    private static final Map<String, String> SYNONYMS = new HashMap<>();
    private static final Map<String, String> UNIT_MAP = new HashMap<>();
    private static final Map<String, Double> UNIT_FACTORS = new HashMap<>();

    static {
        // Ingredient Synonyms (Plural/Variant -> Canonical Singular)
        SYNONYMS.put("tomatoes", "tomato");
        SYNONYMS.put("potatoes", "potato");
        SYNONYMS.put("onions", "onion");
        SYNONYMS.put("eggs", "egg");
        SYNONYMS.put("carrots", "carrot");
        SYNONYMS.put("chickens", "chicken");
        SYNONYMS.put("mushrooms", "mushroom");
        SYNONYMS.put("peppers", "pepper");
        SYNONYMS.put("bell peppers", "pepper");
        SYNONYMS.put("lemons", "lemon");
        SYNONYMS.put("bananas", "banana");
        SYNONYMS.put("apples", "apple");
        SYNONYMS.put("spinach leaves", "spinach");
        SYNONYMS.put("noodles", "noodle");
        SYNONYMS.put("wraps", "wrap");
        SYNONYMS.put("tortillas", "tortilla");
        SYNONYMS.put("olive oil", "oil");
        SYNONYMS.put("vegetable oil", "oil");
        SYNONYMS.put("cooking oil", "oil");
        SYNONYMS.put("veggies", "mixed vegetables");
        SYNONYMS.put("garlic cloves", "garlic");
        SYNONYMS.put("cloves", "garlic");
    }

    static {
        // Unit Groups and Conversion Factors
        // Mass (Base: grams)
        addUnitGroup("grams", 1.0, "g", "gram");
        addUnitGroup("kilograms", 1000.0, "kg", "kilogram");
        addUnitGroup("ounces", 28.35, "oz", "ounce");
        addUnitGroup("pounds", 453.6, "lb", "pound", "lbs", "pounds");

        // Volume (Base: milliliters)
        addUnitGroup("milliliters", 1.0, "ml", "milliliter", "millilitre", "millilitres");
        addUnitGroup("liters", 1000.0, "l", "liter", "litre", "litres");
        addUnitGroup("cups", 240.0, "cup");
        addUnitGroup("tablespoons", 15.0, "tbsp", "tablespoon", "tbs", "tablespoons");
        addUnitGroup("teaspoons", 5.0, "tsp", "teaspoon", "teaspoons");

        // Count (Base: pieces)
        addUnitGroup("pieces", 1.0, "pc", "pcs", "piece");
        addUnitGroup("cans", 1.0, "can");
        addUnitGroup("slices", 1.0, "slice");
        addUnitGroup("packets", 1.0, "packet", "pack", "packs");
        addUnitGroup("bunches", 1.0, "bunch");
    }

    private static void addUnitGroup(String canonical, double factor, String... variants) {
        UNIT_MAP.put(canonical.toLowerCase(), canonical.toLowerCase());
        UNIT_FACTORS.put(canonical.toLowerCase(), factor);
        for (String variant : variants) {
            UNIT_MAP.put(variant.toLowerCase(), canonical.toLowerCase());
            UNIT_FACTORS.put(variant.toLowerCase(), factor);
        }
    }

    public static String normalize(String name) {
        if (name == null) return "";
        String result = name.trim().toLowerCase();
        if (SYNONYMS.containsKey(result)) return SYNONYMS.get(result);
        
        // Basic plural removal if not in map
        if (result.length() > 2 && result.endsWith("s") && !result.endsWith("ss")) {
            String singular = result.substring(0, result.length() - 1);
            if (SYNONYMS.containsKey(singular)) return SYNONYMS.get(singular);
            return singular;
        }
        return result;
    }

    public static String normalizeUnit(String unit) {
        if (unit == null || unit.isEmpty()) return "pieces";
        String u = unit.trim().toLowerCase();
        return UNIT_MAP.getOrDefault(u, u.endsWith("s") ? u : u + "s");
    }

    public static boolean matches(String needed, String available) {
        String nNeeded = normalize(needed);
        String nAvailable = normalize(available);
        // Lenient name matching to improve user experience
        return nNeeded.equals(nAvailable) || nAvailable.contains(nNeeded) || nNeeded.contains(nAvailable);
    }

    public static double convertToBase(double qty, String unit) {
        String u = unit.trim().toLowerCase();
        Double factor = UNIT_FACTORS.get(u);
        if (factor == null) factor = UNIT_FACTORS.get(normalizeUnit(u));
        return factor != null ? qty * factor : qty;
    }

    public static boolean isEnough(double neededQty, String neededUnit, double availableQty, String availableUnit) {
        String nNeeded = normalizeUnit(neededUnit);
        String nAvailable = normalizeUnit(availableUnit);
        
        // Compatibility check
        boolean sameGroup = (isMass(nNeeded) && isMass(nAvailable)) ||
                           (isVolume(nNeeded) && isVolume(nAvailable)) ||
                           (isCount(nNeeded) && isCount(nAvailable));
        
        if (!sameGroup && !nNeeded.equals(nAvailable)) return false;
        
        double neededBase = convertToBase(neededQty, neededUnit);
        double availableBase = convertToBase(availableQty, availableUnit);
        
        return availableBase >= (neededBase - 0.001);
    }

    private static boolean isMass(String u) {
        return u.equals("grams") || u.equals("kilograms") || u.equals("ounces") || u.equals("pounds");
    }

    private static boolean isVolume(String u) {
        return u.equals("milliliters") || u.equals("liters") || u.equals("cups") || u.equals("tablespoons") || u.equals("teaspoons");
    }

    private static boolean isCount(String u) {
        return u.equals("pieces") || u.equals("cans") || u.equals("slices") || u.equals("packets") || u.equals("bunches");
    }
}
