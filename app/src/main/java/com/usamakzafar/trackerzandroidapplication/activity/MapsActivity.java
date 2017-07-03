package com.usamakzafar.trackerzandroidapplication.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.TrucksAdapter;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.MiscMethods;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.RoundedCornersTransform;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.Statics;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.Trucks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ListView listView;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private MarkerOptions marker;
    private LatLng currentPosition;
    private DatabaseReference mDatabase;
    private HashMap<String, Trucks> allTruckList;
    private HashMap<String, String> truckMarkers;

    private boolean mapIsActive = true;
    private ViewFlipper vf;

    private int truckFilter_state = 0;
    private boolean truckFilter_tracking = false;
    private boolean truckFilter_open = false;
    private boolean truckFilter_events = false;
    private boolean truckFilter_trucksOnly = false;
    private boolean truckFilter_all = false;

    private TrucksAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        LoadTrucks();

        setContentView(R.layout.activity_maps);
        listView = (ListView) findViewById(R.id.listView);

        vf = (ViewFlipper) findViewById(R.id.vf_mapScreen);
        vf.setDisplayedChild(0);

        if (mapIsActive) {
            vf.setDisplayedChild(0);
            allTruckList = new HashMap<>();
            truckMarkers = new HashMap<>();


            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            logLocation();
            checkPermissions();
        } else {
            vf.setDisplayedChild(1);
        }
    }

    private void checkPermissions() {

        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLoc != null)
                    currentPosition = new LatLng(lastLoc.getLatitude(),
                            lastLoc.getLongitude());

            } else {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }
    }

    private void logLocation() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        marker = new MarkerOptions();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                refreshViews();
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

        // Add a marker in Riyadh and move the camera
        LatLng Riyadh = new LatLng(24.7784259, 46.7022346);
        //mMap.addMarker(new MarkerOptions().position(Riyadh).title("Riyadh"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location a = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (a != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(a.getLatitude(),a.getLongitude()), 15));
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Riyadh, 15));
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.mapstyle));

        refreshViews();

    }

    private void LoadTrucks() {
        // Read from the database
        mDatabase.child("trucks").addChildEventListener(new ChildEventListener() {
            String TAG = "DataRetrieval";

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Trucks truck = dataSnapshot.getValue(Trucks.class);
                    allTruckList.put(truck.get_id(), truck);
                    refreshViews();
                    Log.d(TAG, "Added Truck is: " + truck.get_name() + " & Loc: " + truck.get_location());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Trucks truck = dataSnapshot.getValue(Trucks.class);
                allTruckList.put(truck.get_id(),truck);
                refreshViews();
                Log.d(TAG, "Truck is: " + truck.get_name() + " & Loc: " + truck.get_location());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void refreshViews() {
        if(Statics.activeTruckList == null)
            Statics.activeTruckList = allTruckList;

        if(mapIsActive) {
            updateMap();
        } else {
            updateListView();
        }
    }

    // Method to update listview
    private void updateListView() {
        listAdapter = new TrucksAdapter(this, Statics.activeTruckList);
        listView.setAdapter(listAdapter);

    }

    //*****************MAP METHODS***********************//
    private void updateMap() {

        // Clear the map
        mMap.clear();
        AddUserMarker();

        for (Map.Entry<String, Trucks> entry : Statics.activeTruckList.entrySet()) {
            final Trucks truck = entry.getValue();

            // Create Marker
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(truck.get_location().getLatitude(), truck.get_location().getLongitude()))
                    .title(truck.get_name())
                    .snippet(truck.get_description())
                    .icon(MiscMethods.getMarkerIcon(truck.get_type(), truck.get_open() == 1));


            // Add marker to map
            Marker marker = mMap.addMarker(markerOptions);

            // Update list to keep track of all markers
            truckMarkers.put(marker.getId(), truck.get_id());

            // Add info window and info window click listener to every marker
            mMap.setOnInfoWindowClickListener(truckInfoWindowListener);
            mMap.setInfoWindowAdapter(truckInfoWindow);

        }
    }

    GoogleMap.OnInfoWindowClickListener truckInfoWindowListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Intent intent = new Intent(MapsActivity.this,TrucksDetailActivity.class);
            intent.putExtra("id",truckMarkers.get(marker.getId()));
            startActivity(intent);
            overridePendingTransition(R.anim.left_from_right,R.anim.right_from_left);
        }
    };

    GoogleMap.InfoWindowAdapter truckInfoWindow = new GoogleMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {

            //Get Truck Info
            Trucks truck = allTruckList.get(truckMarkers.get(marker.getId()));

            if (truck != null) {
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.infowindow, null);

                // Getting the position from the marker
                LatLng latLng = marker.getPosition();

                // Getting reference to the TextView to set name
                TextView name = (TextView) v.findViewById(R.id.t1);

                // Getting reference to the TextView to set description
                TextView description = (TextView) v.findViewById(R.id.t2);

                // Getting the Imageview
                ImageView image = (ImageView) v.findViewById(R.id.iw_image);

                // Setting the Name
                name.setText(truck.get_name());

                // Setting the OpenState
                description.setText(truck.get_description());

                // Setting the Image
                Picasso.with(MapsActivity.this).load(truck.getLogoURL()).transform(new RoundedCornersTransform()).into(image);

                // Returning the view containing InfoWindow contents
                return v;
            }
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    };

    //^^^^^^^^^^^^^^^^MAP METHODS^^^^^^^^^^^^^^^^^^^^^^^^//


    // To add Marker to user's location
    private void AddUserMarker() {
        if(currentPosition!=null) {
            Statics.userLocation = currentPosition;
            marker.position(currentPosition)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle));
            mMap.addMarker(marker);
        }
    }

    // Method to toggle between Map view and List View
    public void flipView(View view){
        int v = Integer.valueOf((String) view.getTag() );

        if(v<2) {
            vf.setDisplayedChild(v);
            mapIsActive = v == 0;
        }

        LinearLayout other;
        switch (v) {
            case 0:                                                                                 // Toggle Between Map and List View
                other = (LinearLayout) findViewById(R.id.cb_list);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    other.setBackground(getResources().getDrawable(R.drawable.bottom_button_right));
                    view.setBackground(getResources().getDrawable(R.drawable.bottom_btn_left_pressed));
                } else {
                    other.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button_right));
                    view.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_btn_left_pressed));
                }
                break;

            case 1:                                                                                 // Toggle Between Map and List View
                other = (LinearLayout) findViewById(R.id.cb_map);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(getResources().getDrawable(R.drawable.bottom_btn_right_pressed));
                    other.setBackground(getResources().getDrawable(R.drawable.bottom_button_left));
                } else {
                    view.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_btn_right_pressed));
                    other.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button_left));
                }
                break;
        }

        refreshViews();
    }

    // Method Called by View
    public void updateFilter(View view){
        int tag = Integer.valueOf((String) view.getTag());

        if(tag != truckFilter_state) {
            if (tag < 3) {
                truckFilter_state = tag;
            } else {
                switch (tag) {
                    case 3:
                        truckFilter_tracking = ((CheckBox) view).isChecked();
                        break;
                    case 4:
                        truckFilter_open = ((CheckBox) view).isChecked();
                        break;
                }
            }
            updateTruckFiltering();
        }
    }

    private void updateTruckFiltering() {

        filterActiveTrucksAccordingToSelectedStateFromTopBar();

        filterActiveTrucksAccordingToSelectedStateFromBottomBar();

        refreshViews();
    }

    private void filterActiveTrucksAccordingToSelectedStateFromBottomBar() {
        if (truckFilter_open){
            List<Trucks> rem = new ArrayList<>();
            for(Map.Entry<String, Trucks> entry : Statics.activeTruckList.entrySet()) {
                Trucks truck = entry.getValue();
                Log.i("Trucks in Active List", "Truck: " + truck.get_name() );
                if(truck.get_open() != 1)
                    rem.add(truck);
            }
            for (Trucks t : rem) {
                Log.i("Removing Truck", "Truck: " + t.get_name());
                Statics.activeTruckList.remove(t.get_id());
            }

        }
        if (truckFilter_tracking){

            //TODO show only the trucks that the user is tracking

        }
    }

    private void filterActiveTrucksAccordingToSelectedStateFromTopBar() {
        switch (truckFilter_state){
            case 0:
                Statics.activeTruckList = new HashMap<>(allTruckList);
                break;
            case 1:
                Statics.activeTruckList = new HashMap<>();

                for(Map.Entry<String, Trucks> entry : allTruckList.entrySet()) {
                    Trucks truck = entry.getValue();
                    if(truck.get_type() != 3)
                        Statics.activeTruckList.put(truck.get_id(),truck);
                }

                break;
            case 2:
                Statics.activeTruckList = new HashMap<>();
                for(Map.Entry<String, Trucks> entry : allTruckList.entrySet()) {
                    Trucks truck = entry.getValue();
                    if(truck.get_type() == 3)
                        Statics.activeTruckList.put(truck.get_id(),truck);
                }
                break;
        }
    }
}
