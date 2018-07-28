package com.example.kudos.miniproject1;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Repository {
    private PlaceDao placeDao;
    private LiveData<List<Place>> places;
    private BookmarkDao bookmarkDao;
    private LiveData<List<Place>> bookmarks;

    Repository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        placeDao = db.placeDao();
        places = placeDao.getPlaces();
        bookmarkDao = db.bookmarkDao();
        bookmarks = placeDao.getBookmarks();
    }

    LiveData<List<Place>> getPlaces() {
        return places;
    }

    LiveData<List<Place>> getBookmarks() {
        return bookmarks;
    }

    LiveData<List<Place>> getResults(String query) {
        return placeDao.getResults(query);
    }

    public long insertPlace(Place place) throws ExecutionException, InterruptedException {
        return new insertPlaceAsyncTask(placeDao).execute(place).get();
    }

    public void updatePlace(Place place) {
        new updatePlaceAsyncTask(placeDao).execute(place);
    }

    public void deletePlace(Place place) {
        new deletePlaceAsyncTask(placeDao).execute(place);
    }

    public void insertBookmark(Bookmark bookmark) {
        new insertBookmarkAsyncTask(bookmarkDao).execute(bookmark);
    }

    public void deleteBookmark(Bookmark bookmark) {
        new deleteBookmarkAsyncTask(bookmarkDao).execute(bookmark);
    }

    private static class insertPlaceAsyncTask extends AsyncTask<Place, Void, Long> {

        private PlaceDao placeDao;

        insertPlaceAsyncTask(PlaceDao placeDao) {
            this.placeDao = placeDao;
        }

        @Override
        protected Long doInBackground(final Place... places) {
            return placeDao.insert(places[0]);
        }
    }

    private static class updatePlaceAsyncTask extends AsyncTask<Place, Void, Void> {

        private PlaceDao placeDao;

        updatePlaceAsyncTask(PlaceDao placeDao) {
            this.placeDao = placeDao;
        }

        @Override
        protected Void doInBackground(final Place... places) {
            placeDao.update(places[0]);
            return null;
        }
    }

    private static class deletePlaceAsyncTask extends AsyncTask<Place, Void, Void> {

        private PlaceDao placeDao;

        deletePlaceAsyncTask(PlaceDao placeDao) {
            this.placeDao = placeDao;
        }

        @Override
        protected Void doInBackground(final Place... places) {
            placeDao.delete(places[0]);
            return null;
        }
    }

    private static class insertBookmarkAsyncTask extends AsyncTask<Bookmark, Void, Void> {

        private BookmarkDao bookmarkDao;

        insertBookmarkAsyncTask(BookmarkDao bookmarkDao) {
            this.bookmarkDao = bookmarkDao;
        }

        @Override
        protected Void doInBackground(final Bookmark... bookmarks) {
            bookmarkDao.insert(bookmarks[0]);
            return null;
        }
    }

    private static class deleteBookmarkAsyncTask extends AsyncTask<Bookmark, Void, Void> {

        private BookmarkDao bookmarkDao;

        deleteBookmarkAsyncTask(BookmarkDao bookmarkDao) {
            this.bookmarkDao = bookmarkDao;
        }

        @Override
        protected Void doInBackground(final Bookmark... bookmarks) {
            bookmarkDao.delete(bookmarks[0]);
            return null;
        }
    }
}
