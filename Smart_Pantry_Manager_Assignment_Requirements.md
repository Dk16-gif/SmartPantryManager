# Mobile App Development 700 — Smart Pantry Manager

## Assignment Requirements

**Project:** Smart Pantry Manager  
**Language:** Java  
**IDE:** Android Studio  
**Purpose:** Reduce food waste by suggesting recipes using strictly the ingredients already available in the user's pantry.

> Source: Richfield Graduate Institute of Technology — Mobile App Development 700 Practical Assignment. fileciteturn0file0L29-L36

---

## 1. Core App Idea

The Smart Pantry Manager must allow users to:

- Add pantry ingredients.
- Edit pantry ingredients.
- Delete pantry ingredients.
- View all current pantry ingredients.
- Store pantry data permanently in a database.
- View a collection of at least **15–20 recipes**.
- Suggest only recipes that can be made using the ingredients currently in the pantry.
- View full recipe details and preparation steps.
- Use a Settings/Profile screen.
- Show a useful message when no recipes match.

The most important business rule is **strict recipe matching**.

A recipe must only be suggested when **every required ingredient is available in the pantry in at least the required quantity**. fileciteturn0file0L48-L53

---

# 2. Required Screens

The application must have at least these screens:

1. **Pantry List**
2. **Add/Edit Ingredient**
3. **Suggested Recipes**
4. **Recipe Detail**
5. **Settings/Profile**

The assignment also requires correct navigation between screens using Intents or another approved navigation approach. fileciteturn0file0L87-L101

### Suggested Screen Flow

```text
                ┌─────────────────┐
                │   Main/Pantry   │
                │      Screen     │
                └────────┬────────┘
                         │
          ┌──────────────┼──────────────┐
          │              │              │
          ▼              ▼              ▼
   ┌────────────┐ ┌──────────────┐ ┌────────────┐
   │ Add/Edit   │ │  Suggested   │ │  Settings  │
   │ Ingredient │ │   Recipes    │ │   Screen   │
   └────────────┘ └──────┬───────┘ └────────────┘
                         │
                         ▼
                  ┌──────────────┐
                  │ Recipe Detail│
                  └──────────────┘
```

---

# 3. Pantry Management

Each pantry item should contain at least:

- Ingredient name
- Quantity
- Unit
- Optional expiry date

The user must be able to perform full CRUD:

| Operation | Required |
|---|---|
| Create | Add a pantry item |
| Read | Display pantry items |
| Update | Edit an existing item |
| Delete | Remove an item |

The data must still exist after closing and reopening the application. fileciteturn0file0L54-L63 fileciteturn0file0L110-L113

---

# 4. Strict Recipe-Matching Logic

This is the **most important part of the application**.

## Rule

A recipe is suggested only if:

```text
ALL required ingredients
        +
required quantities
        =
available in the pantry
```

### Example

Recipe:

```text
Chicken Rice

Required:
- Chicken: 500 g
- Rice: 2 cups
- Tomato: 2
- Onion: 1
```

Pantry:

```text
Chicken: 500 g
Rice: 2 cups
Tomato: 2
Onion: 0
```

Result:

```text
NOT SUGGESTED
```

The recipe must not appear because the user does not have enough onion.

The assignment specifically states that partial matches must be excluded. fileciteturn0file0L71-L80

---

## 5. Ingredient Matching

The matching should handle simple real-world differences.

For example:

```text
tomato
tomatoes
```

should be treated as the same ingredient where appropriate.

Do not rely only on a naive exact-string comparison. fileciteturn0file0L81-L83

### Suggested approach

Create a normalisation method:

```text
"Tomatoes" → "tomato"
" TOMATO " → "tomato"
"tomato"   → "tomato"
```

Then compare the normalised names.

Also compare quantities before allowing a recipe to appear.

---

# 6. Database

Choose **ONE** database option:

- SQLite
- Firebase
- PostgreSQL

For a simple local Android application, SQLite is a suitable implementation option because the assignment explicitly allows local SQLite storage.

