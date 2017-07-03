package com.usamakzafar.trackerzandroidapplication.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckLocation;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckMenuItem;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckPicture;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckReview;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.Trucks;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //createDummyTrucks();
        startActivity(new Intent(MainActivity.this,MapsActivity.class));
    }

    private void createDummyTrucks() {

        LatLng currentPosition = new LatLng(24.7818,46.7024);

        Random random = new Random();

        for(int i=1;i<=20;i++){

            Trucks truck = new Trucks();
            truck.set_id("truck_"+i);
            truck.set_description("This is description of truck no " + i);
            truck.set_location(new TruckLocation(currentPosition.latitude  + ( (double) (random.nextInt(20) - 10) / 200),
                                                currentPosition.longitude + ( (double) (random.nextInt(20) - 10) / 200)));

            truck.setLogoURL("http://www.mnfoodtruckassociation.org/wp-content/uploads/upme/1395707137_az-canteen.jpg");
            truck.set_name("Truck no " + i);
            truck.set_type((i%4)+1);
            truck.set_open(random.nextInt()%2);

            truck.setMenuReference(createDummyMenu(truck.get_id()));
            truck.setPicturesReference(createDummyPictures(truck.get_id()));
            truck.setReviewsReference(createDummyReviews(truck.get_id()));
            truck.set_paidSubscriber(random.nextInt()%2);

            truck.setFacebook("https://www.facebook.com/foodtruck");
            truck.setTwitter("https://twitter.com/foodtruck");
            truck.setYoutube("https://www.youtube.com/user/PrestigeFoodTrucks");
            truck.setGoogle("https://plus.google.com/collection/8zqf3");

            Log.i("Truck Save Task","Reached Here: " + i);
            final Trucks trucke = truck;
            mDatabase.child("trucks").child(truck.get_id()).setValue(truck).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                        Log.i("Truck Save Task","Successfully saved: " + trucke.get_name());
                    else
                        Log.i("Truck Save Task","Unsuccessful save: " + trucke.get_name());
                }
            });

        }
    }

    private String createDummyReviews(String id) {
        String ref = "review_" + id;
        Calendar date =Calendar.getInstance();
        for (int i=1;i<=10;i++){
            date.set(2017,05,i);

            Random rand = new Random();
            TruckReview review = new TruckReview();
            review.setID("review_"+i);
            review.setTimestamp(date.getTimeInMillis());
            review.setName("Review # " + i);
            review.setPictureUrl("http://www.freeiconspng.com/uploads/female-user-icon-clip-art--30.png");
            review.setRating(rand.nextInt(5));
            review.setReview(getString(R.string.dummyReview));

            final TruckReview truckReview = review;

            mDatabase.child("reviews")
                    .child(ref)
                    .child(review.getID())
                    .setValue(review)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                        Log.i("Review Save Task", "Successfully saved: " + truckReview.getName());
                    else
                        Log.i("Review Save Task","Unsuccessful save: " + truckReview.getName());
                }
            });
        }
        return ref;
    }

    private String createDummyPictures(String id) {
        String ref = "pictures_" + id;

        for (int i=1;i<=10;i++) {

            TruckPicture picture = new TruckPicture();

            picture.setPictureID("p_"+i);
            picture.setPictureURL("https://image.freepik.com/free-vector/retro-food-truck_23-2147530708.jpg");

            mDatabase.child(getString(R.string.pictures_table_name))
                    .child(ref)
                    .child(picture.getPictureID())
                    .setValue(picture)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                        Log.i("Picture Save Task", "Successfully saved");
                    else
                        Log.i("Picture Save Task","Unsuccessful save");
                }
            });

        }

        return ref;
    }

    private String createDummyMenu(String id) {
        String ref = "menu_" + id;
        for (int i=1;i<=10;i++) {

            final TruckMenuItem menuItem = new TruckMenuItem();
            menuItem.setItemDescription(getString(R.string.dummyReview));
            menuItem.setItemID("item_" + i);
            menuItem.setItemName("Menu Item " + i);
            menuItem.setItemPicture("http://s.eatthis-cdn.com/media/images/ext/446341906/fast-food-burger-fries.jpg");
            menuItem.setItemPrice(new Random().nextInt(100));

            mDatabase.child(getString(R.string.menu_table_name))
                    .child(ref)
                    .child(menuItem.getItemID())
                    .setValue(menuItem)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                        Log.i("Menu Save Task", "Successfully saved " + menuItem.getItemID());
                    else
                        Log.i("Menu Save Task","Unsuccessful save" + menuItem.getItemID());
                }
            });
        }

        return ref;
    }
}
