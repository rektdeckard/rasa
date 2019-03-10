package com.tobiasfried.brewkeeper.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.tobiasfried.brewkeeper.AppExecutors;
import com.tobiasfried.brewkeeper.constants.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Database(entities = {Ingredient.class, Recipe.class, Brew.class}, version = 1, exportSchema = false)
@TypeConverters({DataTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "brewkeeper";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        prepopulateDB();
                                    }
                                });

                            }
                        })
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract IngredientDao ingredientDao();

    public abstract RecipeDao recipeDao();

    public abstract BrewDao brewDao();

    private static void prepopulateDB() {
        List<Ingredient> baseIngredients = new ArrayList<>();
        baseIngredients.add(new Ingredient("Ginger", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Peach", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Pear", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Lemon", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Lime", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Apple", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Pomegranate", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Raspberry", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Blackberry", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Blueberry", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Cranberry", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Orange", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Clementine", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Grapefruit", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Pineapple", IngredientType.FLAVOR, null));
        baseIngredients.add(new Ingredient("Passionfruit", IngredientType.FLAVOR, null));

        sInstance.ingredientDao().insertIngredientList(baseIngredients);
        Log.d(LOG_TAG, "Base ingredients inserted");

    }

}
