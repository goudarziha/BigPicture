package com.goudarziha.bigpicture;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity  {

    GoogleMap mGoogleMap;
    LocationManager locationManager;
    boolean isStarted = false;
    ArrayList<LatLng> routePoints = new ArrayList<LatLng>();
    PolylineOptions lineOptions;
    Polyline lineRoute;
    TextView tvWelcome;
    Bitmap snapshot;
    String struser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) { isStarted = savedInstanceState.getBoolean("isStarted"); }
        if (googleServicesAvailable()) { setContentView(R.layout.activity_main);
            if (initMap()) {
                mGoogleMap.setMyLocationEnabled(true);
                initDraw();
                ParseUser currentUser = ParseUser.getCurrentUser();
                struser = currentUser.getUsername();
                tvWelcome = (TextView) findViewById(R.id.welcomeLogin);
                tvWelcome.setText("ready to draw some pictures " + struser + "?");
                Toast.makeText(this, "This bitch is working", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Map not working", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main);
        }
    }

    private void goToLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mGoogleMap.moveCamera(update);
    }

    private boolean initMap() {
        if (mGoogleMap == null) {
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mGoogleMap = supportMapFragment.getMap();
        }
        return (mGoogleMap != null);
    }

    public boolean googleServicesAvailable() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(isAvailable == ConnectionResult.SUCCESS) { return true; }
        else if(GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, 0);
            dialog.show();
        } else { Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show(); }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            ParseUser.logOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(16)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public void goToCurrentLocation(View v) {
//        Location currentlocation = locationClient.getLastLocation();
//        if (currentlocation == null) {
//            Toast.makeText(this, "Cant get Current location", Toast.LENGTH_LONG).show();
//        } else {
//            final LatLng ll = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
//            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
//            mGoogleMap.animateCamera(update);
//            Toast.makeText(this, currentlocation.
//
// getLatitude() + " " + currentlocation.getLongitude(), Toast.LENGTH_SHORT).show();
//            Polyline route = mGoogleMap.addPolyline(new PolylineOptions().width(8).color(Color.BLUE));
//            routePoints.add(new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude()));
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    if (routePoints == null) {
                        routePoints.add(ll);
                    } else {
                        drawTrail(location);
//                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
//                        mGoogleMap.animateCamera(update);
                    }
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {}
                @Override
                public void onProviderEnabled(String s) {}
                @Override
                public void onProviderDisabled(String s) {}
            });
        }

    private void initDraw() {
        lineOptions = new PolylineOptions().width(8).color(Color.RED);
        lineRoute = mGoogleMap.addPolyline(lineOptions);
    }

    public void drawTrail(Location location) {
        LatLng newPoint = new LatLng(location.getLatitude(), location.getLongitude());
        routePoints.add(newPoint);
        lineRoute.setPoints(routePoints);
    }

    public void stop(View v) {
        isStarted = false;
    }

    public void captureScreen(View v) {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                OutputStream fout = null;
                String filePath = System.currentTimeMillis() + ".jpeg";
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                try {
                    fout = openFileOutput(filePath, MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    if (byteArray != null) {
                        ParseFile file = new ParseFile(struser + ".jpg", byteArray);
                        file.saveInBackground();
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), TestImage.class);
                        intent.putExtra("Image", byteArray);
                        startActivity(intent);
                    }

                    fout.flush();
                    fout.close();


                } catch (FileNotFoundException e) {
                    Log.d("ImageCapture", "FileNotFoundException");
                    Log.d("ImageCapture", e.getMessage());
                    filePath = "";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                openShareImageDialog(filePath);
            }
        };
        mGoogleMap.snapshot(callback);
    }

    public void openShareImageDialog(String filePath)
    {
        File file = this.getFileStreamPath(filePath);

        if(!filePath.equals(""))
        {
            final ContentValues values = new ContentValues(2);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            final Uri contentUriFile = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(android.content.Intent.EXTRA_STREAM, contentUriFile);
            startActivity(Intent.createChooser(intent, "Share Image"));
        }

    }
}
