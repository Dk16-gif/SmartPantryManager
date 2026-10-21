# Smart Pantry Manager

## Description
Smart Pantry Manager is an Android application that helps users reduce food waste by suggesting recipes based strictly on the ingredients currently available in their pantry. Users manage their pantry through full CRUD operations and receive recipe suggestions only when every required ingredient is present in sufficient quantity.

## Features
- **Pantry Management** — Add, view, edit, and delete pantry ingredients
- **Full CRUD Operations** — Create, Read, Update, Delete with persistent SQLite storage
- **Strict Recipe Matching** — A recipe is suggested only when ALL required ingredients are available in the required quantities
- **Ingredient Normalisation** — "Tomatoes" and "tomato" are treated as the same ingredient
- **20 Pre-loaded Recipes** — Seeded into the database on first run
- **Recipe Details** — Full ingredient list and step-by-step preparation instructions
- **Input Validation** — All fields validated before saving
- **No-Match Message** — Clear feedback when no recipes match the current pantry
- **Settings Screen** — Expiring-soon alerts toggle and preferred unit selection
- **Bottom Navigation** — Pantry, Recipes, and Settings tabs

## Database
**SQLite** — local on-device storage via Android's built-in SQLiteOpenHelper.

## Why SQLite?
SQLite was selected because:
1. It is natively supported on Android with no external dependencies.
2. Data is stored permanently on the device and survives app restarts.
3. It supports full relational CRUD operations via SQL.
4. It is sufficient for a single-user pantry application with no need for cloud sync.

## Database Schema
```
pantry               recipes              recipe_ingredients
─────────────────    ─────────────────    ──────────────────────
id (PK)              id (PK)              id (PK)
ingredient_name      name                 recipe_id (FK)
quantity             instructions         ingredient_name
unit                                      required_quantity
expiry_date                               unit
```

## Requirements
- Android Studio Hedgehog or later
- Java 8+
- Android SDK 24+ (Android 7.0)
- Gradle 8.1+

## How to Run
1. Clone the repository: `git clone <your-repo-url>`
2. Open the project in Android Studio (File → Open → select the `SmartPantryManager` folder).
3. Let Gradle sync automatically (or click **Sync Now** if prompted).
4. Connect a device or start an emulator (API 24+).
5. Click **Run ▶** or press `Shift+F10`.

## Screens
| Screen | Description |
|---|---|
| Pantry List | View all pantry ingredients; edit or delete each one |
| Add / Edit Ingredient | Form with validation for name, quantity, unit, expiry date |
| Suggested Recipes | Lists only recipes where every ingredient is available |
| Recipe Detail | Full ingredient list and preparation steps |
| Settings | Expiry alerts toggle and unit preference |

## Strict Matching Rule
A recipe appears in Suggested Recipes **only if** every required ingredient is present in the pantry **in at least the required quantity**. If even one ingredient is missing or insufficient, the recipe is excluded.


