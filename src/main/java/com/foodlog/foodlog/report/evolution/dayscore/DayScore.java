package com.foodlog.foodlog.report.evolution.dayscore;

import java.time.Instant;

/**
 * Created by rafael on 11/01/18.
 */
public class DayScore {
    private Instant day;
    private Double score;


    public Instant getDay() {
        return day;
    }

    public void setDay(Instant day) {
        this.day = day;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
