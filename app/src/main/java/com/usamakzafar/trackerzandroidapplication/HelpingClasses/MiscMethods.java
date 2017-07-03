package com.usamakzafar.trackerzandroidapplication.HelpingClasses;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckLocation;
import com.usamakzafar.trackerzandroidapplication.activity.ViewImage;

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

    public String getDistanceTo(TruckLocation truckLocation){
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
}
