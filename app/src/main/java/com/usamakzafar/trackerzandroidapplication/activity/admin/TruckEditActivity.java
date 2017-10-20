package com.usamakzafar.trackerzandroidapplication.activity.admin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.CircleTransform;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.PictureUtils;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.Statics;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.Trucks;
import com.usamakzafar.trackerzandroidapplication.activity.interfaces.PictureUploadInterface;
import com.usamakzafar.trackerzandroidapplication.network.FirebaseHelpers;

import static com.usamakzafar.trackerzandroidapplication.HelpingClasses.PictureUtils.SELECT_PICTURE;

public class TruckEditActivity extends AppCompatActivity implements PictureUploadInterface {

    private Trucks truck;
    private DatabaseReference mDatabase;

    private PictureUtils pictureUtils;

    private EditText name        ;
    private EditText description ;
    private EditText facebook    ;
    private EditText youtube     ;
    private EditText twitter     ;
    private EditText google      ;
    private Spinner  type         ;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_edit);

        truck = Statics.activeTruckList.get(FirebaseHelpers.user.getTruck());

        pictureUtils = new PictureUtils(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        prefillViews();
    }

    private void prefillViews() {
        name           = (EditText) findViewById(R.id.et_truckname);
        description    = (EditText) findViewById(R.id.et_description);
        facebook       = (EditText) findViewById(R.id.et_link_facebook);
        youtube        = (EditText) findViewById(R.id.et_link_youtube);
        twitter        = (EditText) findViewById(R.id.et_link_twitter);
        google         = (EditText) findViewById(R.id.et_link_google);
        type           = (Spinner) findViewById(R.id.et_spinner_truckType);
        imageView      = (ImageView) findViewById(R.id.et_admin_trucklogo);

        name       .setText(truck.get_name());
        description.setText(truck.get_description());
        facebook   .setText(truck.getFacebook());
        youtube    .setText(truck.getYoutube());
        twitter    .setText(truck.getTwitter());
        google     .setText(truck.getGoogle());

        type.setSelection(truck.get_type() -1);

        Picasso.with(this).load(truck.getLogoURL()).transform(new CircleTransform()).into(imageView);
    }


    public void updateDetails(View view){
        Toast.makeText(this, R.string.updatingtruck, Toast.LENGTH_SHORT).show();

        truck.set_name(     name       .getText().toString());
        truck.set_type(     type.getSelectedItemPosition() + 1);
        truck.set_description(description.getText().toString());
        truck.setFacebook(  facebook   .getText().toString());
        truck.setYoutube    (youtube    .getText().toString());
        truck.setTwitter(   twitter    .getText().toString());
        truck.setGoogle(    google     .getText().toString());

        mDatabase.child(getString(R.string.trucks_table_name))
                .child(truck.get_id()).setValue(truck)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(TruckEditActivity.this, R.string.truck_save_successful, Toast.LENGTH_SHORT).show();
                    TruckEditActivity.this.finish();
                }
                else
                    Toast.makeText(TruckEditActivity.this, R.string.error_msg_generic, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void backPressed(View view){
        this.finish();
    }


    /************PICTURE ROLA***************************/



    public void truckPictureClicked(View view){
        checkPermissionBeforeLaunchingIntent();
    }

    private void checkPermissionBeforeLaunchingIntent() {
        if (Build.VERSION.SDK_INT >= 23){
            pictureUtils.LaunchPermissionsRequest();
        }else {
            pictureUtils.launchSelectPictureIntent();
        }
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
        FirebaseHelpers helpers = new FirebaseHelpers(TruckEditActivity.this);
        helpers.uploadPicture(this,bitmap);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onUploadComplete(Boolean success, String url) {
        if(success){
            truck.setLogoURL(url);
            Picasso.with(this).load(url).transform(new CircleTransform()).into(imageView);
            Toast.makeText(TruckEditActivity.this, "Successfully Updated Truck Logo", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(TruckEditActivity.this, "An error occurred while uploading truck logo", Toast.LENGTH_SHORT).show();
    }
}
