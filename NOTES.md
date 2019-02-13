# BrewKeeper

Track your brewing and fermentation projects with simple tools to keep you organized. Save your favorite recipes. Get reminders when action is needed. Keep notes on your projects.

### Structure
#### Database
  + Local SQLite DB
    - Recipes table for saved [Recipes](#recipe)
      1. `_id INTEGER PRIMARY KEY AUTOINCREMENT`
      2. `name TEXT NOT NULL`
      3. `tea_name TEXT NOT NULL`
      4. `tea_type TEXT`
      5. `tea_amount INTEGER DEFAULT 0` // default units is grams
      6. ``
      6. `1f_time INTEGER DEFAULT 0` // primary fermentation brew time in milliseconds
      7.
    - Current Brews table for ongoing [Brews](#brew)
    - Completed Brews table for completed projects
  + Contract
    - Defines all DB   
  + ContentResolver
    - Handles content URI requests
    - Declares Provider in manifest
  + ContentProvider
    - Validates queries
    - Parses table projections and selections
    - Directs queries to correct DB and table by matching URI
    - Rregisters MIME types for content URIs
  + SQLiteOpenHelper
    - Handles table creation and initialization with respect to:
      - DB name, version
      - Table creation
    - Serves readable and writable DBs to ContentProvider  

#### Brew

#### Recipe

#### Ingredient

### User Interface
1. Current Brews
  +
2. Completed Brews
3. Recipes
