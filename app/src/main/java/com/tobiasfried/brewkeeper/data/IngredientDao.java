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
    List<Ingredient> loadAllIngredients();

    @Insert
    long insertIngredient(Ingredient ingredient);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateIngredient(Ingredient ingredient);

    @Delete
    int deleteIngredient(Ingredient ingredient);

}
