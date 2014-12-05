package com.goudarziha.bigpicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<Bitmap> {

    private ArrayList<Bitmap> list;
    private Context context;

    public ImageAdapter(Context context, ArrayList<Bitmap> list) {
        super(context, R.layout.image_row, list);
        this.list = list;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.image_row, parent, false);
        }

        ImageView iv = (ImageView) convertView.findViewById(R.id.image);
        iv.setImageBitmap(list.get(position));

        return convertView;
    }
}
