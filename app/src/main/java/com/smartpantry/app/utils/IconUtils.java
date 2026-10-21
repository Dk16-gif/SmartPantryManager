package com.smartpantry.app.utils;

public class IconUtils {

    public static String getIngredientIcon(String name) {
        if (name == null) return "📦";
        String n = name.toLowerCase();
        
        // Meat & Proteins
        if (n.contains("chicken")) return "🍗";
        if (n.contains("beef") || n.contains("steak") || n.contains("meat") || n.contains("lamb") || n.contains("pork")) return "🥩";
        if (n.contains("fish") || n.contains("tuna") || n.contains("salmon") || n.contains("shrimp") || n.contains("seafood")) return "🐟";
        if (n.contains("egg")) return "🥚";
        
        // Dairy
        if (n.contains("milk") || n.contains("cream") || n.contains("yogurt")) return "🥛";
        if (n.contains("cheese")) return "🧀";
        if (n.contains("butter") || n.contains("margarine")) return "🧈";
        
        // Vegetables
        if (n.contains("tomato") && !n.contains("paste") && !n.contains("sauce") && !n.contains("puree")) return "🍅";
        if (n.contains("onion") || n.contains("leek")) return "🧅";
        if (n.contains("garlic")) return "🧄";
        if (n.contains("pepper") || n.contains("chili")) return "🫑";
        if (n.contains("carrot")) return "🥕";
        if (n.contains("potato")) return "🥔";
        if (n.contains("lettuce") || n.contains("cabbage") || n.contains("spinach") || n.contains("leaf") || n.contains("kale")) return "🥬";
        if (n.contains("broccoli") || n.contains("cauliflower")) return "🥦";
        if (n.contains("mushroom")) return "🍄";
        if (n.contains("corn")) return "🌽";
        if (n.contains("cucumber") || n.contains("zucchini")) return "🥒";
        if (n.contains("avocado")) return "🥑";
        
        // Fruit
        if (n.contains("fruit") || n.contains("apple") || n.contains("banana") || n.contains("orange") || n.contains("berry") || n.contains("grape")) return "🍎";
        if (n.contains("lemon") || n.contains("lime")) return "🍋";
        
        // Grains & Pantry
        if (n.contains("pasta") || n.contains("noodle") || n.contains("spaghetti") || n.contains("macaroni")) return "🍝";
        if (n.contains("rice")) return "🍚";
        if (n.contains("bread") || n.contains("toast") || n.contains("loaf")) return "🍞";
        if (n.contains("flour") || n.contains("wheat") || n.contains("baking")) return "🌾";
        if (n.contains("bean") || n.contains("lentil") || n.contains("chickpea")) return "🫘";
        if (n.contains("wrap") || n.contains("tortilla")) return "🌯";
        
        // Condiments & Spices
        // Mayonnaise / Ketchup / Mustard / Dressing - Squeeze Bottle 🧴
        if (n.contains("mayonnaise") || n.contains("mayo") || n.contains("ketchup") || n.contains("mustard") || n.contains("dressing")) return "🧴"; 
        // Oil / Vinegar / Soy Sauce - Glass Bottle 🍶
        if (n.contains("oil") || n.contains("vinegar") || n.contains("sauce") || n.contains("soy")) return "🍶";
        
        if (n.contains("salt") || n.contains("spice") || n.contains("sugar") || n.contains("cinnamon") || n.contains("powder") || n.contains("pepper") && n.contains("ground")) return "🧂";
        if (n.contains("honey") || n.contains("syrup") || n.contains("jam") || n.contains("spread")) return "🍯";
        
        // Canned food / Paste / Puree - Can 🥫
        if (n.contains("can") || n.contains("tin") || n.contains("paste") || n.contains("puree")) return "🥫";
        
        return "📦";
    }

