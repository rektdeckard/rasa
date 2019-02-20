package com.tobiasfried.brewkeeper.data;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface BrewDao {

    @Query("SELECT * FROM brews ORDER BY id")
    List<Brew> loadAllBrews();

    @Insert
    long insertBrew(Brew brew);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateBrew(Brew brew);

    @Delete
    int deleteBrew(Brew brew);

}
