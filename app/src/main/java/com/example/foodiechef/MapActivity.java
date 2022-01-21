package com.example.foodiechef;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.foodiechef.AddFoodActivity.location_here;
import static com.example.foodiechef.CheckoutActivity.delivery_add;
import static com.example.foodiechef.ui.about.AboutFragment.latitude;
import static com.example.foodiechef.ui.about.AboutFragment.location;
import static com.example.foodiechef.ui.about.AboutFragment.longitude;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private Geocoder geocoder;
    private List<Address> addresses;
    private TextView address;
    private ImageView next;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        address = findViewById(R.id.address);
        next = findViewById(R.id.next);
        geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        fetchLastLocation();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void fetchLastLocation(){
        if(ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapActivity.this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;
                    try {
                        addresses = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),1);
                        address.setText(addresses.get(0).getAddressLine(0));
                        Log.e("Address",addresses.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(currentLocation.getLatitude()+ " "+currentLocation.getLongitude());
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(MapActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        final LatLng[] latLng = {new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())};
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng[0]));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng[0],15));
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                latLng[0] = googleMap.getCameraPosition().target;
                latitude = latLng[0].latitude;
                longitude = latLng[0].longitude;
                try {
                    addresses = geocoder.getFromLocation(latLng[0].latitude, latLng[0].longitude,1);
                    if (addresses.get(0).getLocality().equals("Ahmedabad")){
                        address.setText(addresses.get(0).getAddressLine(0));
                        if(getIntent().getStringExtra("from")==null){
                            location.setText(addresses.get(0).getAddressLine(0));
                        }
                        else if (getIntent().getStringExtra("from").equals("checkout")) {
                            delivery_add.setText(addresses.get(0).getAddressLine(0));
                        }
                        else if (getIntent().getStringExtra("from").equals("AddFood")){
                            location_here.setText(addresses.get(0).getAddressLine(0));
                        }
                    }else{
                        Toast.makeText(MapActivity.this,"We don't provide service here",Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /*final MarkerOptions markerOptions = new MarkerOptions().position(latLng[0]).title("I am here");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng[0]));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng[0],15));
        googleMap.addMarker(markerOptions);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                MarkerOptions markerOptions1 = new MarkerOptions().position(latLng).title("I am here");
                googleMap.addMarker(markerOptions1);
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    if (addresses.get(0).getLocality().equals("Ahmedabad")){
                        address.setText(addresses.get(0).getAddressLine(0));
                        location.setText(addresses.get(0).getAddressLine(0));
                    }else{
                        Toast.makeText(MapActivity.this,"We don't provide service here",Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
