# BrewKeeper

Track your brewing and fermentation projects with simple tools to keep you organized. Save your favorite recipes. Get reminders when action is needed. Keep notes on your projects.

## Structure
#### Database
  1. Local SQLite DB
     - Recipes table for saved [Recipes](#recipe)
     - Brews table for both current and completed [Brews](#brew)
     - Ingredients table for all previously used [Ingredients](#ingredient)
  2. SQLiteOpenHelper
     - One for each table: [Recipes](#recipe), [Brew](#brew), [Ingredient](#ingredient)
     - Handles table creation and initialization with:
       - DB name, version
       - Table columns and constraints
     - Serves readable and writable DBs to ContentProvider  
  3. Contract
     - Defines all DB & Table Constants
     - Inner abstract classes for each table: [Recipes](#recipe), [Brew](#brew), [Ingredient](#ingredient)  
  4. ContentProvider
     - Validates queries
     - Parses table projections and selections
     - Directs queries to correct DB and table by matching URI
     - Registers MIME types for content URIs
  5. ContentResolver
     - Handles content URI requests
     - Declares Provider in manifest

#### Brew
  - Schema

| Column Name            | Type & Constraints                 | Notes                       |
|------------------------|------------------------------------|-----------------------------|
| `_id`                  | `INTEGER PRIMARY KEY AUTOINCREMENT`|                             |
| `name`                 | `TEXT NOT NULL`                    |                             |
| `tea_name`             | `INTEGER NOT NULL`                 | id from [Teas](#tea) table  |
| `tea_type`             | `INTEGER NOT NULL`            | tea_type from [Teas](#tea) table |
| `tea_amount`           | `INTEGER DEFAULT 0`                | in grams                    |
| `1f_sugar_type`        | `INTEGER`             | id from [Ingredients](#ingredient) table |
| `1f_sugar_amount`      | `INTEGER DEFAULT 0`                | in grams                    |
| `1f_time`              | `INTEGER DEFAULT 0`                | in seconds UTC              |
| `2f_sugar_type`        | `INTEGER`             | id from [Ingredients](#ingredient) table |
| `2f_sugar_amount`      | `INTEGER DEAULT 0`                 | in grams                    |
| `2f_time`              | `INTEGER DEAFULT 0`                | in seconds UTC              |
| `ingredient_1  `       | `INTEGER`             | id from [Ingredients](#ingredient) table |
| `ingredient_1_amount`  | `INTEGER DEFAULT 0`                | in grams                    |
| `ingredient_2`         | `INTEGER`             | id from [Ingredients](#ingredient) table |
| `ingredient_2_amount`  | `INTEGER DEFAULT 0`                | in grams                    |
| `start_time`           | `INTEGER NOT NULL`                 | in seconds UTC              |
| `end_time`             | `INTEGER NOT NULL`                 | in seconds UTC              |
| `stage`                | `INTEGER NOT NULL`                 | where 1 or 2                |
| `is_running`           | `BOOLEAN NOT NULL`                 | where 0 or 1                |

#### Recipe
  - Schema

| Column Name            | Type & Constraints                 | Notes                       |
|------------------------|------------------------------------|-----------------------------|
| `_id`                  | `INTEGER PRIMARY KEY AUTOINCREMENT`|                             |
| `name`                 | `TEXT NOT NULL`                    |                             |
| `tea_name`             | `INTEGER NOT NULL`                 | id from [Teas](#tea) table  |
| `tea_type`             | `INTEGER NOT NULL`            | tea_type from [Teas](#tea) table |
| `tea_amount`           | `INTEGER DEFAULT 0`                | in grams                    |
| `1f_sugar_type`        | `INTEGER`             | id from [Ingredients](#ingredient) table |
| `1f_sugar_amount`      | `INTEGER DEFAULT 0`                | in grams                    |
| `1f_time`              | `INTEGER DEFAULT 0`                | in seconds UTC              |
| `2f_sugar_type`        | `INTEGER`             | id from [Ingredients](#ingredient) table |
| `2f_sugar_amount`      | `INTEGER DEAULT 0`                 | in grams                    |
| `2f_time`              | `INTEGER DEAFULT 0`                | in seconds UTC              |
| `ingredient_1  `       | `INTEGER`             | id from [Ingredients](#ingredient) table |
| `ingredient_1_amount`  | `INTEGER DEFAULT 0`                | in grams                    |
| `ingredient_2`         | `INTEGER`             | id from [Ingredients](#ingredient) table |
| `ingredient_2_amount`  | `INTEGER DEFAULT 0`                | in grams                    |

#### Ingredient
  - Schema

| Column Name     | Type & Constraints                  | Notes                                             |
|-----------------|-------------------------------------|---------------------------------------------------|
| `_id`           | `INTEGER PRIMARY KEY AUTOINCREMENT` | referenced in [Brew](#brew) and [Recipe](#recipe) |
| `name`          | `TEXT NOT NULL`                     |                                                   |
| `type`          | `TEXT NOT NULL`                     |                                                   |

#### Tea
  - Schema

| Column Name            | Type & Constraints                 | Notes                       |
|------------------------|------------------------------------|-----------------------------|
| `_id`                  | `INTEGER PRIMARY KEY AUTOINCREMENT`|                             |
| `tea_name`             | `INTEGER NOT NULL`                 | int 0 - 5                   |
| `tea_type`             | `INTEGER NOT NULL`                 | int 0 - 5                   |  

## User Interface
#### Screens
  1. Current Brews
  2. Completed Brews
  3. Recipes
