package com.foodlog.foodlog.report.timeline.dayStats;

/**
 * Created by rafa on 13/08/17.
 */
public class DayStats {
   private float avgSecondsBetweenMeals;
   private float scheduledAvgSecondsBetweenMeals;
   private DayScore dayScore = new DayScore();
   private int treePlusHourIntervals;
   private int loggedMeals;
   private int scheduledMeals;
   private int onTimeMeals;
   private int offTimeMeals;

    public int getOffTimeMeals() {
        return offTimeMeals;
    }

    public void setOffTimeMeals(int offTimeMeals) {
        this.offTimeMeals = offTimeMeals;
    }

    public float getScheduledAvgSecondsBetweenMeals() {
        return scheduledAvgSecondsBetweenMeals;
    }

    public void setScheduledAvgSecondsBetweenMeals(float scheduledAvgSecondsBetweenMeals) {
        this.scheduledAvgSecondsBetweenMeals = scheduledAvgSecondsBetweenMeals;
    }

    public float getAvgSecondsBetweenMeals() {
        return avgSecondsBetweenMeals;
    }

    public void setAvgSecondsBetweenMeals(float avgSecondsBetweenMeals) {
        this.avgSecondsBetweenMeals = avgSecondsBetweenMeals;
    }

    public DayScore getDayScore() {
        return dayScore;
    }

    public void setDayScore(DayScore dayScore) {
        this.dayScore = dayScore;
    }

    public int getTreePlusHourIntervals() {
        return treePlusHourIntervals;
    }

    public void setTreePlusHourIntervals(int treePlusHourIntervals) {
        this.treePlusHourIntervals = treePlusHourIntervals;
    }

    public int getLoggedMeals() {
        return loggedMeals;
    }

    public void setLoggedMeals(int loggedMeals) {
        this.loggedMeals = loggedMeals;
    }

    public int getScheduledMeals() {
        return scheduledMeals;
    }

    public void setScheduledMeals(int scheduledMeals) {
        this.scheduledMeals = scheduledMeals;
    }

    public int getOnTimeMeals() {
        return onTimeMeals;
    }

    public void setOnTimeMeals(int onTimeMeals) {
        this.onTimeMeals = onTimeMeals;
    }
}
