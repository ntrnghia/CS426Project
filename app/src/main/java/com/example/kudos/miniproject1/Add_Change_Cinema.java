package com.example.kudos.miniproject1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static com.example.kudos.miniproject1.MainActivity.cinemas;

public class Add_Change_Cinema extends AppCompatActivity {

    Cinema cinema;
    Bitmap bitmap;
    Boolean is_change;
    //hehehehe

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_change_cinema);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        is_change = getIntent().getBooleanExtra("is_change", false);
        if (is_change) {
            getSupportActionBar().setTitle("Change information");
            cinema = (Cinema) getIntent().getSerializableExtra("cinema");

            ImageView imageView = findViewById(R.id.image_add);
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
            ((EditText) findViewById(R.id.name_add)).setText(cinema.getName());
            ((EditText) findViewById(R.id.url_add)).setText(cinema.getUrl());
            ((EditText) findViewById(R.id.phone_add)).setText(cinema.getPhone());
            ((EditText) findViewById(R.id.location_add)).setText(cinema.getLocation());
            ((EditText) findViewById(R.id.description_add)).setText(cinema.getDescription());
        } else getSupportActionBar().setTitle("Add cinema");
    }

    public void btnAddPicture_onclick(View view) {
        Intent imageIntent = new Intent(Intent.ACTION_PICK);
        imageIntent.setType("image/*");
        startActivityForResult(imageIntent, 0);
    }

    public void btnTakePicture_onclick(View view) {
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(imageIntent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_change_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
            case R.id.action_done:
                if (!is_change) {
                    EditText name = findViewById(R.id.name_add);
                    EditText location = findViewById(R.id.location_add);
                    EditText description = findViewById(R.id.description_add);
                    EditText url = findViewById(R.id.url_add);
                    EditText phone = findViewById(R.id.phone_add);
                    if (name.getText().toString().isEmpty() || location.getText().toString().isEmpty() || description.getText().toString().isEmpty() || url.getText().toString().isEmpty() || phone.getText().toString().isEmpty())
                        Toast.makeText(this, "Can not leave blank", Toast.LENGTH_SHORT).show();
                    else {
                        int id = getSharedPreferences("data", MODE_PRIVATE).getInt("id", 0);
                        if (bitmap != null)
                            try {
                                File file = new File(getFilesDir(), "cinema_" + id + ".jpg");
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        Cinema new_cinema = new Cinema(id, "cinema_" + id++, true, name.getText().toString(), location.getText().toString(), description.getText().toString(), url.getText().toString(), phone.getText().toString());
                        cinemas.add(new_cinema);
                        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putInt("id", id);
                        editor.apply();
                        finishAfterTransition();
                    }
                } else {
                    cinema = cinemas.get(cinemas.indexOf(cinema));
                    if (bitmap != null) {
                        try {
                            File file = new File(getFilesDir(), cinema.getAvatar_name() + ".jpg");
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        cinema.setAvatar_internal(true);
                    }
                    cinema.setName(((EditText) findViewById(R.id.name_add)).getText().toString());
                    cinema.setLocation(((EditText) findViewById(R.id.location_add)).getText().toString());
                    cinema.setDescription(((EditText) findViewById(R.id.description_add)).getText().toString());
                    cinema.setUrl(((EditText) findViewById(R.id.url_add)).getText().toString());
                    cinema.setPhone(((EditText) findViewById(R.id.phone_add)).getText().toString());
                    Intent intent = new Intent();
                    intent.putExtra("cinema", cinema);
                    setResult(RESULT_OK, intent);
                    finishAfterTransition();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                ((ImageView) findViewById(R.id.image_add)).setImageURI(data.getData());
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 1) {
                ((ImageView) findViewById(R.id.image_add)).setImageBitmap((Bitmap) Objects.requireNonNull(data.getExtras()).get("data"));
                bitmap = (Bitmap) data.getExtras().get("data");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Gson gson = new Gson();
        String jsonString = gson.toJson(cinemas);
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("cinemas", jsonString);
        editor.apply();
    }
}
