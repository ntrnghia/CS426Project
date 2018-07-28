package com.example.kudos.CS426Project;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Place.class, parentColumns = "id", childColumns = "placeId", onDelete = CASCADE))
class Bookmark {
    @PrimaryKey
    private final int placeId;

    public Bookmark(int placeId) {
        this.placeId = placeId;
    }

    public int getPlaceId() {
        return placeId;
    }
}
