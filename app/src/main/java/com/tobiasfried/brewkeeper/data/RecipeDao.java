package com.tobiasfried.brewkeeper.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY id")
    LiveData<List<Recipe>> getAllRecipes();

    @Query("SELECT * FROM recipes WHERE id = :id")
    LiveData<Recipe> getRecipe(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRecipe(Recipe recipe);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateRecipe(Recipe recipe);

    @Delete
    int deleteRecipe(Recipe recipe);

}
