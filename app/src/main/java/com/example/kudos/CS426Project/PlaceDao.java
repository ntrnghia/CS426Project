package com.example.kudos.CS426Project;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Place place);

    @Update
    void update(Place... places);

    @Delete
    void delete(Place... places);

    @Query("SELECT * FROM Place")
    LiveData<List<Place>> getPlaces();

    @Query("SELECT * FROM Place WHERE Id IN (SELECT placeId FROM Bookmark)")
    LiveData<List<Place>> getBookmarks();

    @Query("SELECT * FROM Place WHERE Name LIKE :query")
    LiveData<List<Place>> getResults(String query);
}
