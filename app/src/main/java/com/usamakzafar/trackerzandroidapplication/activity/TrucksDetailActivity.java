package com.usamakzafar.trackerzandroidapplication.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.CircleTransform;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.ImageAdapter;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.MenuAdapter;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.MiscMethods;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.ReviewsAdapter;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.Statics;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckLocation;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckMenuItem;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckPicture;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckReview;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.Trucks;

import java.util.ArrayList;
import java.util.HashMap;

public class TrucksDetailActivity extends AppCompatActivity {
    private AdView mAdView;

    private int selectedView = 0;
    private DatabaseReference mDatabase;

    private ListView listView;

    private ArrayList<TruckPicture> pictures;
    private ImageAdapter imageAdapter;

    private Trucks truck;
    private HashMap<String,TruckMenuItem> menuItems;

    private MenuAdapter menuAdapter;
    private HashMap<String,TruckReview> truckReviews;
    private ReviewsAdapter reviewsAdapter;

    private ViewFlipper vf;

    private Button btn_menu, btn_pictures, btn_reviews, btn_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trucks_detail);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadButtons();

        truck = Statics.activeTruckList.get(getIntent().getStringExtra("id"));

        Toast.makeText(this, "Truck ID: " + truck.get_id(), Toast.LENGTH_SHORT).show();
        btn_toggler(null);

        fillMainHeader();

        loadAds();
    }

    private void loadButtons() {
        btn_menu = (Button) findViewById(R.id.td_btn_menu    );
        btn_pictures = (Button) findViewById(R.id.td_btn_pictures);
        btn_reviews = (Button) findViewById(R.id.td_btn_review  );
        btn_info = (Button) findViewById(R.id.td_btn_info    );
    }

    private void loadAds() {
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void fillMainHeader() {
        ImageView imageView = (ImageView) findViewById(R.id.td_truckLogo);
        TextView nameTop = (TextView) findViewById(R.id.truckName);
        TextView name = (TextView) findViewById(R.id.td_name);
        TextView dis = (TextView) findViewById(R.id.td_truckDistance);

        final String logoURL = truck.getLogoURL();

        Picasso.with(this).load(logoURL).transform(new CircleTransform()).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TrucksDetailActivity.this, ViewImage.class);
                i.putExtra("imageURL",logoURL);
                startActivity(i);
            }
        });
        nameTop.setText(truck.get_name());
        name.setText(truck.get_name());

        MiscMethods m = new MiscMethods();
        dis.setText(m.getDistanceTo(truck.get_location()));

        setPaid();
    }

    private void setPaid() {
        Button btn_reviews  = (Button) findViewById(R.id.td_btn_review);
        Button btn_menu     = (Button) findViewById(R.id.td_btn_menu);
        Button btn_pictures = (Button) findViewById(R.id.td_btn_pictures);

        if (truckIsPaidSubscriber()){
            btn_menu.setVisibility(View.VISIBLE);
            btn_pictures.setVisibility(View.VISIBLE);
        }
        else {
            btn_reviews.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar_button_left));
            setSelectedView(2);
            btn_menu.setVisibility(View.GONE);
            btn_pictures.setVisibility(View.GONE);

        }
    }

    private boolean truckIsPaidSubscriber() {
        return truck.get_paidSubscriber() == 1;
    }

    public void backPressed(View view){onBackPressed();}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_from_left_back,R.anim.left_from_right_back);
    }


    //**** Methods to set Tabs & Views ****//
    public void btn_toggler(View view){
        try {
            selectedView = Integer.valueOf((String) view.getTag());
        }catch (Exception e){e.printStackTrace();}

        setSelectedView(selectedView);
    }

    private void setSelectedView(int selectedView) {
        vf = (ViewFlipper) findViewById(R.id.vf_tdScreen);
        vf.setDisplayedChild(selectedView);

        initializeTabViews();

        switch (selectedView){
            case 0:
                setViewToMenu();
                LoadMenu();
                break;
            case 1:
                setViewToPictures();
                LoadPictures();
                break;
            case 2:
                setViewToReviews();
                LoadReviews();
                break;
            case 3:
                setViewToInfo();
                LoadInfo();
                break;
        }
    }

    private void initializeTabViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btn_menu.setBackground(getMenuDrawable(false));
            btn_pictures.setBackground(getPicturesDrawable(false));
            btn_reviews.setBackground(getReviewsDrawable(false));
            btn_info.setBackground(getInfoDrawable(false));
        } else
        {
            btn_menu.setBackgroundDrawable(getMenuDrawable(false));
            btn_pictures.setBackgroundDrawable(getPicturesDrawable(false));
            btn_reviews.setBackgroundDrawable(getReviewsDrawable(false));
            btn_info.setBackgroundDrawable(getInfoDrawable(false));
        }
    }

    private void setViewToInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btn_info.setBackground(getInfoDrawable(true));
        }else{
            btn_info.setBackgroundDrawable(getInfoDrawable(true));
        }
        btn_menu.setTextColor(Color.DKGRAY);
        btn_pictures.setTextColor(Color.DKGRAY);
        btn_reviews.setTextColor(Color.DKGRAY);
        btn_info.setTextColor(Color.WHITE);
    }

    private void setViewToReviews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btn_reviews.setBackground(getReviewsDrawable(true));
        }else{
            btn_reviews.setBackgroundDrawable(getReviewsDrawable(true));
        }
        btn_menu.setTextColor(Color.DKGRAY);
        btn_pictures.setTextColor(Color.DKGRAY);
        btn_reviews.setTextColor(Color.WHITE);
        btn_info.setTextColor(Color.DKGRAY);
    }

    private void setViewToPictures() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btn_pictures.setBackground(getPicturesDrawable(true));
        }else{
            btn_pictures.setBackgroundDrawable(getPicturesDrawable(true));
        }
        btn_menu.setTextColor(Color.DKGRAY);
        btn_pictures.setTextColor(Color.WHITE);
        btn_reviews.setTextColor(Color.DKGRAY);
        btn_info.setTextColor(Color.DKGRAY);
    }

    private void setViewToMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btn_menu.setBackground(getMenuDrawable(true));
        }else{
            btn_menu.setBackgroundDrawable(getMenuDrawable(true));
        }
        btn_menu.setTextColor(Color.WHITE);
        btn_pictures.setTextColor(Color.DKGRAY);
        btn_reviews.setTextColor(Color.DKGRAY);
        btn_info.setTextColor(Color.DKGRAY);
    }

    //******** Loaders ********//
    private void LoadMenu() {
        listView = (ListView) findViewById(R.id.truck_detail_listview);
        menuItems = new HashMap<>();

        mDatabase.child(getString(R.string.menu_table_name))
                .child(truck.getMenuReference())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        try {
                            TruckMenuItem menuItem = dataSnapshot.getValue(TruckMenuItem.class);
                            menuItems.put(menuItem.getItemID(),menuItem);
                            menuAdapter = new MenuAdapter(TrucksDetailActivity.this, menuItems);
                            listView.setAdapter(menuAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        TruckMenuItem menuItem = dataSnapshot.getValue(TruckMenuItem.class);
                        menuItems.put(menuItem.getItemID(),menuItem);
                        menuAdapter.notifyDataSetChanged();
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

    private void LoadPictures() {
        GridView gridView = (GridView) findViewById(R.id.pictures_gridview);
        pictures = new ArrayList<>();

        imageAdapter = new ImageAdapter(this, pictures);

        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(TrucksDetailActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        mDatabase.child(getString(R.string.pictures_table_name))
                .child(truck.getPicturesReference())
                .addChildEventListener(new ChildEventListener() {
                    String TAG = "DataRetrieval";

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        try {
                            TruckPicture picture = dataSnapshot.getValue(TruckPicture.class);
                            pictures.add(picture);
                            imageAdapter.notifyDataSetChanged();
                            Log.d(TAG, "Added Truck is: " + truck.get_name() + " & Loc: " + truck.get_location());

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

    private void LoadReviews() {
        listView = (ListView) findViewById(R.id.truck_detail_listview1);
        truckReviews = new HashMap<>();

        mDatabase.child(getString(R.string.reviews_table_name))
                .child(truck.getReviewsReference())
                .addChildEventListener(new ChildEventListener() {


                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        try {
                            TruckReview review = dataSnapshot.getValue(TruckReview.class);
                            truckReviews.put(review.getID(),review);
                            reviewsAdapter = new ReviewsAdapter(TrucksDetailActivity.this, truckReviews);
                            listView.setAdapter(reviewsAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        TruckReview review = dataSnapshot.getValue(TruckReview.class);
                        truckReviews.put(review.getID(),review);
                        reviewsAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "Failed to read value.", databaseError.toException());
                    }
                });

    }

    private void LoadInfo(){

        ImageView logo = (ImageView) findViewById(R.id.td_logo);
        TextView name = (TextView) findViewById(R.id.td_truckName);
        TextView desc = (TextView) findViewById(R.id.td_truckDescription);

        Picasso.with(this).load(truck.getLogoURL()).into(logo);
        name.setText(truck.get_name());
        desc.setText(truck.get_description());

    }


    //******** Methods to help set menus ********//
    private Drawable getMenuDrawable(boolean pressed) {
        if (pressed) return getResources().getDrawable(R.drawable.topbar_button_left_pressed);
        return getResources().getDrawable(R.drawable.topbar_button_left);
    }

    private Drawable getPicturesDrawable(boolean pressed) {
        if (pressed) return getResources().getDrawable(R.drawable.topbar_button_center_pressed);
        return getResources().getDrawable(R.drawable.topbar_button_center);
    }

    private Drawable getInfoDrawable(boolean pressed) {
        if (pressed)
            return getResources().getDrawable(R.drawable.topbar_button_right_pressed);
        return getResources().getDrawable(R.drawable.topbar_button_right);
    }

    private Drawable getReviewsDrawable(boolean pressed) {
        if(pressed) {
            if (truckIsPaidSubscriber())
                return getResources().getDrawable(R.drawable.topbar_button_center_pressed);
            return getResources().getDrawable(R.drawable.topbar_button_left_pressed);
        }else{
            if (truckIsPaidSubscriber())
                return getResources().getDrawable(R.drawable.topbar_button_center);
            return getResources().getDrawable(R.drawable.topbar_button_left);
        }
    }

    // Method to handle clicking of social buttons
    public void socialClicked(View view){
        String clickedTag = (String) view.getTag();

        if (clickedTag.equals("facebook")){ MiscMethods.launchLink(this,truck.getFacebook()); }
        if (clickedTag.equals("twitter")) { MiscMethods.launchLink(this,truck.getTwitter());  }
        if (clickedTag.equals("google"))  { MiscMethods.launchLink(this,truck.getGoogle());   }
        if (clickedTag.equals("youtube")) { MiscMethods.launchLink(this,truck.getYoutube());  }
    }

    // Method to start navigation to the truck
    public void startNavigation(View view) {
        TruckLocation location = truck.get_location();
        String url = "https://www.google.com/maps/dir/?api=1&destination=" + location.getLatitude() + "," + location.getLongitude();
        MiscMethods.launchLink(this,url);
    }
}
