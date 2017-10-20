package com.usamakzafar.trackerzandroidapplication.HelpingClasses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckPicture;
import com.usamakzafar.trackerzandroidapplication.activity.ViewImage;
import com.usamakzafar.trackerzandroidapplication.network.FirebaseHelpers;

import java.util.ArrayList;

/**
 * Created by usamazafar on 06/06/2017.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<TruckPicture> picturesURL;
    private boolean admin;

    public ImageAdapter(Context c,ArrayList<TruckPicture> urls, boolean _admin) {
        mContext = c;
        picturesURL = urls;
        admin = _admin;
    }

    public int getCount() {
        return picturesURL.size();
    }

    public Object getItem(int position) {
        return picturesURL.get(position).getPictureURL();
    }

    public long getItemId(int position) {
        return position;
    }

    public String getPictureId(int position) {
        return picturesURL.get(position).getPictureID();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, final View convertView, ViewGroup parent) {
        ImageView imageView;
        final String url =(String) getItem(position);

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext).load(url).placeholder(R.drawable.image_placeholder).into(imageView);
        if(!admin)
        {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(mContext, ViewImage.class);
                    i.putExtra("imageURL", url);
                    mContext.startActivity(i);

                }
            });
        }
        return imageView;
    }
/*
    private AlertDialog dialog;
    private void adminDialog() {
        dialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.delete_review_prefix) + currentReview.getName() + "?")
                .setMessage(R.string.disclaimer_review_delete)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference mDatabase;
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child(mContext.getString(R.string.reviews_table_name))
                                .child(Statics.activeTruckList.get(FirebaseHelpers.user.getTruck()).getReviewsReference())
                                .child(currentReview.getID()).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError == null) {
                                    Toast.makeText(mContext, R.string.delete_successful, Toast.LENGTH_SHORT).show();
                                    removeReview(currentReview);
                                }
                                else
                                    Toast.makeText(mContext, R.string.error_msg_generic, Toast.LENGTH_SHORT).show();
                                dismissDialog();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel",null)
                .show();
    }*/

}