The database must support persistent CRUD operations. fileciteturn0file0L102-L113

## Suggested SQLite Structure

### Pantry Table

```text
pantry
-------------------------
id
ingredient_name
quantity
unit
expiry_date
```

### Recipe Table

```text
recipes
-------------------------
id
name
instructions
```

### Recipe Ingredients Table

```text
recipe_ingredients
-------------------------
id
recipe_id
ingredient_name
required_quantity
unit
```

This structure allows one recipe to have multiple ingredients.

---

# 7. RecyclerView / Adapter

The application must use at least one:

- RecyclerView, or
- ListView

with a custom Adapter.

Recommended:

```text
RecyclerView
     ↓
PantryAdapter
     ↓
PantryItem
     ↓
SQLite Database
```

The RecyclerView should dynamically display the current pantry data. fileciteturn0file0L95-L97

---

# 8. Navigation

Include a working navigation element.

Possible choices:

- Bottom navigation bar
- Toolbar menu
- Navigation drawer

A simple design could use:

```text
Home / Pantry
Recipes
Settings
```

Navigation should use Intents where appropriate. fileciteturn0file0L95-L99

---

# 9. Input Validation

Validate all user input.

Examples:

### Ingredient Name

```text
Cannot be empty
```

### Quantity

```text
Must be a valid number
Must be greater than 0
```

### Unit

```text
Must be selected or entered
```

### Expiry Date

```text
Must use a valid date
```

Show a clear error message instead of allowing invalid data.

---

# 10. No-Match Message

If the pantry does not contain enough ingredients for any recipe, do not leave the screen blank.

Display something such as:

```text
No recipes match your pantry yet.
Add more ingredients to see suggestions.
```

This is specifically required by the assignment. fileciteturn0file0L66-L70

---

# 11. Recipe Collection

Pre-load at least **15–20 recipes** when the application is first run.

Each recipe needs:

- Recipe name
- Required ingredients
- Required quantities
- Preparation steps

Example recipes:

```text
1. Chicken Fried Rice
2. Tomato Pasta
3. Vegetable Omelette
4. Chicken Curry
5. Tuna Sandwich
6. Beef Stir Fry
7. Potato Omelette
8. Tomato Rice
9. Chicken Pasta
10. Vegetable Rice
11. Beef Pasta
12. Egg Sandwich
13. Potato Curry
14. Chicken Wrap
15. Tuna Pasta
16. Vegetable Soup
17. Beef Rice
18. Chicken Soup
19. Tomato Omelette
20. Potato and Egg Hash
```

The assignment requires a minimum of 15–20 recipes stored in the database. fileciteturn0file0L59-L61

---

# 12. Settings Screen

The Settings/Profile screen must satisfy the minimum screen requirement.

Possible settings:

```text
Settings

[ ] Expiring-soon alerts

Preferred units:
( ) Metric
( ) Cups / spoons
```

The assignment gives expiring-soon alerts or unit preferences as examples. fileciteturn0file0L66-L68

---

# 13. Important Restrictions

Do **NOT** add:

- Google Maps
- Mapping SDKs
- GPS
- Device location services
- Nearby grocery-store features
- Payment processing
- Real financial transactions

The app must remain focused on the user's pantry and recipe matching. fileciteturn0file0L84-L86 fileciteturn0file0L114-L117

---

# 14. GitHub Requirements

The project must use GitHub from the start of development.

Requirements:

- Public GitHub repository
- Minimum **10 meaningful commits**
- Commits must represent real development progress
- Use clear commit messages
- Include a README.md

Example commit history:

```text
1. Create Android Studio project
2. Add pantry data model
3. Create SQLite database
4. Implement pantry CRUD
5. Add Pantry RecyclerView
6. Add ingredient validation
7. Add recipe database
8. Implement strict recipe matching
9. Add recipe detail screen
10. Add settings screen
11. Improve UI design
12. Fix recipe matching bug
13. Add final testing
```

