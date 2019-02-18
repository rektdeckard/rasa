package com.tobiasfried.brewkeeper.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tobiasfried.brewkeeper.data.BrewContract.BasicEntry;
import com.tobiasfried.brewkeeper.data.BrewContract.BrewEntry;
import com.tobiasfried.brewkeeper.data.BrewContract.IngredientEntry;

public class BrewDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BrewDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "brewkeeper.db";
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public BrewDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Assemble command to create Recipes table
        String SQL_CREATE_RECIPES ="CREATE TABLE " + BasicEntry.TABLE_NAME + " (" +
                BasicEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BasicEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                BasicEntry.COLUMN_TEA_NAME + " INTEGER NOT NULL, " +
                BasicEntry.COLUMN_TEA_TYPE + " INTEGER NOT NULL, " +
                BasicEntry.COLUMN_TEA_AMOUNT + " INTEGER DEFAULT 0, " +
                BasicEntry.COLUMN_PRIMARY_SUGAR_TYPE + " INTEGER, " +
                BasicEntry.COLUMN_PRIMARY_SUGAR_AMOUNT + " INTEGER DEFAULT 0, " +
                BasicEntry.COLUMN_PRIMARY_TIME + " INTEGER DEFAULT 0, " +
                BasicEntry.COLUMN_SECONDARY_SUGAR_TYPE + " INTEGER, " +
                BasicEntry.COLUMN_SECONDARY_SUGAR_AMOUNT + " INTEGER DEFAULT 0, " +
                BasicEntry.COLUMN_SECONDARY_TIME + " INTEGER DEFAULT 0, " +
                BasicEntry.COLUMN_INGREDIENT_1 + " INTEGER, " +
                BasicEntry.COLUMN_INGREDIENT_1_AMOUNT + " INTEGER DEFAULT 0, " +
                BasicEntry.COLUMN_INGREDIENT_2 + " INTEGER, " +
                BasicEntry.COLUMN_INGREDIENT_2_AMOUNT + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY (" + BasicEntry.COLUMN_TEA_NAME + ", " + BasicEntry.COLUMN_TEA_TYPE +
                ") REFERENCES " + IngredientEntry.TABLE_NAME + "(" + IngredientEntry.COLUMN_INGREDIENT_ID + " , " + BasicEntry.COLUMN_TEA_TYPE + "), " +
                "FOREIGN KEY (" + BasicEntry.COLUMN_INGREDIENT_1 +
                ") REFERENCES " + IngredientEntry.TABLE_NAME + "(" + IngredientEntry.COLUMN_INGREDIENT_ID + "), " +
                "FOREIGN KEY (" + BasicEntry.COLUMN_INGREDIENT_2 +
                ") REFERENCES " + IngredientEntry.TABLE_NAME + "(" + IngredientEntry.COLUMN_INGREDIENT_ID + "));";

        // Assemble command to create Brews table
        String SQL_CREATE_BREWS = "CREATE TABLE " + BrewEntry.TABLE_NAME + " (" +
                BasicEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BasicEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                BasicEntry.COLUMN_TEA_NAME + " INTEGER NOT NULL, " +
                BasicEntry.COLUMN_TEA_TYPE + " INTEGER NOT NULL, " +
                BasicEntry.COLUMN_TEA_AMOUNT + " INTEGER DEFAULT 0, " +
                BasicEntry.COLUMN_PRIMARY_SUGAR_TYPE + " INTEGER, " +
                BasicEntry.COLUMN_PRIMARY_SUGAR_AMOUNT + " INTEGER DEFAULT 0, " +
                BasicEntry.COLUMN_PRIMARY_TIME + " INTEGER DEFAULT 0, " +
                BasicEntry.COLUMN_SECONDARY_SUGAR_TYPE + " INTEGER, " +
                BasicEntry.COLUMN_SECONDARY_SUGAR_AMOUNT + " INTEGER DEFAULT 0, " +
                BasicEntry.COLUMN_SECONDARY_TIME + " INTEGER DEFAULT 0, " +
                BasicEntry.COLUMN_INGREDIENT_1 + " INTEGER, " +
                BasicEntry.COLUMN_INGREDIENT_1_AMOUNT + " INTEGER DEFAULT 0, " +
                BasicEntry.COLUMN_INGREDIENT_2 + " INTEGER, " +
                BasicEntry.COLUMN_INGREDIENT_2_AMOUNT + " INTEGER DEFAULT 0, " +
                BrewEntry.COLUMN_START_TIME + " INTEGER NOT NULL, " +
                BrewEntry.COLUMN_END_TIME + " INTEGER NOT NULL, " +
                BrewEntry.COLUMN_STAGE + " INTEGER NOT NULL, " +
                BrewEntry.COLUMN_IS_RUNNING + " BOOLEAN NOT NULL, " +
                "FOREIGN KEY (" + BasicEntry.COLUMN_TEA_NAME + ", " + BasicEntry.COLUMN_TEA_TYPE +
                ") REFERENCES " + IngredientEntry.TABLE_NAME + "(" + IngredientEntry.COLUMN_INGREDIENT_ID + " , " + BasicEntry.COLUMN_TEA_TYPE + "), " +
                "FOREIGN KEY (" + BasicEntry.COLUMN_INGREDIENT_1 +
                ") REFERENCES " + IngredientEntry.TABLE_NAME + "(" + IngredientEntry.COLUMN_INGREDIENT_ID + "), " +
                "FOREIGN KEY (" + BasicEntry.COLUMN_INGREDIENT_2 +
                ") REFERENCES " + IngredientEntry.TABLE_NAME + "(" + IngredientEntry.COLUMN_INGREDIENT_ID + "));";

        // Assemble command to create Ingredients table
        String SQL_CREATE_INGREDIENTS = "CREATE TABLE " + IngredientEntry.TABLE_NAME + " (" +
                BasicEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                IngredientEntry.COLUMN_INGREDIENT_ID + " INTEGER UNIQUE NOT NULL, " +
                BasicEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                IngredientEntry.COLUMN_TYPE + " INTEGER NOT NULL CHECK(" + IngredientEntry.COLUMN_TYPE + " >= 1 AND " +
                IngredientEntry.COLUMN_TYPE + " <= 3), " +
                BasicEntry.COLUMN_TEA_TYPE + " INTEGER CHECK (" + IngredientEntry.COLUMN_TYPE + " == 1 AND " +
                BasicEntry.COLUMN_TEA_TYPE + " >= 0 AND " +
                BasicEntry.COLUMN_TEA_TYPE + " <= 5));";

        // Create tables
        db.execSQL(SQL_CREATE_INGREDIENTS);
        db.execSQL(SQL_CREATE_RECIPES);
        db.execSQL(SQL_CREATE_BREWS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
