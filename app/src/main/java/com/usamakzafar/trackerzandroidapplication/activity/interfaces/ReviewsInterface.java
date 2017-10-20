package com.usamakzafar.trackerzandroidapplication.activity.interfaces;

import com.usamakzafar.trackerzandroidapplication.TrucksClasses.TruckReview;

/**
 * Created by usamazafar on 17/09/2017.
 */

public interface ReviewsInterface {

    public void OnReviewByUserLoaded(boolean success, TruckReview review);

    public void OnReviewSubmition(boolean success);
}
