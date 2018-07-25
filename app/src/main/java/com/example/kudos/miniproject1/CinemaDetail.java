package com.example.kudos.miniproject1;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.example.kudos.miniproject1.MainActivity.bookmarks;
import static com.example.kudos.miniproject1.MainActivity.cinemas;

public class CinemaDetail extends AppCompatActivity {

    Cinema cinema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_detail);

        //getWindow().setAllowEnterTransitionOverlap(true);

        cinema = (Cinema) getIntent().getSerializableExtra("cinema");

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(cinema.getName());
        }

        init();
    }

    private void init() {
        ImageView imageView = findViewById(R.id.img_avatar);
        if (!cinema.isAvatar_internal())
            imageView.setImageResource(getResources().getIdentifier(cinema.getAvatar_name(), "drawable", getPackageName()));
        else {
            try {
                File file = new File(getFilesDir(), cinema.getAvatar_name() + ".jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        ((TextView) findViewById(R.id.txtName)).setText(cinema.getName());
        ((TextView) findViewById(R.id.txtUrl)).setText(cinema.getUrl());
        findViewById(R.id.txtUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(cinema.getUrl()));
                startActivity(browserIntent);
            }
        });
        String txtPhone = "Tel: " + cinema.getPhone();
        ((TextView) findViewById(R.id.txtPhone)).setText(txtPhone);
        findViewById(R.id.txtPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + cinema.getPhone()));
                startActivity(callIntent);
            }
        });
        ((TextView) findViewById(R.id.txtLoc)).setText(cinema.getLocation());
        ((TextView) findViewById(R.id.txtDesc)).setText(cinema.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.cinema_detail_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_bookmark);
        if (!bookmarks.contains(cinema)) menuItem.setIcon(R.drawable.ic_favorite_border_black_24dp);
        else menuItem.setIcon(R.drawable.ic_favorite_black_24dp);
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
            case R.id.action_bookmark:
                if (!bookmarks.contains(cinema)) {
                    bookmarks.add(cinema);
                    Toast.makeText(this, "Added this cinema to bookmarks", Toast.LENGTH_LONG).show();
                    item.setIcon(R.drawable.ic_favorite_black_24dp);
                } else {
                    bookmarks.remove(cinema);
                    Toast.makeText(this, "Removed this cinema from bookmarks", Toast.LENGTH_LONG).show();
                    item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                }
                invalidateOptionsMenu();

                ArrayList<Integer> bookmarksInt = new ArrayList<>();
                for (int i = 0; i < bookmarks.size(); ++i)
                    bookmarksInt.add(bookmarks.get(i).getId());
                String jsonString = new Gson().toJson(bookmarksInt);
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("bookmarks", jsonString);
                editor.apply();
                return true;
            case R.id.action_place:
                Intent intent = new Intent(CinemaDetail.this, MapActivity.class);
                intent.putExtra("cinema", cinema);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                return true;
            case R.id.action_change_info:
                intent = new Intent(this, Add_Change_Cinema.class);
                intent.putExtra("cinema", cinema);
                intent.putExtra("is_change", true);
                View view1 = findViewById(R.id.img_avatar);
                View view2 = findViewById(R.id.txtName);
                View view3 = findViewById(R.id.txtUrl);
                View view4 = findViewById(R.id.txtPhone);
                View view5 = findViewById(R.id.txtLoc);
                View view6 = findViewById(R.id.txtDesc);
                startActivityForResult(intent, 0, ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(view1, view1.getTransitionName()), Pair.create(view2, view2.getTransitionName()), Pair.create(view3, view3.getTransitionName()), Pair.create(view4, view4.getTransitionName()), Pair.create(view5, view5.getTransitionName()), Pair.create(view6, view6.getTransitionName())).toBundle());
                return true;
            case R.id.action_delete:
                cinemas.remove(cinema);
                bookmarks.remove(cinema);

                Gson gson = new Gson();
                jsonString = gson.toJson(cinemas);
                editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("cinemas", jsonString);
                bookmarksInt = new ArrayList<>();
                for (int i = 0; i < bookmarks.size(); ++i)
                    bookmarksInt.add(bookmarks.get(i).getId());
                jsonString = gson.toJson(bookmarksInt);
                editor.putString("bookmarks", jsonString);
                editor.apply();

                finishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0)
            cinema = (Cinema) data.getSerializableExtra("cinema");
        init();
    }
}
