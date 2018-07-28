package com.example.kudos.CS426Project;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import java.util.Objects;

import static android.app.SearchManager.QUERY;
import static com.example.kudos.CS426Project.MainActivity.appViewModel;

public class SearchableActivity extends AppCompatActivity {

    private PlaceViewAdapter placeViewAdapter;
    private SearchRecentSuggestions suggestions;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ListView listView = findViewById(R.id.list_view);
        placeViewAdapter = new PlaceViewAdapter(this, R.layout.place_item);
        listView.setAdapter(placeViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchableActivity.this, PlaceDetailActivity.class);
                intent.putExtra("place", (Place) parent.getAdapter().getItem(position));
                View view1 = view.findViewById(R.id.img_avatar);
                View view2 = view.findViewById(R.id.txtName);
                View view3 = view.findViewById(R.id.txtDesc);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SearchableActivity.this, Pair.create(view1, view1.getTransitionName()), Pair.create(view2, view2.getTransitionName()), Pair.create(view3, view3.getTransitionName())).toBundle());
            }
        });

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(QUERY);
            suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            Objects.requireNonNull(getSupportActionBar()).setTitle(query);
            final LiveData<List<Place>> myResults = appViewModel.getResults("%" + query + "%");
            myResults.observe(this, new Observer<List<Place>>() {
                @Override
                public void onChanged(@Nullable List<Place> places) {
                    placeViewAdapter.setPlaces(myResults.getValue());
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        final String query = intent.getStringExtra(QUERY);
        suggestions.saveRecentQuery(query, null);
        Objects.requireNonNull(getSupportActionBar()).setTitle(query);
        final LiveData<List<Place>> myResults = appViewModel.getResults("%" + query + "%");
        myResults.observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                placeViewAdapter.setPlaces(myResults.getValue());
            }
        });
        searchView.onActionViewCollapsed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false);

        return true;
    }
}
