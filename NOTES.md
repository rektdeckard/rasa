# BrewKeeper

Track your brewing and fermentation projects with simple tools to keep you organized. Save your favorite recipes. Get reminders when action is needed. Keep notes on your projects.

## Structure
#### Database
<<<<<<< HEAD
  1. Local SQLite DB
    - Recipes table for saved [Recipes](#recipe)
    - Brews table for both current and completed [Brews](#brew)
    - Ingredients table for all previously used [Ingredients](#ingredient)
  2. SQLiteOpenHelper
    - One for each table: [Recipes](#recipe), [Brew](#brew), [Ingredient](#ingredient)
    - Handles table creation and initialization with respect to:
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
=======
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
>>>>>>> f2f642e2a5b5f3c2ae41ea055f75fdb0f20839f9

#### Brew
  - Schema
| Column Name            | Type & Constraints                 | Notes                       |
|------------------------|------------------------------------|-----------------------------|
| `_id`                  | `INTEGER PRIMARY KEY AUTOINCREMENT`|                             |
| `name`                 | `TEXT NOT NULL`                    |                             |
| `tea_name`             | `TEXT NOT NULL`                    |                             |
| `tea_type`             | `TEXT NOT NULL`                    |                             |
| `tea_amount`           | `INTEGER DEFAULT 0`                | in grams                    |
| `1f_sugar_type`        | `TEXT DEFAULT 'Sugar'`             |                             |
| `1f_sugar_amount`      | `INTEGER DEFAULT 0`                | in grams                    |
| `1f_time`              | `INTEGER DEFAULT 0`                | in milliseconds             |
| `2f_sugar_type`        | `TEXT DEFAULT 'Sugar'`             |                             |
| `2f_sugar_amount`      | `INTEGER DEAULT 0`                 | in grams                    |
| `2f_time`              | `INTEGER DEAFULT 0`                | in seconds UTC              |
| `2f_ingredient1_id`    | `INTEGER` | ingredient1 id from [Ingredients](#ingredient) table |
| `2f_ingredient1_amount`| `INTEGER DEFAULT 0`                | in grams                    |
| `2f_ingredient2_id`    | `INTEGER` | ingredient2 if from [Ingredients](#ingredient) table |
| `start_time`           | `INTEGER NOT NULL`                 | in seconds UTC              |
| `end_time`             | `INTEGER NOT NULL`                 | in seconds UTC              |
| `is_running`           | `BOOLEAN NOT NULL`                 | where 0 or 1                |

#### Recipe
  - Schema
| Column Name            | Type & Constraints                  | Notes                       |
|------------------------|-------------------------------------|-----------------------------|
| `_id`                  | `INTEGER PRIMARY KEY AUTOINCREMENT` |                             |
| `name`                 | `TEXT NOT NULL`                     |                             |
| `tea_name`             | `TEXT NOT NULL`                     |                             |
| `tea_type`             | `TEXT NOT NULL`                     |                             |
| `tea_amount`           | `INTEGER DEFAULT 0`                 | in grams                    |
| `1f_sugar_type`        | `TEXT DEFAULT 'Sugar'`              |                             |
| `1f_sugar_amount`      | `INTEGER DEFAULT 0`                 | in grams                    |
| `1f_time`              | `INTEGER DEFAULT 0`                 | in milliseconds             |
| `2f_sugar_type`        | `TEXT DEFAULT 'Sugar'`              |                             |
| `2f_sugar_amount`      | `INTEGER DEAULT 0`                  | in grams                    |
| `2f_time`              | `INTEGER DEAFULT 0`                 | in seconds UTC              |
| `2f_ingredient1_id`    | `INTEGER`  | ingredient1 id from [Ingredients](#ingredient) table |
| `2f_ingredient1_amount`| `INTEGER DEFAULT 0`                 | in grams                    |
| `2f_ingredient2_id`    | `INTEGER`  | ingredient2 if from [Ingredients](#ingredient) table |

#### Ingredient
  - Schema
| Column Name     | Type & Constraints                  | Notes                                             |
|-----------------|-------------------------------------|---------------------------------------------------|
| `_id`           | `INTEGER PRIMARY KEY AUTOINCREMENT` | referenced in [Brew](#brew) and [Recipe](#recipe) |
| `name`          | `TEXT NOT NULL`                     |                                                   |
| `type`          | `TEXT NOT NULL`                     |                                                   |

### User Interface
1. Current Brews
2. Completed Brews
3. Recipes
