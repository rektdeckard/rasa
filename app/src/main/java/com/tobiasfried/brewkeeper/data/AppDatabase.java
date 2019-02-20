package com.tobiasfried.brewkeeper.data;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.tobiasfried.brewkeeper.constants.*;

@Database(entities = {Ingredient.class, Recipe.class, Brew.class}, version = 1, exportSchema = false)
@TypeConverters({DataTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "brewkeeper";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        // TODO NEVER DO THIS!! ALWAYS IN BACKGROUND THREAD!!
                        .allowMainThreadQueries()
                        .build();

//                Ingredient whiteSugar = new Ingredient("White Sugar", IngredientType.SWEETENER, null);
//                Ingredient rawSugar = new Ingredient("Raw Sugar", IngredientType.SWEETENER, null);
//                Ingredient brownSugar = new Ingredient("Brown Sugar", IngredientType.SWEETENER, null);
//                Ingredient agave = new Ingredient("Agave", IngredientType.SWEETENER, null);
//                Ingredient honey = new Ingredient("Honey", IngredientType.SWEETENER, null);
//                Ingredient other = new Ingredient("Other", IngredientType.SWEETENER, null);
//                sInstance.ingredientDao().insertIngredient(whiteSugar);
//                sInstance.ingredientDao().insertIngredient(rawSugar);
//                sInstance.ingredientDao().insertIngredient(brownSugar);
//                sInstance.ingredientDao().insertIngredient(agave);
//                sInstance.ingredientDao().insertIngredient(honey);
//                sInstance.ingredientDao().insertIngredient(other);
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract IngredientDao ingredientDao();
    public abstract RecipeDao recipeDao();
    public abstract BrewDao brewDao();

}
