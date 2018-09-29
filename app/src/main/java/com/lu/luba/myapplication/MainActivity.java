package com.lu.luba.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private LocationListener listener;
    private LocationManager manager;
     private Button sendButton;
    private TextView tView, latitude, longitude, adress;
    private TextView otr;

    private ProgressBar mProgressBar;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.progressBar);
        otr = findViewById(R.id.otr);
        mProgressBar.setVisibility(View.VISIBLE);
        otr.setVisibility(View.VISIBLE);

        sendButton = findViewById(R.id.button);
        //String sNewMyText="";
        sendButton.setEnabled(false);
        manager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Location ", location.toString());
                boolean enabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if(enabled) {


                    tView = findViewById(R.id.tView);
                    latitude = findViewById(R.id.latitude);
                    longitude = findViewById(R.id.longitude);
                    adress = findViewById(R.id.adress);

                    // tView.setText(Double.toString(location.getLatitude()) + "_____" + Double.toString(location.getLongitude()) + "  " + getCompleteAddressString(location.getLatitude(), location.getLongitude()));
                    latitude.setText("Широта:\n"+Double.toString(location.getLatitude()));
                    longitude.setText("Довгота:\n"+Double.toString(location.getLongitude()));
                    adress.setText("Адреса:\n"+getCompleteAddressString(location.getLatitude(), location.getLongitude()));

                    otr.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);

                } else {Toast.makeText(MainActivity.this, "перевірте gps", Toast.LENGTH_LONG); tView.setText("перевірте gps");}

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };


        if (Build.VERSION.SDK_INT < 23) {
            manager.requestLocationUpdates(manager.GPS_PROVIDER, 0, 0, listener);

        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            } else {
                manager.requestLocationUpdates(manager.GPS_PROVIDER, 0, 0, listener);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                manager.requestLocationUpdates(manager.GPS_PROVIDER, 0, 0, listener);
            }
        }
    }

    @SuppressLint("LongLogTag")
    public String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current location address", strReturnedAddress.toString());
            } else {
                Log.w("My Current location address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current location address", "Cannot get Address!");
        }
        return strAdd;
    }
    public void onClick(View view) {

        String txt = latitude.getText()+"   "+longitude.getText()+"   "+adress.getText();
       if (txt.isEmpty()) txt="";
        Intent sendIntent = new Intent();// создаем новый Intent
        sendIntent.setAction(Intent.ACTION_SEND);// добавляем действие ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, txt); // добавляем текст для передачи
        sendIntent.setType("text/plain");// указываем тип данных
        startActivity(sendIntent);// запускаем активити
    }





}

