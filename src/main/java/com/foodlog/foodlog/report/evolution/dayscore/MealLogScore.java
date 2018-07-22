package com.foodlog.foodlog.report.evolution.dayscore;

import java.time.Instant;

/**
 * Created by rafael on 11/01/18.
 */
public interface MealLogScore {
    public Instant getMealDateTime();
    public Integer getRating();

}
