package com.example.kudos.CS426Project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.example.kudos.CS426Project.MainActivity.appViewModel;

public class AddChangePlaceActivity extends AppCompatActivity {

    private Place place;
    private Bitmap bitmap;
    private Boolean is_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_change_place);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        is_change = getIntent().getBooleanExtra("is_change", false);
        if (is_change) {
            getSupportActionBar().setTitle("Change information");
            place = (Place) getIntent().getSerializableExtra("place");

            ImageView imageView = findViewById(R.id.image_add);
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
            ((EditText) findViewById(R.id.name_add)).setText(place.getName());
            ((EditText) findViewById(R.id.url_add)).setText(place.getUrl());
            ((EditText) findViewById(R.id.phone_add)).setText(place.getPhone());
            ((EditText) findViewById(R.id.location_add)).setText(place.getLocation());
            ((EditText) findViewById(R.id.description_add)).setText(place.getDescription());
        } else getSupportActionBar().setTitle("Add place");
    }

    public void btnLocationSearch_onclick(View view) {
        String name_add = ((EditText) findViewById(R.id.name_add)).getText().toString();
        if (!name_add.equals(""))
            try {
                List<Address> addresses = new Geocoder(this).getFromLocationName(name_add, 1);
                if (addresses.size() != 0) {
                    Address address = addresses.get(0);
                    if (address.getUrl() != null)
                        ((EditText) findViewById(R.id.url_add)).setText(address.getUrl());
                    if (address.getPhone() != null)
                        ((EditText) findViewById(R.id.phone_add)).setText(address.getPhone());
                    ((EditText) findViewById(R.id.location_add)).setText(address.getAddressLine(0));
                } else
                    Toast.makeText(this, "Can not find this place!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        else
            Toast.makeText(this, "Name field is required to search place", Toast.LENGTH_LONG).show();
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
                EditText name = findViewById(R.id.name_add);
                EditText location = findViewById(R.id.location_add);
                EditText description = findViewById(R.id.description_add);
                EditText url = findViewById(R.id.url_add);
                EditText phone = findViewById(R.id.phone_add);
                if (name.getText().toString().isEmpty() || location.getText().toString().isEmpty() || description.getText().toString().isEmpty() || url.getText().toString().isEmpty() || phone.getText().toString().isEmpty())
                    Toast.makeText(this, "Can not leave blank", Toast.LENGTH_SHORT).show();
                else {
                    if (!is_change) {
                        long id = 0;
                        try {
                            id = appViewModel.insertPlace(new Place("", name.getText().toString(), location.getText().toString(), description.getText().toString(), url.getText().toString(), phone.getText().toString()));
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (bitmap != null)
                            try {
                                File file = new File(getFilesDir(), "place_" + id + ".jpg");
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    } else {
                        if (bitmap != null) {
                            try {
                                File file = new File(getFilesDir(), "place_" + place.getId() + ".jpg");
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        place.setName(((EditText) findViewById(R.id.name_add)).getText().toString());
                        place.setLocation(((EditText) findViewById(R.id.location_add)).getText().toString());
                        place.setDescription(((EditText) findViewById(R.id.description_add)).getText().toString());
                        place.setUrl(((EditText) findViewById(R.id.url_add)).getText().toString());
                        place.setPhone(((EditText) findViewById(R.id.phone_add)).getText().toString());
                        appViewModel.updatePlace(place);
                        Intent intent = new Intent();
                        intent.putExtra("place", place);
                        setResult(RESULT_OK, intent);
                    }
                    finishAfterTransition();
                }
                return true;
            case R.id.action_identify:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                else initLocationAndFill();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    initLocationAndFill();
        }
    }

    private void initLocationAndFill() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                Toast.makeText(this, "Both GPS and Cellular is disable!", Toast.LENGTH_LONG).show();
        } else
            try {
                List<Address> addresses = new Geocoder(this).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() != 0) {
                    Address address = addresses.get(0);
                    if (address.getUrl() != null)
                        ((EditText) findViewById(R.id.url_add)).setText(address.getUrl());
                    if (address.getPhone() != null)
                        ((EditText) findViewById(R.id.phone_add)).setText(address.getPhone());
                    ((EditText) findViewById(R.id.location_add)).setText(address.getAddressLine(0));
                } else
                    Toast.makeText(this, "Can not find this location!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}
