package com.example.kudos.CS426Project;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import static com.example.kudos.CS426Project.MapActivity.polylineArray;

public class GetDirections extends AsyncTask<Object, String, String> {

    private GoogleMap mMap;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        String url = (String) objects[1];
        String jsonContent = null;
        try {
            URLConnection urlConnection = new URL(url).openConnection();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