Do not make one large "final" commit. The assignment states that GitHub history is evidence of the development process. fileciteturn0file0L121-L134

---

# 15. README.md Requirements

The GitHub README should include:

```text
# Smart Pantry Manager

## Description
Short description of the application.

## Features
- Pantry management
- CRUD operations
- Recipe suggestions
- Strict ingredient matching
- Recipe details
- Settings

## Database
SQLite

## Why SQLite?
Explain why SQLite was selected.

## Requirements
- Android Studio
- Java
- Android SDK

## How to Run
1. Clone the repository.
2. Open the project in Android Studio.
3. Sync Gradle.
4. Run the application.
```

The assignment requires the README to explain the app, database choice and setup/run instructions. fileciteturn0file0L129-L130

---

# 16. Video Demonstration

Video length:

**5–7 minutes**

Your voice must narrate the demonstration. Your face does not have to appear. fileciteturn0file0L135-L139 fileciteturn0file0L157-L163

## Recommended Video Structure

### 0:00–1:00 — GitHub

Show:

- Repository
- Project files
- Commit history
- Explain how development progressed

### 1:00–3:30 — App Demo

Show:

1. Open app
2. Add ingredient
3. View ingredient
4. Edit ingredient
5. Delete ingredient
6. Open Suggested Recipes
7. Show a recipe that matches
8. Remove one required ingredient
9. Show the recipe disappear
10. Add the ingredient again
11. Show the recipe return
12. Close and reopen app
13. Show data is still saved

### 3:30–5:30 — Code Explanation

Explain three concepts such as:

- Activity/Fragment lifecycle
- Database operation
- RecyclerView and Adapter
- Intents
- Strict-matching algorithm

Point to the actual code while explaining. fileciteturn0file0L140-L154

### 5:30–6:30 — Database Choice

Explain:

```text
Why SQLite was selected
How data is stored
How CRUD works
How data remains after the app closes
```

---

# 17. Written Report

The report must contain these sections in this order:

1. Cover page
2. Table of contents
3. Introduction
4. System design
5. Screenshots of every output
6. Key code snippets
7. Challenges and solutions
8. Conclusion and reflection
9. References

The report must contain real screenshots from the running application, not mockups or stock images. fileciteturn0file0L164-L181

---

# 18. Report Screenshots Checklist

Capture screenshots of:

```text
[ ] Main/Pantry screen
[ ] Empty pantry
[ ] Add ingredient screen
[ ] Validation error
[ ] Pantry with ingredients
[ ] Edit ingredient
[ ] Delete confirmation/result
[ ] Suggested Recipes
[ ] Strict-match recipe shown
[ ] Recipe excluded because an ingredient is missing
[ ] Recipe Detail
[ ] Settings
[ ] Data after reopening app
```

---

# 19. Key Code Snippets for Report

Use 3–5 short snippets.

Recommended:

### Snippet 1 — Database

Show the SQLite database creation or CRUD method.

### Snippet 2 — RecyclerView Adapter

Show how pantry records are displayed.

### Snippet 3 — Strict Matching

Show the method that checks whether all recipe ingredients and quantities are available.

### Snippet 4 — Intent

Show navigation from one Activity to another.

### Snippet 5 — Input Validation

Show validation for the Add/Edit Ingredient form.

---

# 20. Challenges and Solutions

Use real problems encountered during development.

Suggested format:

```text
Challenge:
The recipe appeared even when one ingredient was missing.

Solution:
The matching method was changed to check every required ingredient
before adding a recipe to the Suggested Recipes list.
```

Only include challenges that actually happened during development.

---

# 21. Academic Formatting

The written assignment must use:

- Times New Roman
- Font size 12
- 1.5 line spacing
- Harvard Referencing Style

Required essay structure:

```text
Table of Contents
Introduction
Main Body
    Relevant Subheadings
Conclusion
References
```

Submit the assignment as a PDF on Moodle and use the specified cover page. Include a signed declaration of originality. fileciteturn0file0L15-L25

---

# 22. Submission ZIP

The final submission must be **one ZIP file**.

