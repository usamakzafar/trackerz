package com.usamakzafar.trackerzandroidapplication.HelpingClasses;

import android.content.Context;
import android.content.Intent;

import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckMenuItem;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.Trucks;

/**
 * Created by usamazafar on 21/09/2017.
 */

public class ShareMethods {
    public static final int APP = 1;
    public static final int TRUCK = 2;
    public static final int MENU = 3;
    private String message;

    private Context context;

    public ShareMethods(Context c){
        context = c;
    }
    public void shareApp(){
        startShareIntent(context.getString(R.string.share_text_app));
    }

    public void shareTruck(Trucks truck){
        String message = context.getString(R.string.share_text_message1)
                + " truck called "
                + truck.get_name()
                + " "
                + context.getString(R.string.share_text_message2);
        startShareIntent(message);
    }


    public void shareMenu(TruckMenuItem item){
        String message = context.getString(R.string.share_text_message1)
                + " food item called "
                + item.getItemName()
                + " "
                + context.getString(R.string.share_text_message2);
        startShareIntent(message);
    }

    private void startShareIntent(String message){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }
}
