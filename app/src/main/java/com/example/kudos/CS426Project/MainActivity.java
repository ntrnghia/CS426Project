package com.example.kudos.CS426Project;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static Resources resourcesMain;
    static AppViewModel appViewModel;
    PlaceViewAdapter placeViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ListView listView = findViewById(R.id.list_view);
        placeViewAdapter = new PlaceViewAdapter(MainActivity.this, R.layout.place_item);
        listView.setAdapter(placeViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PlaceDetailActivity.class);
                intent.putExtra("place", (Place) parent.getAdapter().getItem(position));
                View view1 = view.findViewById(R.id.img_avatar);
                View view2 = view.findViewById(R.id.txtName);
                View view3 = view.findViewById(R.id.txtDesc);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, Pair.create(view1, view1.getTransitionName()), Pair.create(view2, view2.getTransitionName()), Pair.create(view3, view3.getTransitionName())).toBundle());
            }
        });

        resourcesMain = getResources();
        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        appViewModel.getPlaces().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                if (navigationView.getMenu().getItem(0).isChecked())
                    placeViewAdapter.setPlaces(places);
            }
        });
        appViewModel.getBookmarks().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(@Nullable List<Place> places) {
                if (navigationView.getMenu().getItem(1).isChecked())
                    placeViewAdapter.setPlaces(places);
            }
        });

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddChangePlaceActivity.class);
                intent.putExtra("is_change", false);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });

        navigationView.setCheckedItem(R.id.nav_all_cinema);
        navigationView.getMenu().performIdentifierAction(R.id.nav_all_cinema, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_all_cinema:
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                Objects.requireNonNull(getSupportActionBar()).setTitle("All place");
                placeViewAdapter.setPlaces(appViewModel.getPlaces().getValue());
                ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
                return true;
            case R.id.nav_bookmarks:
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                Objects.requireNonNull(getSupportActionBar()).setTitle("Bookmarks");
                placeViewAdapter.setPlaces(appViewModel.getBookmarks().getValue());
                ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
                return true;
        }
        return false;
    }
}