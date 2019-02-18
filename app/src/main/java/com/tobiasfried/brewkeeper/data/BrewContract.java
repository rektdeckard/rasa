package com.tobiasfried.brewkeeper.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BrewContract {

    // Database URI constants
    public static final String CONTENT_AUTHORITY = "com.tobiasfried.brewkeeper";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RECIPES = "recipes";
    public static final String PATH_BREWS = "brews";
    public static final String PATH_INGREDIENTS = "ingredients";

    /** Private Constuctor (should not me instantiated) **/
    private BrewContract() {}

    /** Recipe Table Constants **/
    public static abstract class BasicEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of recipes.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single recipe.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPES;

        // Table URI constants
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RECIPES);

        // Table Name
        public static final String TABLE_NAME = PATH_RECIPES;

        // Column Names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TEA_NAME = "tea_name";
        public static final String COLUMN_TEA_TYPE = "tea_type";
        public static final String COLUMN_TEA_AMOUNT = "tea_amount";
        public static final String COLUMN_PRIMARY_SUGAR_TYPE = "primary_sugar_type";
        public static final String COLUMN_PRIMARY_SUGAR_AMOUNT = "primary_sugar_amount";
        public static final String COLUMN_PRIMARY_TIME = "primary_time";
        public static final String COLUMN_SECONDARY_SUGAR_TYPE = "secondary_sugar_type";
        public static final String COLUMN_SECONDARY_SUGAR_AMOUNT = "secondary_sugar_amount";
        public static final String COLUMN_SECONDARY_TIME = "secondary_time";
        public static final String COLUMN_INGREDIENT_1 = "ingredient_1";
        public static final String COLUMN_INGREDIENT_1_AMOUNT = "ingredient_1_amount";
        public static final String COLUMN_INGREDIENT_2 = "ingredient_2";
        public static final String COLUMN_INGREDIENT_2_AMOUNT = "ingredient_2_amount";

    }

    /** Brew Table Constants **/
    public static abstract class BrewEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of brews.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BREWS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single brew.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BREWS;

        // Table URI constants
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BREWS);

        // Table Name
        public static final String TABLE_NAME = PATH_BREWS;

        // Column Names (most enumerated in {@link #BaseEntry}
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_STAGE = "stage";
        public static final String COLUMN_IS_RUNNING = "is_running";

        // Constant Values
        public static final int IS_RUNNING = 1;
        public static final int NOT_RUNNING = 0;

        public static final int STAGE_PRIMARY = 1;
        public static final int STAGE_SECONDARY = 2;

        /**
         * Returns whether or not the given brew is {@link #IS_RUNNING}, {@link #NOT_RUNNING},
         */
        public static boolean isValidStatus(int running) {
            return (running == IS_RUNNING || running == NOT_RUNNING);
        }

    }

    /** Ingredients Table Constants **/
    public static abstract class IngredientEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of ingredients.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single ingredient.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENTS;

        // Table URI constants
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INGREDIENTS);

        // Table Name
        public static final String TABLE_NAME = PATH_INGREDIENTS;

        // Column Names
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_INGREDIENT_ID = "ingredient_id";

        // Constants
        public static final int TYPE_WHITE = 0;
        public static final int TYPE_GREEN = 1;
        public static final int TYPE_OOLONG = 2;
        public static final int TYPE_BLACK = 3;
        public static final int TYPE_PUERH = 4;
        public static final int TYPE_HERBAL = 5;
        public static final int TYPE_OTHER = 6;

    }

}
