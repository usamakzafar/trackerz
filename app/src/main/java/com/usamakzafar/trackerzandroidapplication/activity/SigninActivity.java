package com.usamakzafar.trackerzandroidapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.PictureUtils;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.activity.admin.TruckMenuEditActivity;
import com.usamakzafar.trackerzandroidapplication.activity.interfaces.PictureUploadInterface;
import com.usamakzafar.trackerzandroidapplication.models.User;
import com.usamakzafar.trackerzandroidapplication.network.FirebaseHelpers;

import static com.usamakzafar.trackerzandroidapplication.HelpingClasses.PictureUtils.SELECT_PICTURE;

public class SigninActivity extends AppCompatActivity implements PictureUploadInterface{

    private String TAG = "LoginActivity";

    public static final int VIEW_SIGNIN = 1;
    public static final int VIEW_SIGNUP = 2;
    public static final int VIEW_DETAIL = 3;

    private boolean signUpState = false;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String username;
    private String password;

    private EditText et_username;
    private EditText et_password;

    private View login_View;
    private View signUpView;
    private View detailsView;
    private View progressBar;

    private PictureUtils pictureUtils;
    private ImageView userPicture;

    private FirebaseHelpers helpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        progressBar = findViewById(R.id.loginprogressbar);
        progressBar.setVisibility( View.GONE );

        login_View = findViewById(R.id.loginView);
        signUpView = findViewById(R.id.signupView);
        detailsView = findViewById(R.id.fillDetailsView);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    if (!signUpState)
                        userSignedIn();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    changeView(VIEW_SIGNIN);
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        pictureUtils = new PictureUtils(this);
    }

    private void changeView(int view){
        switch (view)
        {
            case VIEW_SIGNIN:
                signUpView.setVisibility(View.INVISIBLE);
                login_View.setVisibility(View.VISIBLE);
                detailsView.setVisibility(View.INVISIBLE);
                break;
            case VIEW_SIGNUP:
                signUpView.setVisibility(View.VISIBLE);
                login_View.setVisibility(View.INVISIBLE);
                detailsView.setVisibility(View.INVISIBLE);
                break;
            case VIEW_DETAIL:
                signUpView.setVisibility(View.INVISIBLE);
                login_View.setVisibility(View.INVISIBLE);
                detailsView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void attemptSignin(View view){

        et_username = (EditText) findViewById(R.id.username);
        et_password = (EditText) findViewById(R.id.password);

        username = et_username.getText().toString();
        password = et_password.getText().toString();

        if (!username.contains(" ") && !password.contains(" ") && username.contains("@") && username.contains(".")) {

            login_View.setVisibility(  View.GONE );
            progressBar.setVisibility( View.VISIBLE );

            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            login_View.setVisibility(  View.VISIBLE );
                            progressBar.setVisibility( View.GONE );
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(SigninActivity.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                //startActivity(new Intent(SigninActivity.this,AdminActivity.class));
                                userSignedIn();
                            }

                        }
                    });
        }else{
            Toast.makeText(SigninActivity.this, R.string.invalid_Username,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void createAccount(View view){
        changeView(VIEW_SIGNUP);
    }

    public void attemptSignUp(View view){
        et_username = (EditText) findViewById(R.id.signup_email);
        EditText con_et_username = (EditText) findViewById(R.id.confirm_signup_email);
        et_password = (EditText) findViewById(R.id.signup_password);

        signUpView = findViewById(R.id.signupView);

        username = et_username.getText().toString();
        password = et_password.getText().toString();
        String confirmUsername = con_et_username.getText().toString();

        if(!username.equals(confirmUsername)){
            Toast.makeText(this, R.string.email_not_match, Toast.LENGTH_SHORT).show();
        }else {

            if (!username.contains(" ") && !password.contains(" ") && username.contains("@") && username.contains(".")) {

                signUpView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                signUpState = true;
                mAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                progressBar.setVisibility(View.INVISIBLE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    changeView(VIEW_SIGNUP);
                                    Log.w(TAG, "signUpWithEmail:failed", task.getException());
                                    Toast.makeText(SigninActivity.this, task.getException().getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(SigninActivity.this, "Successfully signed in as " + user.getEmail(),
                                            Toast.LENGTH_SHORT).show();
                                    changeView(VIEW_DETAIL);
                                }
                            }
                        });
            } else {
                Toast.makeText(SigninActivity.this, R.string.illegal_characters,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void submitDetails(View view){
        EditText name = (EditText) findViewById(R.id.fd_Name);
        EditText date = (EditText) findViewById(R.id.fd_Date);

        progressBar.setVisibility(View.VISIBLE);
        User user = new User();
        user.setName(name.getText().toString());
        user.setDob(date.getText().toString());
        user.setTruck("no");
        user.setEmail(mAuth.getCurrentUser().getEmail());

        final String user_name = user.getName();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()){
                    Log.i("User Details save Task", "Successfully saved");
                    updateFirebaseUser(user_name);
                    userSignedIn();
                }
                else {
                    changeView(VIEW_DETAIL);
                    Toast.makeText(SigninActivity.this, R.string.generic_error, Toast.LENGTH_SHORT).show();
                    Log.i("Truck Save Task", "Unsuccessful save, " + task.getException().getMessage());
                }
            }
        });

    }

    private void updateFirebaseUser(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }

    private void userSignedIn() {
        //startActivity(new Intent(SigninActivity.this,MapsActivity.class));
        this.finish();
    }

    public void addUserPicture(View view) {
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
        userPicture = (ImageView) findViewById(R.id.user_pic_imageview);
        helpers = new FirebaseHelpers(this);
        helpers.uploadPicture(this,bitmap);
        userPicture.setImageBitmap(bitmap);
    }

    @Override
    public void onUploadComplete(Boolean success, String url) {
        if(success){
            helpers.saveUserProfilePicture(url);
            Picasso.with(this).load(url).into(userPicture);
            Toast.makeText(this, "Successfully updated profile image", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "An error occurred while uploading profile image", Toast.LENGTH_SHORT).show();
    }
}
