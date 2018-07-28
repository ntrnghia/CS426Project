package com.example.kudos.CS426Project;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

import static com.example.kudos.CS426Project.MainActivity.appViewModel;

public class PlaceDetailActivity extends AppCompatActivity {

    Place place;
    Boolean isBookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        place = (Place) getIntent().getSerializableExtra("place");

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(place.getName());
        ImageView imageView = findViewById(R.id.img_avatar);
        if (!place.getAvatar_name().equals(""))
            imageView.setImageResource(getResources().getIdentifier(place.getAvatar_name(), "drawable", getPackageName()));
        else {
            try {
                File file = new File(getFilesDir(), "place_" + place.getId() + ".jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        ((TextView) findViewById(R.id.txtName)).setText(place.getName());
        ((TextView) findViewById(R.id.txtUrl)).setText(place.getUrl());
        findViewById(R.id.txtUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(place.getUrl()));
                startActivity(browserIntent);
            }
        });
        String txtPhone = "Tel: " + place.getPhone();
        ((TextView) findViewById(R.id.txtPhone)).setText(txtPhone);
        findViewById(R.id.txtPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + place.getPhone()));
                startActivity(callIntent);
            }
        });
        ((TextView) findViewById(R.id.txtLoc)).setText(place.getLocation());
        ((TextView) findViewById(R.id.txtDesc)).setText(place.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.place_detail_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_bookmark);
        if (appViewModel.getBookmarks().getValue() == null || !(appViewModel.getBookmarks().getValue()).contains(place)) {
            menuItem.setIcon(R.drawable.ic_favorite_border_black_24dp);
            isBookmark = false;
        } else {
            menuItem.setIcon(R.drawable.ic_favorite_black_24dp);
            isBookmark = true;
        }
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
                if (!isBookmark) {
                    appViewModel.insertBookmark(new Bookmark(place.getId()));
                    Toast.makeText(this, "Added this place to bookmarks", Toast.LENGTH_LONG).show();
                    item.setIcon(R.drawable.ic_favorite_black_24dp);
                    isBookmark = true;
                } else {
                    appViewModel.deleteBookmark(new Bookmark(place.getId()));
                    Toast.makeText(this, "Removed this place from bookmarks", Toast.LENGTH_LONG).show();
                    item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                    isBookmark = false;
                }
                return true;
            case R.id.action_place:
                Intent intent = new Intent(PlaceDetailActivity.this, MapActivity.class);
                intent.putExtra("place", place);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                return true;
            case R.id.action_change_info:
                intent = new Intent(this, AddChangePlaceActivity.class);
                intent.putExtra("place", place);
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
                appViewModel.deletePlace(place);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0)
            place = (Place) data.getSerializableExtra("place");
        init();
    }
}
