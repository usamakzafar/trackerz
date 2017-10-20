package com.usamakzafar.trackerzandroidapplication.activity.admin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.CircleTransform;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.MiscMethods;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.Statics;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckLocation;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckMenuItem;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckPicture;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckReview;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.Trucks;
import com.usamakzafar.trackerzandroidapplication.network.FirebaseHelpers;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Trucks truck;

    public static HashMap<String,TruckMenuItem> menuItemsList;
    public static HashMap<String,TruckReview>   truckReviewsList;
    public static ArrayList<TruckPicture>       picturesList;
    private int followersCount=0;

    private TextView truckName;
    private TextView truckType;
    private TextView reviews;
    private TextView reviews2;
    private TextView reviews3;
    private TextView menuCount;
    private TextView menuCount2;
    private TextView followers;
    private TextView pictures;

    private Switch truckOpen;
    private Switch truckTracking;

    private TruckLocation truckLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (FirebaseHelpers.user == null) FirebaseHelpers.loadUser();
        truck = Statics.activeTruckList.get(FirebaseHelpers.user.getTruck());

        TextView t = (TextView) findViewById(R.id.admin_truckname);
        t.setText("Signed in as " + mAuth.getCurrentUser().getEmail() + "\nTruck ID: " + truck.get_id());

        setViews();
    }


    private void setViews() {
        truckName  = (TextView) findViewById(R.id.admin_truckname);
        truckType  = (TextView) findViewById(R.id.admin_trucktype);
        reviews    = (TextView) findViewById(R.id.admin_reviews_count);
        reviews2   = (TextView) findViewById(R.id.admin_reviews_count2);
        reviews3   = (TextView) findViewById(R.id.td_ratings);
        menuCount  = (TextView) findViewById(R.id.admin_menu_count);
        menuCount2 = (TextView) findViewById(R.id.admin_menu_count2);
        followers  = (TextView) findViewById(R.id.admin_followers_count);
        pictures   = (TextView) findViewById(R.id.admin_pictures_count);

        truckName .setText(truck.get_name());
        truckType .setText(Statics.Truck_type_name[truck.get_type()]);
        getCounts();
        initSwitches();
        updateValues();
        try {
            ImageView imageView = (ImageView) findViewById(R.id.admin_trucklogo);
            Picasso.with(this).load(truck.getLogoURL()).transform(new CircleTransform()).into(imageView);
        } catch (Exception e){Log.i("LoadingPicture", "Some error occurred during picture loading,\n Exception: ");  e.printStackTrace();}
    }

    private void initSwitches() {
        truckOpen = (Switch) findViewById(R.id.admin_open_switch);
        truckTracking = (Switch) findViewById(R.id.admin_location_switch);
    }

    private void updateValues() {
        reviews   .setText(String.valueOf(truckReviewsList.size() ));
        reviews2  .setText(String.valueOf(truckReviewsList.size() ));
        reviews3  .setText(MiscMethods.inBrackets(truckReviewsList.size() ));
        menuCount .setText(String.valueOf(menuItemsList.size()    ));
        menuCount2.setText(String.valueOf(menuItemsList.size()    ));
        followers .setText(String.valueOf(truck.getFollowers()    ));
        pictures  .setText(String.valueOf(picturesList.size()     ));
        truckOpen.setChecked(truck.get_open() == 1);

        fillReviewStars();
    }


    private void fillReviewStars() {

        CheckBox s1 = (CheckBox) findViewById(R.id.star1);
        CheckBox s2 = (CheckBox) findViewById(R.id.star2);
        CheckBox s3 = (CheckBox) findViewById(R.id.star3);
        CheckBox s4 = (CheckBox) findViewById(R.id.star4);
        CheckBox s5 = (CheckBox) findViewById(R.id.star5);

        //Checking stars for reviews
        int rating = MiscMethods.getReviewsAverage(truckReviewsList);
        if (rating >=1)
            s1.setChecked(true);
        if (rating >=2)
            s2.setChecked(true);
        if (rating >=3)
            s3.setChecked(true);
        if (rating >=4)
            s4.setChecked(true);
        if (rating >=5)
            s5.setChecked(true);

    }

    public void editTruck (View view){
        startActivity(new Intent(AdminActivity.this,TruckEditActivity.class));
    }

    public void editMenu(View view) {
        startActivity(new Intent(AdminActivity.this, TruckMenuEditActivity.class));
    }

    public void editReviews(View view){
        startActivity(new Intent(AdminActivity.this, TruckReviewsEditActivity.class));
    }

    public void editPictures(View view){
        startActivity(new Intent(AdminActivity.this, TruckPicturesEditActivity.class));
    }

    public void backPressed(View view){this.finish();}

    public void logOut(View view){
        mAuth.signOut();
        this.finish();
    }

    public void getCounts() {

        menuItemsList     = new HashMap<>() ;
        truckReviewsList  = new HashMap<>() ;
        picturesList      = new ArrayList<>() ;

        LoadMenu();
        LoadReviews();
        LoadPictures();

    }

    private void LoadPictures() {

        mDatabase.child(getString(R.string.pictures_table_name))
                .child(truck.getPicturesReference())
                .addChildEventListener(new ChildEventListener() {
                    String TAG = "DataRetrieval";

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        try {
                            TruckPicture picture = dataSnapshot.getValue(TruckPicture.class);
                            picturesList.add(picture);        updateValues();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

    private void LoadMenu() {

        mDatabase.child(getString(R.string.menu_table_name))
                .child(truck.getMenuReference())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        try {
                            TruckMenuItem menuItem = dataSnapshot.getValue(TruckMenuItem.class);
                            menuItemsList.put(menuItem.getItemID(),menuItem);
                            updateValues();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        TruckMenuItem menuItem = dataSnapshot.getValue(TruckMenuItem.class);
                        menuItemsList.put(menuItem.getItemID(),menuItem);
                        updateValues();

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }

    private void LoadReviews() {
        mDatabase.child(getString(R.string.reviews_table_name))
                .child(truck.getReviewsReference())
                .addChildEventListener(new ChildEventListener() {


                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        try {
                            TruckReview review = dataSnapshot.getValue(TruckReview.class);
                            truckReviewsList.put(review.getID(),review);
                            updateValues();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        TruckReview review = dataSnapshot.getValue(TruckReview.class);
                        truckReviewsList.put(review.getID(),review);
                        updateValues();

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }

    public void switchToggled(View view){
        if(truckOpen.isChecked())truck.set_open(1);
        else truck.set_open(0);

        startUpdatingTruckLocation(truckTracking.isChecked());

        saveTruck();
    }

    private void saveTruck() {

        mDatabase.child("trucks").child(truck.get_id()).setValue(truck).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Log.i("Truck Save Task","Successfully saved: " + truck.get_name());
                else
                    Log.i("Truck Save Task","Unsuccessful save: " + truck.get_name());
            }
        });
    }

    private void startUpdatingTruckLocation(boolean checked) {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        logLocation();
        checkPermissions();

    }

    private void checkPermissions() {
        truckLocation = truck.get_location();

        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLoc != null){
                    saveLocationtoTruck(lastLoc);
                }

            } else {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }
    }

    private void saveLocationtoTruck(Location location) {
        if(location!=null) {
            truckLocation.setLatitude(location.getLatitude());
            truckLocation.setLongitude(location.getLongitude());
        }
        truck.set_location(truckLocation);
        saveTruck();
    }

    private LocationManager locationManager;
    private LocationListener locationListener;
    private void logLocation() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                saveLocationtoTruck(location);
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

}
