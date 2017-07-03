package com.usamakzafar.trackerzandroidapplication.HelpingClasses;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.Trucks;
import com.usamakzafar.trackerzandroidapplication.activity.TrucksDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by usamazafar on 22/05/2017.
 */

public class TrucksAdapter extends BaseAdapter {

    private HashMap<String,Trucks> trucksMap;
    private ArrayList<Trucks> trucksData;

    private Context mContext;


    public TrucksAdapter(Context context, HashMap<String,Trucks> trucks) {
        this.trucksMap  = trucks;
        this.trucksData = new ArrayList<>();
        this.mContext   = context;

        for(Map.Entry<String, Trucks> entry : trucks.entrySet()) {
            Trucks truck = entry.getValue();
            this.trucksData.add(truck);
        }

    }

    @Override
    public int getCount() {
        return trucksData.size();
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
        TextView txtName;
        TextView txtDesc;
        TextView txtFlwr;
        TextView txtDist;
        ImageView image;
        ImageView pin;
    }

    int lastPosition  = -1;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.truck_list_item, parent, false);

            viewHolder.txtName = (TextView) convertView.findViewById(R.id.li_truckName);
            viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.li_truckDescription);
            viewHolder.txtFlwr = (TextView) convertView.findViewById(R.id.li_truckFollowers);
            viewHolder.txtDist = (TextView) convertView.findViewById(R.id.li_truckDistance);
            viewHolder.image   = (ImageView) convertView.findViewById(R.id.li_truckImage);
            viewHolder.pin     = (ImageView) convertView.findViewById(R.id.li_pin);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        final Trucks currentTruck = trucksData.get(position);

        viewHolder.txtName.setText(currentTruck.get_name());
        viewHolder.txtDesc.setText(currentTruck.get_description());
        viewHolder.txtFlwr.setText(currentTruck.get_open() + "k");

        MiscMethods methods = new MiscMethods();
        viewHolder.txtDist.setText(methods.getDistanceTo(currentTruck.get_location()));

        //Load the Image
        Picasso.with(mContext).load(currentTruck.getLogoURL()).transform(new CircleTransform()).into(viewHolder.image);
        viewHolder.image.setTag(position);

        //Load the pin
        viewHolder.pin.setImageResource(MiscMethods.getSmallPin(currentTruck.get_type()));

        // Add click listener
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,TrucksDetailActivity.class);
                intent.putExtra("id",currentTruck.get_id());
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityOptions options =
                            ActivityOptions.makeCustomAnimation(mContext,R.anim.left_from_right,R.anim.right_from_left);
                    mContext.startActivity(intent, options.toBundle());
                }else
                    mContext.startActivity(intent);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}