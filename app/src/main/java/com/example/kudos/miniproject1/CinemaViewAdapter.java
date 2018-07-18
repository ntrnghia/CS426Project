package com.example.kudos.miniproject1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class CinemaViewAdapter extends ArrayAdapter<Cinema> {
    private Context context;
    private int resource;
    private ArrayList<Cinema> cinemas;

    public CinemaViewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Cinema> cinemas) {
        super(context, resource, cinemas);
        this.context = context;
        this.resource = resource;
        this.cinemas = cinemas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);

        Cinema cinema = cinemas.get(position);

        ImageView imageView = convertView.findViewById(R.id.img_avatar);
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtDesc = convertView.findViewById(R.id.txtDesc);

        if (!cinema.isAvatar_internal())
            imageView.setImageResource(context.getResources().getIdentifier(cinema.getAvatar_name(), "drawable", getContext().getPackageName()));
        else {
            try {
                File file = new File(context.getFilesDir(), cinema.getAvatar_name() + ".jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                imageView.setImageResource(R.mipmap.ic_launcher);
            }
        }

        txtName.setText(cinema.getName());
        txtDesc.setText(cinema.getDescription());

        return convertView;
    }
}