package com.usamakzafar.trackerzandroidapplication.HelpingClasses;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckPicture;
import com.usamakzafar.trackerzandroidapplication.activity.ViewImage;

import java.util.ArrayList;

/**
 * Created by usamazafar on 06/06/2017.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<TruckPicture> picturesURL;

    public ImageAdapter(Context c,ArrayList<TruckPicture> urls) {
        mContext = c;
        picturesURL = urls;
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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ViewImage.class);
                i.putExtra("imageURL",url);
                mContext.startActivity(i);
            }
        });
        return imageView;
    }

}
