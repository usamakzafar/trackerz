package com.usamakzafar.trackerzandroidapplication.HelpingClasses;

import android.content.Context;
import android.util.Log;
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
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckMenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by usamazafar on 29/05/2017.
 */

public class MenuAdapter extends BaseAdapter {

    private HashMap<String,TruckMenuItem> menuMap;
    private ArrayList<TruckMenuItem> menuData;

    private Context mContext;


    public MenuAdapter(Context context, HashMap<String,TruckMenuItem> menuItemHashMap) {
        this.menuMap = menuItemHashMap;
        this.menuData = new ArrayList<>();
        this.mContext   = context;

        for(Map.Entry<String, TruckMenuItem> entry : menuItemHashMap.entrySet()) {
            TruckMenuItem menuItem = entry.getValue();
            this.menuData.add(menuItem);
            Log.i("Menu Item Received", "Item ID: " + menuItem.getItemID() + ", Item Name: " + menuItem.getItemName());
        }

    }

    @Override
    public int getCount() {
        return menuData.size();
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
        TextView menuItemName;
        TextView menuItemDescription;
        TextView menuItemPrice;
        ImageView menuItemImage;
    }

    int lastPosition  = -1;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MenuAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new MenuAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.truck_menu_item, parent, false);

            viewHolder.menuItemName = (TextView) convertView.findViewById(R.id.mi_name);
            viewHolder.menuItemDescription = (TextView) convertView.findViewById(R.id.mi_description);
            viewHolder.menuItemPrice = (TextView) convertView.findViewById(R.id.mi_price);
            viewHolder.menuItemImage = (ImageView) convertView.findViewById(R.id.mi_image);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MenuAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        final TruckMenuItem currentMenuItem = menuData.get(position);

        viewHolder.menuItemName.setText(currentMenuItem.getItemName());
        viewHolder.menuItemDescription.setText(currentMenuItem.getItemDescription());
        viewHolder.menuItemPrice.setText(currentMenuItem.getItemPrice() + " SR");
        Picasso.with(mContext).load(currentMenuItem.getItemPicture()).into(viewHolder.menuItemImage);
        viewHolder.menuItemImage.setTag(position);
        // Return the completed view to render on screen

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Menu Item Clicked", "Item ID: " + currentMenuItem.getItemID() + ", Item Name: " + currentMenuItem.getItemName());
            }
        });

        return convertView;
    }
}
