package com.foodlog.foodlog.report.timeline.dayStats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafa on 13/08/17.
 */
public class DayScore {
    private float score;
    private float ratingScore;

    private List<String> penalties = new ArrayList<>();

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public List<String> getPenalties() {
        return penalties;
    }

    public void setPenalties(List<String> penalties) {
        this.penalties = penalties;
    }


    public float getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(float ratingScore) {
        this.ratingScore = ratingScore;
    }
}
