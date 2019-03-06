package com.tobiasfried.brewkeeper.data;

import java.util.Collection;
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
    List<Brew> getAllBrews();

    @Query("SELECT * FROM brews WHERE id = :id")
    Brew getBrew(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertBrew(Brew brew);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBrewList(List<Brew> brews);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateBrew(Brew brew);

    @Delete
    int deleteBrew(Brew brew);

    @Delete
    int deleteBrews(Collection<Brew> brews);

}
