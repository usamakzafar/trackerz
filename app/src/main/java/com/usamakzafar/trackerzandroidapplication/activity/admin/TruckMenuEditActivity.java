package com.usamakzafar.trackerzandroidapplication.activity.admin;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.CircleTransform;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.MenuAdapter;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.PictureUtils;
import com.usamakzafar.trackerzandroidapplication.HelpingClasses.Statics;
import com.usamakzafar.trackerzandroidapplication.R;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckMenuItem;
import com.usamakzafar.trackerzandroidapplication.TrucksClasses.Trucks;
import com.usamakzafar.trackerzandroidapplication.activity.interfaces.PictureUploadInterface;
import com.usamakzafar.trackerzandroidapplication.network.FirebaseHelpers;

import java.util.HashMap;

import static com.usamakzafar.trackerzandroidapplication.HelpingClasses.PictureUtils.SELECT_PICTURE;

public class TruckMenuEditActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, PictureUploadInterface{

    private ListView menu;
    private PictureUtils pictureUtils;
    private HashMap<String,TruckMenuItem> menuItems;
    private DatabaseReference mDatabase;
    private Trucks truck;
    private MenuAdapter menuAdapter;
    private AlertDialog dialog;

    private EditText menu_name  ;
    private EditText menu_price ;
    private EditText menu_des   ;
    private ImageView menuImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_menu_edit);

        pictureUtils = new PictureUtils(this);

        menu = (ListView) findViewById(R.id.admin_menu_edit_list);
        menu.setOnItemClickListener(this);
        menu.setOnItemLongClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        truck = Statics.activeTruckList.get(FirebaseHelpers.user.getTruck());
        LoadMenu();
    }

    private void LoadMenu() {
        menuItems = new HashMap<>();

        mDatabase.child(getString(R.string.menu_table_name))
                .child(truck.getMenuReference())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        try {
                            TruckMenuItem menuItem = dataSnapshot.getValue(TruckMenuItem.class);
                            menuItems.put(menuItem.getItemID(),menuItem);
                            menuAdapter = new MenuAdapter(TruckMenuEditActivity.this, menuItems);
                            menu.setAdapter(menuAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        TruckMenuItem menuItem = dataSnapshot.getValue(TruckMenuItem.class);
                        menuItems.put(menuItem.getItemID(),menuItem);
                        menuAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        TruckMenuItem menuItem = dataSnapshot.getValue(TruckMenuItem.class);
                        menuItems.remove(menuItem.getItemID());
                        menuAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void backPressed(View view){this.finish();}

    public void addNew(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(R.layout.add_new_menu);
        dialog = builder.show();
        currentMenuItem = null;
        TextView title = (TextView) dialog.findViewById(R.id.admin_menu_dialog_title);
        title.setText(getString(R.string.admin_menu_dialog_title_add));
    }

    public void closeDialog(View view){
        dialog.dismiss();
    }

    TruckMenuItem currentMenuItem;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentMenuItem = (TruckMenuItem) menuAdapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(TruckMenuEditActivity.this)
                .setView(R.layout.add_new_menu);
        dialog = builder.show();
        TextView title = (TextView) dialog.findViewById(R.id.admin_menu_dialog_title);
        title.setText(getString(R.string.admin_menu_dialog_title_edit));

        findDialogViews();

        menu_name .setText(String.valueOf(currentMenuItem.getItemName()));
        menu_price.setText(String.valueOf(currentMenuItem.getItemPrice()));
        menu_des  .setText(String.valueOf(currentMenuItem.getItemDescription()));
        Picasso.with(this).load(currentMenuItem.getItemPicture()).into(menuImageView);
    }

    private void findDialogViews() {
        menuImageView = (ImageView) dialog.findViewById(R.id.menu_item_imageview);
        menu_name  = (EditText) dialog.findViewById(R.id.d_menu_name);
        menu_price = (EditText) dialog.findViewById(R.id.d_menu_price);
        menu_des   = (EditText) dialog.findViewById(R.id.d_menu_des);
    }

    public void updateMenu(View view){
        findDialogViews();

        if (currentMenuItem ==null ) {
            currentMenuItem = new TruckMenuItem();
            currentMenuItem.setItemID(getString(R.string.menu_item_id_prefix) + String.valueOf(menuAdapter.getCount() +1));
        }

        currentMenuItem.setItemName(menu_name.getText().toString());
        currentMenuItem.setItemPrice(Integer.valueOf(menu_price.getText().toString()));
        currentMenuItem.setItemDescription(menu_des.getText().toString());

        mDatabase.child(getString(R.string.menu_table_name))
                .child(truck.getMenuReference())
                .child(currentMenuItem.getItemID())
                .setValue(currentMenuItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()){
                Toast.makeText(TruckMenuEditActivity.this, R.string.menu_updated, Toast.LENGTH_SHORT).show();
                Log.i("Menu Save Task", "Successfully saved: " + currentMenuItem.getItemName());
                dialog.dismiss();
                //LoadMenu();
            }
                else {
                Toast.makeText(TruckMenuEditActivity.this, R.string.menu_update_failed, Toast.LENGTH_SHORT).show();
                Log.i("Menu Save Task", "Unsuccessful save: " + currentMenuItem.getItemName());
            }
            }
        });

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        currentMenuItem = (TruckMenuItem) menuAdapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Delete " + currentMenuItem.getItemName())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.child(getString(R.string.menu_table_name))
                                .child(truck.getMenuReference())
                                .child(currentMenuItem.getItemID()).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError == null)
                                    Toast.makeText(TruckMenuEditActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel",null);

        builder.show();
        return false;
    }

    public void addMenuPicture(View view) {
        checkPermissionBeforeLaunchingIntent();
    }

    private void checkPermissionBeforeLaunchingIntent() {
        if (Build.VERSION.SDK_INT >= 23){
            pictureUtils.LaunchPermissionsRequest();
        }else {
            pictureUtils.launchSelectPictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        pictureUtils.OnRequestPermissionsResult(grantResults);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if(Build.VERSION.SDK_INT > 19) {
                    pictureUtils.OnFetchedPicture(data);
                }
            }
        }
    }

    @Override
    public void onPictureFetched(String path, Bitmap bitmap) {
        menuImageView = (ImageView) findViewById(R.id.menu_item_imageview);
        FirebaseHelpers helpers = new FirebaseHelpers(TruckMenuEditActivity.this);
        helpers.uploadPicture(this,bitmap);
        menuImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onUploadComplete(Boolean success, String url) {
        if(success){
            currentMenuItem.setItemPicture(url);
            Picasso.with(this).load(url).into(menuImageView);
            Toast.makeText(TruckMenuEditActivity.this, "Successfully updated menu image", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(TruckMenuEditActivity.this, "An error occurred while uploading menu image", Toast.LENGTH_SHORT).show();
    }

}
