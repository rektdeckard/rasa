package com.tobiasfried.brewkeeper.data;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface IngredientDao {

    @Query("SELECT * FROM ingredients ORDER BY id")
    List<Ingredient> getAllIngredients();

    @Query("SELECT * FROM ingredients WHERE type = 2 ORDER BY name ASC")
    List<Ingredient> getAllFlavors();

    @Query("SELECT * FROM ingredients WHERE id = :id")
    Ingredient getIngredient(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertIngredient(Ingredient ingredient);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertIngredientList(List<Ingredient> ingredients);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateIngredient(Ingredient ingredient);

    @Delete
    int deleteIngredient(Ingredient ingredient);

    @Delete
    int deleteIngredients(List<Ingredient> ingredients);

}
