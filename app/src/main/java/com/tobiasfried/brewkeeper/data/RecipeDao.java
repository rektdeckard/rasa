package com.tobiasfried.brewkeeper.data;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY id")
    List<Recipe> loadAllRecipes();

    @Insert
    long insertRecipe(Recipe recipe);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateRecipe(Recipe recipe);

    @Delete
    int deleteRecipe(Recipe recipe);

}
