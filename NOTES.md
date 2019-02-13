# BrewKeeper

Track your brewing and fermentation projects with simple tools to keep you organized. Save your favorite recipes. Get reminders when action is needed. Keep notes on your projects.

### Structure
#### Database
  + Local SQLite DB
    - Recipes table for saved [Recipes](#recipes)
      1. -id INTEGER PRIMARY KEY AUTOINCREMENT
      2.
    - Current Brews table for ongoing [Brews](#brew)
    - Completed Brews table for completed projects
  + Contract
    -     
  + ContentResolver
    - Handles content URI requests
    - Declares Provider in manifest
  + ContentProvider
    - Validates queries
    - Parses table projections and selections
    - Directs queries to correct DB and table by matching URI
    - Rregisters MIME types for content URIs
  + SQLiteOpenHelper  
    - Holds reference to:
      - DB name, version
      - DB tables
      - Table columns and column arguments
      - Handles raw queries in SQL

#### Brew

#### Recipe

#### Ingredient

### User Interface
1. Current Brews
  +
2. Completed Brews
3. Recipes
