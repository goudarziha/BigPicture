package com.goudarziha.bigpicture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.GridView;
import java.util.ArrayList;

public class TestImage extends Activity {

    GridView grid;
    Bitmap b;
    ArrayList<Bitmap> images = new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_image);
        grid = (GridView) findViewById(R.id.gridView);
        Intent intent = getIntent();
        byte[] byteArray = intent.getByteArrayExtra("Image");
        b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        images.add(b);

        if (images != null) {
            ImageAdapter adapter = new ImageAdapter(this, images);
            grid.setAdapter(adapter);
        }
    }
}
