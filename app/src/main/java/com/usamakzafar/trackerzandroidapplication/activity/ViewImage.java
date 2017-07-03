package com.usamakzafar.trackerzandroidapplication.activity;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.usamakzafar.trackerzandroidapplication.R;

import uk.co.senab.photoview.PhotoView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ViewImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_image);

        PhotoView photoView = (PhotoView) findViewById(R.id.fullscreen_image);
        Picasso.with(this).load(getIntent().getStringExtra("imageURL")).placeholder(android.R.drawable.picture_frame).into(photoView);
    }

}
