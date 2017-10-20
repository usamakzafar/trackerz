package com.usamakzafar.trackerzandroidapplication.activity.admin;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.ReviewsAdapter;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.Statics;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckReview;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.Trucks;
import com.usamakzafar.trackerzandroidapplication.network.FirebaseHelpers;

import java.util.HashMap;

public class TruckReviewsEditActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView reviews;
    private HashMap<String, TruckReview> truckReviews;
    private DatabaseReference mDatabase;
    private Trucks truck;
    private ReviewsAdapter reviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_reviews_edit);

        reviews = (ListView) findViewById(R.id.admin_reviews_list);
        reviews.setOnItemClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        truck = Statics.activeTruckList.get(FirebaseHelpers.user.getTruck());

        LoadReviews();
    }

    private void LoadReviews() {
        truckReviews = new HashMap<>();

        mDatabase.child(getString(R.string.reviews_table_name))
                .child(truck.getReviewsReference())
                .addChildEventListener(new ChildEventListener() {


                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        try {
                            TruckReview review = dataSnapshot.getValue(TruckReview.class);
                            truckReviews.put(review.getID(),review);
                            reviewsAdapter = new ReviewsAdapter(TruckReviewsEditActivity.this, truckReviews, true);
                            reviews.setAdapter(reviewsAdapter);
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
                        TruckReview review = dataSnapshot.getValue(TruckReview.class);
                        truckReviews.remove(review.getID());
                        reviewsAdapter.notifyDataSetChanged();

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


    public void backPressed(View view){
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "Hekkoi", Toast.LENGTH_SHORT).show();
        final TruckReview review = (TruckReview) reviewsAdapter.getItem(position);
        /*AlertDialog.Builder builder = new AlertDialog.Builder(TruckReviewsEditActivity.this)
                .setTitle(getString(R.string.delete_review_prefix) + review.getName() + "?")
                .setMessage(R.string.disclaimer_review_delete)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.child(getString(R.string.reviews_table_name))
                                .child(truck.getReviewsReference())
                                .child(review.getID()).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError == null)
                                    Toast.makeText(TruckReviewsEditActivity.this, R.string.delete_successful, Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(TruckReviewsEditActivity.this, R.string.error_msg_generic, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel",null);

        builder.show();*/
    }
}
