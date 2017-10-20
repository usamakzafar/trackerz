package com.usamakzafar.trackerzandroidapplication.HelpingClasses;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckLocation;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckMenuItem;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckReview;
import com.usamakzafar.trackerzandroidapplication.activity.ViewImage;
import com.usamakzafar.trackerzandroidapplication.activity.admin.TruckEditActivity;
import com.usamakzafar.trackerzandroidapplication.activity.admin.TruckMenuEditActivity;
import com.usamakzafar.trackerzandroidapplication.network.FirebaseHelpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by usamazafar on 18/05/2017.
 */

public class MiscMethods {

    public static BitmapDescriptor getMarkerIcon(int type, boolean isopen){
        if (isopen)
            switch (type){
                case 1:
                    return BitmapDescriptorFactory.fromResource(R.drawable.redpinmap);
                case 2:
                    return BitmapDescriptorFactory.fromResource(R.drawable.orangepin);
                case 3:
                    return BitmapDescriptorFactory.fromResource(R.drawable.bluepin);
                case 4:
                    return BitmapDescriptorFactory.fromResource(R.drawable.greenpin);
                default:
                    return null;
            }
        else
            switch (type){
                case 1:
                    return BitmapDescriptorFactory.fromResource(R.drawable.redpinho);
                case 2:
                    return BitmapDescriptorFactory.fromResource(R.drawable.orangeholo);
                case 3:
                    return BitmapDescriptorFactory.fromResource(R.drawable.bluepinholo);
                case 4:
                    return BitmapDescriptorFactory.fromResource(R.drawable.greenholo);
                default:
                    return null;
            }
    }

    public static int getSmallPin(int type){
        switch (type){
            case 1:
                return R.drawable.smallred;
            case 2:
                return R.drawable.smallorange;
            case 3:
                return R.drawable.smallblue;
            case 4:
                return R.drawable.smallgree;
            default:
                return 0;
        }
    }

    public static String getDistanceTo(TruckLocation truckLocation){
        LatLng userLocation = Statics.userLocation;
        if(userLocation!=null) {
            String dis = "";


            float[] dist = new float[]{1f};

            Location.distanceBetween(userLocation.latitude, userLocation.longitude, truckLocation.getLatitude(), truckLocation.getLongitude(), dist);

            Log.i("Distance Calculated", dist[0] + " m");
            int m = (int) dist[0];

            if (m >= 1000)
                dis = String.valueOf(m / 1000) + " km";
            else
                dis = String.valueOf(m) + " m";

            return dis;
        }
        return "Nearby";
    }

    //Method to launch a URL in browser
    public static void launchLink(Context context, String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    public static int getReviewsAverage(HashMap<String, TruckReview> reviews){

        int avg = 0;

        if (reviews.size() >0) {
            for(Map.Entry<String, TruckReview> entry : reviews.entrySet()) {
                TruckReview review = entry.getValue();
                avg += review.getRating();
            }
            avg = avg / reviews.size();
        }
        return avg;
    }

    public static String inBrackets(int rating){ return "(" + rating + ")";    }

    public static String getReviewID() {

        return "review_" + FirebaseHelpers.getUserID();
    }

}
