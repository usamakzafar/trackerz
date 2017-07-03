package com.usamakzafar.trackerzandroidapplication.HelpingClasses;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckMenuItem;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckReview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by usamazafar on 29/05/2017.
 */

public class ReviewsAdapter extends BaseAdapter {

    private HashMap<String,TruckReview> reviewHashMap;
    private ArrayList<TruckReview> reviewData;

    private Context mContext;


    public ReviewsAdapter(Context context, HashMap<String,TruckReview> truckReviewHashMap) {
        this.reviewHashMap = truckReviewHashMap;
        this.reviewData = new ArrayList<>();
        this.mContext   = context;

        for(Map.Entry<String, TruckReview> entry : truckReviewHashMap.entrySet()) {
            TruckReview reviewItem = entry.getValue();
            this.reviewData.add(reviewItem);
            Log.i("Review Received", "Item ID: " + reviewItem.getID() + ", Item Name: " + reviewItem.getName());
        }

    }

    @Override
    public int getCount() {
        return reviewData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    // View lookup cache
    private static class ViewHolder {
        TextView reviewerName;
        TextView reviewText;
        TextView reviewDate;
        ImageView reviewerImage;
        CheckBox s1,s2,s3,s4,s5;
    }

    int lastPosition  = -1;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ReviewsAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ReviewsAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.truck_review_item, parent, false);

            viewHolder.reviewerName = (TextView) convertView.findViewById(R.id.ri_name);
            viewHolder.reviewText   = (TextView) convertView.findViewById(R.id.ri_description);
            viewHolder.reviewDate   = (TextView) convertView.findViewById(R.id.ri_time);
            viewHolder.reviewerImage = (ImageView) convertView.findViewById(R.id.ri_image);

            viewHolder.s1 = (CheckBox) convertView.findViewById(R.id.star1);
            viewHolder.s2 = (CheckBox) convertView.findViewById(R.id.star2);
            viewHolder.s3 = (CheckBox) convertView.findViewById(R.id.star3);
            viewHolder.s4 = (CheckBox) convertView.findViewById(R.id.star4);
            viewHolder.s5 = (CheckBox) convertView.findViewById(R.id.star5);



            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ReviewsAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        final TruckReview currentReview = reviewData.get(position);

        //Setting values of the review placeholders
        viewHolder.reviewerName.setText(currentReview.getName());
        viewHolder.reviewText.setText(currentReview.getReview());
        viewHolder.reviewDate.setText(getHowLongAgo(currentReview.getTimestamp()));
        Picasso.with(mContext).load(currentReview.getPictureUrl()).transform(new CircleTransform()).into(viewHolder.reviewerImage);
        viewHolder.reviewerImage.setTag(position);


        //Checking stars for reviews
        int rating = currentReview.getRating();
        if (rating >=1)
            viewHolder.s1.setChecked(true);
        if (rating >=2)
            viewHolder.s2.setChecked(true);
        if (rating >=3)
            viewHolder.s3.setChecked(true);
        if (rating >=4)
            viewHolder.s4.setChecked(true);
        if (rating >=5)
            viewHolder.s5.setChecked(true);


        //Adding the pop up to view full review
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Review by " + currentReview.getName());
                builder.setMessage(currentReview.getReview());
                builder.show();
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    // Date parsing to to return time elapsed
    private String getHowLongAgo(long then) {
        long now = System.currentTimeMillis();
        //long then = startDate.getTimeInMillis();

        return (String) DateUtils.getRelativeTimeSpanString(then,now,1);
    }
}
