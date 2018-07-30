package com.example.kudos.CS426Project;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Place place;
    private Address address;
    private ArrayList<Polyline> polylineArray = new ArrayList<>();
    private final ArrayList<Marker> markers = new ArrayList<>();
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        place = (Place) getIntent().getSerializableExtra("place");

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Place's location");

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                else {
                    initLocation();
                    updateAndDisplay();
                }
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            List<Address> addresses = new Geocoder(this).getFromLocationName(place.getLocation(), 1);
            if (addresses.size() != 0) {
                address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Place"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            } else
                Toast.makeText(this, "Can not find this place's location!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(MapActivity.this, "Your location has changed", Toast.LENGTH_LONG).show();
                updateAndDisplay();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
    }

    private void updateAndDisplay() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                Toast.makeText(this, "Both GPS and Cellular are disable!", Toast.LENGTH_LONG).show();
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Direction");
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);
            findViewById(R.id.show_hide).setVisibility(View.VISIBLE);
            findViewById(R.id.time_remaining).setVisibility(View.VISIBLE);
            findViewById(R.id.distance).setVisibility(View.VISIBLE);
            for (Polyline polyline : polylineArray) polyline.remove();
            polylineArray.clear();
            for (Marker marker : markers) marker.remove();
            markers.clear();
            String url = "https://maps.googleapis.com/maps/api/directions/json?" + "origin=" + location.getLatitude() + "," + location.getLongitude() +
                    "&destination=" + address.getLatitude() + "," + address.getLongitude() +
                    "&key=AIzaSyA0yF5W8HbPRNsfDRaiMTItE_1y0kjl75Q";
            new GetDirections(this, mMap, polylineArray).execute(url);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            markers.add(mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Your Location")));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    private static class GetDirections extends AsyncTask<String, Void, String> {

        private WeakReference<MapActivity> mapActivityWeakReference;
        private GoogleMap mMap;
        private ArrayList<Polyline> polylineArray;

        GetDirections(MapActivity mapActivity, GoogleMap mMap, ArrayList<Polyline> polylineArray) {
            this.mapActivityWeakReference = new WeakReference<>(mapActivity);
            this.mMap = mMap;
            this.polylineArray = polylineArray;
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonContent = null;
            try {
                URLConnection urlConnection = new URL(strings[0]).openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                jsonContent = new Scanner(inputStream).useDelimiter("\\Z").next();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonContent;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray jsonArray = new JSONObject(s).getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                String[] directionsList = new String[jsonArray.length()];
                for (int i = 0; i < directionsList.length; ++i)
                    directionsList[i] = jsonArray.getJSONObject(i).getJSONObject("polyline").getString("points");

                for (String aDirectionsList : directionsList) {
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.RED);
                    polylineOptions.width(10);
                    polylineOptions.addAll(PolyUtil.decode(aDirectionsList));

                    polylineArray.add(mMap.addPolyline(polylineOptions));
                }
                JSONArray jsonArray2 = new JSONObject(s).getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                MapActivity mapActivity = mapActivityWeakReference.get();
                ((TextView) mapActivity.findViewById(R.id.time_remaining)).setText(jsonArray2.getJSONObject(0).getJSONObject("duration").getString("text"));
                ((TextView) mapActivity.findViewById(R.id.distance)).setText(jsonArray2.getJSONObject(0).getJSONObject("distance").getString("text"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocation();
                    updateAndDisplay();
                }
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (locationListener != null)
            ((LocationManager) (Objects.requireNonNull(getSystemService(Context.LOCATION_SERVICE)))).removeUpdates(locationListener);
    }

    public void btnShow_hide(View view) {
        if (findViewById(R.id.time_remaining).isShown()) {
            ((TextView) findViewById(R.id.show_hide)).setText(R.string.show);
            findViewById(R.id.time_remaining).setVisibility(View.GONE);
            findViewById(R.id.distance).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.show_hide)).setText(R.string.hide);
            findViewById(R.id.time_remaining).setVisibility(View.VISIBLE);
            findViewById(R.id.distance).setVisibility(View.VISIBLE);
        }
    }
}