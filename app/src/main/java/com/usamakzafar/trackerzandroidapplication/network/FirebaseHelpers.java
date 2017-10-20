package com.usamakzafar.trackerzandroidapplication.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.MiscMethods;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckReview;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.Trucks;
import com.usamakzafar.trackerzandroidapplication.activity.SigninActivity;
import com.usamakzafar.trackerzandroidapplication.activity.admin.TruckEditActivity;
import com.usamakzafar.trackerzandroidapplication.activity.interfaces.ReviewsInterface;
import com.usamakzafar.trackerzandroidapplication.activity.interfaces.PictureUploadInterface;
import com.usamakzafar.trackerzandroidapplication.models.User;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by usamazafar on 30/08/2017.
 */

public class FirebaseHelpers {

    private static FirebaseAuth mAuth;
    private static DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    public static User user;

    private Context context;
    private String userImageURL;

    public FirebaseHelpers(Context c){
        this.context = c;
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public static final int USER_STATUS_SIGNED_IN = 1;
    public static final int USER_STATUS_FETCHING = 2;
    public static final int USER_STATUS_NOT_SIGNED_IN = 0;

    public void getReviewbyUser(Trucks truck) {
        final ReviewsInterface reviewByUserLoaded = (ReviewsInterface) context;
        mDatabase.child(context.getString(R.string.reviews_table_name))
                .child(truck.getReviewsReference())
                .child(context.getString(R.string.review_prefix) + mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            TruckReview review = dataSnapshot.getValue(TruckReview.class);
                            reviewByUserLoaded.OnReviewByUserLoaded(true,review);
                        } catch (Exception e) {
                            reviewByUserLoaded.OnReviewByUserLoaded(false,null);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public int getUser() {
        if (mAuth.getCurrentUser() !=null){
            if (user !=null) return USER_STATUS_SIGNED_IN;
            else return USER_STATUS_FETCHING;
        }
        return USER_STATUS_NOT_SIGNED_IN;
    }

    public void getUserObject() {

        if(user == null && mAuth.getCurrentUser() !=null) {
            mDatabase.child(context.getString(R.string.users_table_name))
                    .child(mAuth.getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                user = dataSnapshot.getValue(User.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private ProgressDialog progressDialog;
    public void uploadPicture(Context c,Bitmap bitmap){
        Uri uri = getImageUri(c,bitmap);

        final PictureUploadInterface uploadInterface = (PictureUploadInterface) c;

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        StorageReference picturesRef = mStorageRef.child("images/" + mAuth.getCurrentUser().getUid() + "/"+ uri.getPath() + ".jpg");

        progressDialog = new ProgressDialog(c);
        progressDialog.setMessage(c.getString(R.string.uploading_picture));
        progressDialog.show();

        Log.i("Uploading Picture", "Uploading Started");
        picturesRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        uploadInterface.onUploadComplete(true,downloadUrl.toString());
                        Log.i("Uploading Picture", "Uploading Successful, url: " + downloadUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        uploadInterface.onUploadComplete(false,null);
                        Log.i("Uploading Picture", "Uploading Failed");
                    }
                });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static boolean signedIn() {
        return user != null;
    }

    public void Logout() {
        mAuth.signOut();
        user = null;
    }

    public static String getUserID(){
        return mAuth.getCurrentUser().getUid();
    }

    public void submitReview(TruckReview review, Trucks truck) {
        final ReviewsInterface reviewsInterface = (ReviewsInterface) context;

        Calendar date =Calendar.getInstance();
        review.setTimestamp(date.getTimeInMillis());
        mDatabase.child(context.getString(R.string.reviews_table_name))
                .child(truck.getReviewsReference())
                .child(review.getID())
                .setValue(review)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        reviewsInterface.OnReviewSubmition(task.isSuccessful());
                    }
                });
    }

    public void updateTotalRating(final Trucks truck, HashMap<String, TruckReview> truckReviews) {
        int rating = MiscMethods.getReviewsAverage(truckReviews);
        truck.setRating(rating);
        truck.setReviewsCount(truckReviews.size());
        saveTruck(truck);
    }


    public boolean userIsFollowing(String id) {
        if(user == null) return false;
        List<String> following = user.getFollowing();
        return following.contains(id);
    }

    public void addTruckToFollow(Trucks truck){
        List<String> following = user.getFollowing();
        following.add(truck.get_id());
        user.setFollowing(following);
        saveUser();
        updateTruckFollowing(truck,1);
    }


    public void removefollowing(Trucks truck) {
        List<String> following = user.getFollowing();
        following.remove(truck.get_id());
        user.setFollowing(following);
        saveUser();
        updateTruckFollowing(truck,-1);
    }


    private void updateTruckFollowing(Trucks truck,int i) {
        truck.setFollowers(truck.getFollowers() + i);
        saveTruck(truck);
    }

    private void saveUser() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.i("User Details save Task", "Successfully saved");
                }
                else {
                    Log.i("User Details Save Task", "Unsuccessful save, " + task.getException().getMessage());
                }
            }
        });
    }

    private void saveTruck(final Trucks truck) {
        mDatabase.child(context.getString(R.string.trucks_table_name)).child(truck.get_id()).setValue(truck).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Log.i("Truck Save Task","Successfully saved: " + truck.get_name());
                else
                    Log.i("Truck Save Task","Unsuccessful save: " + truck.get_name());
            }
        });

    }

    public void saveUserProfilePicture(String url) {
        FirebaseUser fuser = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(url))
                .build();
        fuser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("UserImage", "User profile updated.");
                } else Log.d("UserImage", "User profile update failed.");
            }
        });;
    }

    public Uri getUserImageURL() {
        if(userHasImage())
            return mAuth.getCurrentUser().getPhotoUrl();
        return null;
    }

    public boolean userIsSignedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public boolean userHasImage() {
        return mAuth.getCurrentUser().getPhotoUrl() != null;
    }

    public static void loadUser() {
    }
}
