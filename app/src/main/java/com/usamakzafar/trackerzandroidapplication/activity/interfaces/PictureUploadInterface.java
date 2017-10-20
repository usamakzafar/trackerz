package com.usamakzafar.trackerzandroidapplication.activity.interfaces;

import android.graphics.Bitmap;

/**
 * Created by usamazafar on 16/09/2017.
 */

public interface PictureUploadInterface {

    void onPictureFetched(String path,Bitmap bitmap);
    void onUploadComplete(Boolean success,String url);
}
