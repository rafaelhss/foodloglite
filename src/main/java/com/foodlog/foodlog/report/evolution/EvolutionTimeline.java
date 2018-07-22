package com.foodlog.foodlog.report.evolution;

import com.foodlog.domain.BodyLog;
import com.foodlog.domain.Weight;
import com.foodlog.foodlog.report.evolution.dayscore.DayScore;

import java.util.List;

/**
 * Created by rafael on 11/01/18.
 */
public class EvolutionTimeline {
    private List<Weight> weightList;
    private List<BodyLog> bodyLogList;
    private List<DayScore> dayScoreList;

    public List<Weight> getWeightList() {
        return weightList;
    }

    public void setWeightList(List<Weight> weightList) {
        this.weightList = weightList;
    }

    public List<BodyLog> getBodyLogList() {
        return bodyLogList;
    }

    public void setBodyLogList(List<BodyLog> bodyLogList) {
        this.bodyLogList = bodyLogList;
    }

    public List<DayScore> getDayScoreList() {
        return dayScoreList;
    }

    public void setDayScoreList(List<DayScore> dayScoreList) {
        this.dayScoreList = dayScoreList;
    }
}
