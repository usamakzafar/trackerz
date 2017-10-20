package com.usamakzafar.trackerzandroidapplication.HelpingClasses;

import com.google.android.gms.maps.model.LatLng;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.Trucks;

import java.util.HashMap;

/**
 * Created by usamazafar on 23/05/2017.
 */

public class Statics {

    public static HashMap<String, Trucks> activeTruckList;
    public static LatLng userLocation;

    public static String[] Truck_type_name = new String[]{"","Food Truck","Coffee Truck","Event Truck","Other"};
}
