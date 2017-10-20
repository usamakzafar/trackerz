package com.usamakzafar.trackerzandroidapplication.activity.admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.ImageAdapter;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.MenuAdapter;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.PictureUtils;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.Statics;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckMenuItem;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckPicture;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.Trucks;
import com.usamakzafar.trackerzandroidapplication.activity.TrucksDetailActivity;
import com.usamakzafar.trackerzandroidapplication.activity.interfaces.PictureUploadInterface;
import com.usamakzafar.trackerzandroidapplication.network.FirebaseHelpers;

import java.util.ArrayList;
import java.util.HashMap;

import static com.usamakzafar.trackerzandroidapplication.HelpingClasses.PictureUtils.SELECT_PICTURE;

public class TruckPicturesEditActivity extends AppCompatActivity implements PictureUploadInterface{

    private ArrayList<TruckPicture> pictures;
    private DatabaseReference mDatabase;
    private Trucks truck;
    private ImageAdapter imageAdapter;
    private PictureUtils pictureUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_pictures_edit);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        truck = Statics.activeTruckList.get(FirebaseHelpers.user.getTruck());

        LoadPictures();
    }


    private void LoadPictures() {
        GridView gridView = (GridView) findViewById(R.id.pictures_gridview);
        pictures = new ArrayList<>();

        imageAdapter = new ImageAdapter(this, pictures, true);

        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                showDeleteDialog(position);
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
                        TruckPicture picture = dataSnapshot.getValue(TruckPicture.class);
                        pictures.remove(picture);
                        imageAdapter.notifyDataSetChanged();
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

    public void backPressed(View view){this.finish();}

    private AlertDialog dialog;
    private void showDeleteDialog(final int position) {
        dialog = new AlertDialog.Builder(TruckPicturesEditActivity.this)
                .setTitle(R.string.delete_picture_warning)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        DatabaseReference mDatabase;
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child(TruckPicturesEditActivity.this.getString(R.string.pictures_table_name))
                                .child(truck.getPicturesReference())
                                .child(pictures.get(position).getPictureID()).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    Toast.makeText(TruckPicturesEditActivity.this, R.string.delete_successful, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    LoadPictures();
                                } else
                                    Toast.makeText(TruckPicturesEditActivity.this, R.string.error_msg_generic, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void addNew(View view) {
        pictureUtils = new PictureUtils(this);
        pictureUtils.LaunchPermissionsRequest();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        pictureUtils.OnRequestPermissionsResult(grantResults);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if(Build.VERSION.SDK_INT > 19) {
                    pictureUtils.OnFetchedPicture(data);

                }
            }
        }
    }

    @Override
    public void onPictureFetched(String path, Bitmap bitmap) {
        FirebaseHelpers helpers = new FirebaseHelpers(this);
        helpers.uploadPicture(this,bitmap);
    }

    @Override
    public void onUploadComplete(Boolean success, String url) {
        if(success){
            saveNewPicture(url);
            Toast.makeText(this, "Successfully uploaded image", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "An error occurred while uploading image", Toast.LENGTH_SHORT).show();
    }

    private void saveNewPicture(String url) {
        TruckPicture picture = new TruckPicture();

        picture.setPictureID("p_"+pictures.size()+1);
        picture.setPictureURL(url);

        mDatabase.child(getString(R.string.pictures_table_name))
                .child(truck.getPicturesReference())
                .child(picture.getPictureID())
                .setValue(picture)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        LoadPictures();
                        if(task.isSuccessful())
                            Log.i("Picture Save Task", "Successfully saved");
                        else
                            Log.i("Picture Save Task","Unsuccessful save");
                    }
                });
    }
}
