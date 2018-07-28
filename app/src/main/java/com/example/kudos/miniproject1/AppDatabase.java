package com.example.kudos.miniproject1;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Scanner;

import static com.example.kudos.miniproject1.MainActivity.resourcesMain;

@Database(entities = {Place.class, Bookmark.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PlaceDao placeDao();

    public abstract BookmarkDao bookmarkDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "AppDatabase").addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            new PopulateDbAsync(INSTANCE).execute();
                        }
                    }).build();
                }
            }
        }
        return INSTANCE;
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final PlaceDao placeDao;

        PopulateDbAsync(AppDatabase db) {
            placeDao = db.placeDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            ArrayList<Place> places = new Gson().fromJson(new Scanner(resourcesMain.openRawResource(R.raw.places)).useDelimiter("\\Z").next(), new TypeToken<ArrayList<Place>>() {
            }.getType());
            for (int i = 0; i < places.size(); ++i)
                placeDao.insert(places.get(i));
            return null;
        }
    }
}