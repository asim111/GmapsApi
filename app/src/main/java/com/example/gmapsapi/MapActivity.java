package com.example.gmapsapi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private final static String FINE_LOCTION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final static String COURSE_LOCTION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final static int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Boolean locationpermissiongranted = false;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private EditText searchtext;
    private ImageView search,backtoorigionallocation;
//    MaterialSearchBar searchBar;
//    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds( new LatLng(-40,-168),new LatLng(71,136));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        searchtext  = (EditText)findViewById(R.id.search_text);
        search = findViewById(R.id.search);
        backtoorigionallocation = findViewById(R.id.gps);
        getLoctaionrequest();
    }



    private void init() {

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate(v);
                hideSoftKeyboards(v);
            }
        });
        backtoorigionallocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

    }

    private void geoLocate(View view) {
       String searchstr = searchtext.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchstr, 1);

        } catch (IOException e) {
            Toast.makeText(MapActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (list.size() > 0) {
            Address address = list.get(0);
//            Toast.makeText(MapActivity.this, "" + address.toString(), Toast.LENGTH_SHORT).show();
            movercamera(new LatLng(address.getLatitude(),address.getLongitude()),15f,address.getAddressLine(0));
        }
        hideSoftKeyboards(view);
    }

    //initalize map here...
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if (locationpermissiongranted) {
                    getDeviceLocation();
                    if (ActivityCompat.checkSelfPermission(MapActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(MapActivity.this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        locationpermissiongranted = false;
                        return;
                    }
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    init();
                }
            }
        });//onMapReady method ends here....

    }//init ends here

    private void getLoctaionrequest() {
        String[] permissions = {FINE_LOCTION, COURSE_LOCTION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCTION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCTION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationpermissiongranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
            locationpermissiongranted = true;
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationpermissiongranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationpermissiongranted = false;
                            return;
                        }
                    }
                    locationpermissiongranted = true;
                    //intialize map here
                    initMap();
                }
        }
    }

    //getDevicelocation method...
    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (locationpermissiongranted) {
                Task<Location> location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Location currentlocation = (Location) task.getResult();
                            movercamera(new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude()), 15f,"My Location");
                        } else {
                            Toast.makeText(MapActivity.this, "could not find the current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Toast.makeText(MapActivity.this, "error occour:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }//getlocationdevice ends here...

    private void movercamera(LatLng latLng, float zoom ,String title) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions();
            options.position(latLng).title(title);
            map.addMarker(options);
        }
    }
    private void hideSoftKeyboards(View v){
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


}