    public static String getRecipeIcon(String name) {
        if (name == null) return "🍽️";
        String n = name.toLowerCase();
        if (n.contains("fried rice")) return "🥡";
        if (n.contains("rice")) return "🍚";
        if (n.contains("pasta") || n.contains("noodle") || n.contains("spaghetti")) return "🍝";
        if (n.contains("curry")) return "🍛";
        if (n.contains("omelette") || n.contains("egg")) return "🍳";
        if (n.contains("sandwich") || n.contains("burger")) return "🥪";
        if (n.contains("wrap") || n.contains("tortilla") || n.contains("burrito")) return "🌯";
        if (n.contains("soup") || n.contains("stew") || n.contains("broth")) return "🍲";
        if (n.contains("stir fry") || n.contains("wok")) return "🥢";
        if (n.contains("hash") || n.contains("pan") || n.contains("roast")) return "🥘";
        if (n.contains("salad")) return "🥗";
        if (n.contains("beef") || n.contains("steak")) return "🥩";
        if (n.contains("chicken")) return "🍗";
        if (n.contains("tuna") || n.contains("fish") || n.contains("salmon")) return "🐟";
        return "🍽️";
    }

    public static int getIngredientColor(String name) {
        if (name == null) return 0xFF26A69A;
        String n = name.toLowerCase();
        if (n.contains("chicken") || n.contains("beef") || n.contains("meat") || n.contains("steak") || n.contains("lamb") || n.contains("pork")) return 0xFFEF5350; // Red
        if (n.contains("tuna") || n.contains("fish") || n.contains("salmon") || n.contains("shrimp") || n.contains("seafood")) return 0xFF42A5F5; // Blue
        if (n.contains("tomato") || n.contains("onion") || n.contains("pepper") || n.contains("carrot") || n.contains("vegetable") || n.contains("potato") || n.contains("lettuce") || n.contains("garlic") || n.contains("broccoli") || n.contains("mushroom")) return 0xFF66BB6A; // Green
        if (n.contains("milk") || n.contains("cheese") || n.contains("egg") || n.contains("butter") || n.contains("cream") || n.contains("yogurt")) return 0xFFFFCA28; // Amber
        if (n.contains("pasta") || n.contains("rice") || n.contains("bread") || n.contains("flour") || n.contains("noodle") || n.contains("wrap") || n.contains("tortilla") || n.contains("bean")) return 0xFF8D6E63; // Brown
        if (n.contains("oil") || n.contains("sauce") || n.contains("mayonnaise") || n.contains("ketchup") || n.contains("salt") || n.contains("spice") || n.contains("sugar") || n.contains("honey")) return 0xFF78909C; // Grey
        return 0xFF26A69A; // Teal default
    }

    public static String getFullUnitName(double qty, String unit) {
        if (unit == null) return "";
        String u = unit.toLowerCase().trim();
        boolean plural = qty != 1.0;
        
        switch (u) {
            case "g":
            case "gram":
            case "grams":
                return plural ? "grams" : "gram";
            case "kg":
            case "kilogram":
            case "kilograms":
                return plural ? "kilograms" : "kilogram";
            case "ml":
            case "milliliter":
            case "milliliters":
                return plural ? "milliliters" : "milliliter";
            case "l":
            case "liter":
            case "liters":
                return plural ? "liters" : "liter";
            case "pcs":
            case "pc":
            case "piece":
            case "pieces":
                return plural ? "pieces" : "piece";
            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return plural ? "tablespoons" : "tablespoon";
            case "tsp":
            case "teaspoon":
            case "teaspoons":
                return plural ? "teaspoons" : "teaspoon";
            case "cup":
            case "cups":
                return plural ? "cups" : "cup";
            case "can":
            case "cans":
                return plural ? "cans" : "can";
            case "slice":
            case "slices":
                return plural ? "slices" : "slice";
            case "bottle":
            case "bottles":
                return plural ? "bottles" : "bottle";
            case "packet":
            case "packets":
            case "pack":
            case "packs":
                return plural ? "packets" : "packet";
            case "bunch":
            case "bunches":
                return plural ? "bunches" : "bunch";
            case "oz":
            case "ounce":
            case "ounces":
                return plural ? "ounces" : "ounce";
            case "lb":
            case "pound":
            case "pounds":
                return plural ? "pounds" : "pound";
            default: return unit;
        }
    }
}
