package com.example.kudos.CS426Project;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;

@Dao
public interface BookmarkDao {
    @Insert
    void insert(Bookmark... bookmarks);

    @Delete
    void delete(Bookmark... bookmarks);
}