Naming format:

```text
Studentnumber_Surname_MobileAppDev700_Assignment.zip
```

It must contain:

```text
Android Studio Project
        +
5–7 Minute Video
        +
Written Report
```

The report should be named:

```text
Studentnumber_Surname_MobileAppDev700_Assignment.docx
```

The report must contain a working GitHub repository link. fileciteturn0file0L182-L193

## ZIP Size

Maximum:

```text
50 MB
```

Exclude unnecessary `build/` and `.gradle/` folders and compress the video appropriately. fileciteturn0file0L194-L196

---

# 23. Mark Allocation

| Area | Marks |
|---|---:|
| App Functionality & Requirements | 20 |
| Database Implementation | 15 |
| Code Quality & Java Fundamentals | 25 |
| UI/UX Design | 10 |
| GitHub Version Control | 10 |
| Video Demonstration | 10 |
| Written Report | 10 |
| **Total** | **100** |

The rubric specifically assesses working functionality, database persistence, Java fundamentals, UI/UX, GitHub history, the video demonstration and the written report. fileciteturn0file0L199-L231

---

# 24. Final Development Checklist

## Application

- [ ] Java only
- [ ] Android Studio project works
- [ ] Pantry List screen
- [ ] Add/Edit Ingredient screen
- [ ] Suggested Recipes screen
- [ ] Recipe Detail screen
- [ ] Settings screen
- [ ] RecyclerView/ListView + custom Adapter
- [ ] Navigation works
- [ ] Input validation works
- [ ] SQLite/Firebase/PostgreSQL implemented
- [ ] CRUD works
- [ ] Data persists after restart
- [ ] 15–20 recipes seeded
- [ ] Strict recipe matching works
- [ ] Quantity matching works
- [ ] Basic ingredient normalisation works
- [ ] No-match message works
- [ ] No GPS/maps/location features

## GitHub

- [ ] Repository created early
- [ ] Repository accessible to marker
- [ ] At least 10 meaningful commits
- [ ] Commit messages are descriptive
- [ ] README.md included
- [ ] GitHub history matches development

## Video

- [ ] 5–7 minutes
- [ ] Voice narration
- [ ] GitHub walkthrough
- [ ] Full CRUD demonstration
- [ ] Strict matching demonstrated
- [ ] Persistence demonstrated
- [ ] Code concepts explained
- [ ] Database choice explained
- [ ] Video included inside ZIP

## Written Report

- [ ] Cover page
- [ ] Table of contents
- [ ] Introduction
- [ ] System design
- [ ] Screen flow/wireframe
- [ ] Database model/ER diagram
- [ ] Real app screenshots
- [ ] Screenshot captions
- [ ] 3–5 code snippets
- [ ] Challenges and solutions
- [ ] Conclusion/reflection
- [ ] References
- [ ] Harvard referencing
- [ ] Times New Roman 12
- [ ] 1.5 spacing
- [ ] Signed declaration
- [ ] GitHub link

## Submission

- [ ] Android Studio source code included
- [ ] Video included
- [ ] Report included
- [ ] Correct ZIP name
- [ ] ZIP is under 50 MB
- [ ] Final ZIP uploaded to Moodle

---

# 25. Important Success Test

Before submission, perform this exact test:

```text
1. Start with an empty pantry.
2. Add all ingredients required by one recipe.
3. Open Suggested Recipes.
4. Confirm the recipe appears.
5. Remove ONE required ingredient.
6. Refresh Suggested Recipes.
7. Confirm the recipe disappears.
8. Add the missing ingredient again.
9. Confirm the recipe appears again.
10. Close the app.
11. Reopen the app.
12. Confirm the pantry data is still present.
```

This directly demonstrates the central strict-matching requirement and database persistence required by the assignment. fileciteturn0file0L71-L80 fileciteturn0file0L110-L113

---

## Source

Richfield Graduate Institute of Technology, **Mobile App Development 700 — Practical Assignment: Smart Pantry Manager**, supplied assignment brief.
