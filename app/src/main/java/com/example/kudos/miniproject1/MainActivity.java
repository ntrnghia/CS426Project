package com.example.kudos.miniproject1;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static final ArrayList<Place> PLACES = new ArrayList<>();
    static final ArrayList<Place> bookmarks = new ArrayList<>();
    private static final int idInit = 3;
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

        getSupportActionBar().setTitle("All place");

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (PLACES.size() == 0) {
            SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (!sharedPreferences.contains("PLACES")) {
                editor.putString("PLACES", new Scanner(getResources().openRawResource(R.raw.places)).useDelimiter("\\Z").next());
                editor.apply();
            }
            if (!sharedPreferences.contains("id")) {
                editor.putInt("id", idInit);
                editor.apply();
            }
            Gson gson = new Gson();
            ArrayList<Place> places = gson.fromJson(sharedPreferences.getString("PLACES", ""), new TypeToken<ArrayList<Place>>() {
            }.getType());
            ArrayList<Integer> Bookmarks = gson.fromJson(sharedPreferences.getString("bookmarks", ""), new TypeToken<ArrayList<Integer>>() {
            }.getType());
            if (places != null)
                if (Bookmarks == null) PLACES.addAll(places);
                else for (int i = 0; i < places.size(); ++i) {
                    PLACES.add(places.get(i));
                    for (int j = 0; j < Bookmarks.size(); ++j)
                        if (places.get(i).getId() == Bookmarks.get(j))
                            bookmarks.add(places.get(i));
                }
        }

        navigationView.setCheckedItem(R.id.nav_all_cinema);
        navigationView.getMenu().performIdentifierAction(R.id.nav_all_cinema, 0);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddChangePlaceActivity.class);
                intent.putExtra("is_change", false);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        placeViewAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
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
                getSupportActionBar().setTitle("All place");
                ListView listView = findViewById(R.id.list_view);
                placeViewAdapter = new PlaceViewAdapter(MainActivity.this, R.layout.place_item, PLACES);
                listView.setAdapter(placeViewAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, PlaceDetailActivity.class);
                        intent.putExtra("place", PLACES.get(position));
                        View view1 = view.findViewById(R.id.img_avatar);
                        View view2 = view.findViewById(R.id.txtName);
                        View view3 = view.findViewById(R.id.txtDesc);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, Pair.create(view1, view1.getTransitionName()), Pair.create(view2, view2.getTransitionName()), Pair.create(view3, view3.getTransitionName())).toBundle());
                    }
                });
                ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
                return true;
            case R.id.nav_bookmarks:
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                getSupportActionBar().setTitle("Bookmarks");
                listView = findViewById(R.id.list_view);
                placeViewAdapter = new PlaceViewAdapter(MainActivity.this, R.layout.place_item, bookmarks);
                listView.setAdapter(placeViewAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, PlaceDetailActivity.class);
                        intent.putExtra("place", bookmarks.get(position));
                        View view1 = view.findViewById(R.id.img_avatar);
                        View view2 = view.findViewById(R.id.txtName);
                        View view3 = view.findViewById(R.id.txtDesc);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, Pair.create(view1, view1.getTransitionName()), Pair.create(view2, view2.getTransitionName()), Pair.create(view3, view3.getTransitionName())).toBundle());
                    }
                });
                ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
                return true;
        }
        return false;
    }
}