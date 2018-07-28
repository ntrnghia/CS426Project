package com.example.kudos.CS426Project;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AppViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<List<Place>> places;
    private LiveData<List<Place>> bookmarks;

    public AppViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        places = repository.getPlaces();
        bookmarks = repository.getBookmarks();
    }

    public LiveData<List<Place>> getPlaces() {
        return places;
    }

    public LiveData<List<Place>> getBookmarks() {
        return bookmarks;
    }

    public LiveData<List<Place>> getResults(String query) {
        return repository.getResults(query);
    }

    public long insertPlace(Place place) throws ExecutionException, InterruptedException {
        return repository.insertPlace(place);
    }

    public void updatePlace(Place place) {
        repository.updatePlace(place);
    }

    public void insertBookmark(Bookmark bookmark) {
        repository.insertBookmark(bookmark);
    }

    public void deletePlace(Place place) {
        repository.deletePlace(place);
    }

    public void deleteBookmark(Bookmark bookmark) {
        repository.deleteBookmark(bookmark);
    }
}
