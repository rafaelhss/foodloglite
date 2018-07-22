package com.foodlog.foodlog.report.timeline.dayStats;


import com.foodlog.domain.MealLog;
import com.foodlog.domain.ScheduledMeal;
import com.foodlog.domain.User;
import com.foodlog.repository.MealLogRepository;
import com.foodlog.repository.ScheduledMealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by rafa on 13/08/17.
 */
@Service
public class DayStatsService {

    @Autowired
    MealLogRepository mealLogRepository;

    @Autowired
    ScheduledMealRepository scheduledMealRepository;

    private final int BASIC_PENALIZATION = 5;
    private final int LOW_PENALIZATION = BASIC_PENALIZATION * 1;
    private final int MEDIUM_PENALIZATION = LOW_PENALIZATION * 2;
    private final int HIGH_PENALIZATION = MEDIUM_PENALIZATION * 3;

    private final int MAX_RATING = 5;
    private final int MIN_RATING = 0;
    private static final Integer EXTRA_MEAL_PENALTY = 3;
    private static final Integer SKIPPED_MEAL_RATING = 4;



    public DayStats generateStats(User currentUser) {

        List<MealLog> mealLogs = getMeallogWindow(currentUser);

        DayStats dayStats = generateTimeRelatedStats(currentUser, mealLogs);

        dayStats = calculateRatingScore(dayStats, currentUser, mealLogs);

        return dayStats;

    }

    private DayStats calculateRatingScore(DayStats dayStats, User currentUser, List<MealLog> mealLogs) {

        List<Integer> ratings = mealLogs.stream().map(mealLog -> mealLog.getRating()).sorted(Comparator.comparingInt(Integer::intValue).reversed()).collect(Collectors.toList());

        int scheduledMeals = getScheduledMeals(currentUser);

        int rawRatingScore = 0;

        String penaltyDescription = "Reported meals(" + ratings.size() + "), Scheduled Meals(" + scheduledMeals + "), Ratings{";


        for (int i=0; i < ratings.size(); i++) {
            System.out.println("rating: " + ratings.get(i));
            penaltyDescription += ratings.get(i) + ",";

            int ratingToAdd = ratings.get(i);

            //se comeu mais do que devia, pau!
            if(scheduledMeals > 0 && (i+1) > scheduledMeals){
                ratingToAdd = (ratings.get(i) - EXTRA_MEAL_PENALTY);
            }

            System.out.println("ratingToAdd: " + ratingToAdd);

            rawRatingScore += ratingToAdd;
        }

        penaltyDescription += "}";

        float max = mealLogs.size();
        // se comeu menos do que devia, soma refeições não 100% para sensibilizar negativamente a nota
        if(scheduledMeals > mealLogs.size()){
            rawRatingScore += (scheduledMeals - mealLogs.size()) * SKIPPED_MEAL_RATING;
            max = scheduledMeals;
        }

        max = max * MAX_RATING;

        //percentual de pontos em face do que poderia ser
        float ratingScore = ((float)rawRatingScore) / (max);



        System.out.println("");
        System.out.println("max: " + max);
        System.out.println("rawRatingScore: " +rawRatingScore);
        System.out.println("mealLogs.size(): " +mealLogs.size());
        System.out.println("scheduledMeals: " +scheduledMeals);
        System.out.println("ratingScore: " +ratingScore);
        System.out.println("Math.log(ratingScore): " + ((1) + Math.log(ratingScore)/Math.log(2)));
        System.out.println("xxxxxxxxxx");


        float ratingScoreAjusted = 100 * (float) (((1) + Math.log(ratingScore)/Math.log(2)));

        penaltyDescription += ", Log(" + new DecimalFormat("#.##").format(ratingScoreAjusted) + ")";
        penaltyDescription += ", Score(" + new DecimalFormat("#.##").format(ratingScore) + ")";


        dayStats.getDayScore().getPenalties().add(penaltyDescription);


        dayStats.getDayScore().setRatingScore(ratingScoreAjusted);


        return  dayStats;

    }

    private List<MealLog> getMeallogWindow(User currentUser) {
        List<MealLog> result = new ArrayList<MealLog>();

        Instant now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
        List<MealLog> mealLogs2Days = mealLogRepository.findByUserAndMealDateTimeAfterOrderByMealDateTimeDesc(currentUser, now.truncatedTo(ChronoUnit.DAYS).minus(2, ChronoUnit.DAYS));


        Instant lastMealTime = null;
        for (MealLog mealLog : mealLogs2Days) {
            if (lastMealTime != null) {

                Instant current = mealLog.getMealDateTime();
                float seconds = Duration.between(current, lastMealTime).getSeconds();

                if (seconds > (60 * 60 * 5)) { //se for mais que 5 horas chegou no dia anterior. ai para
                    break;
                } else {
                    result.add(mealLog);
                }
            } else {
                result.add(mealLog);
            }
            lastMealTime =  mealLog.getMealDateTime();
        }
        return result;
    }


    private DayStats generateTimeRelatedStats(User currentUser, List<MealLog> mealLogs) {
        DayStats dayStats = calculateMealIntervals(new DayStats(), currentUser, mealLogs);

        int score = 100;

        // penalizacao por cada refeicao fora de hora,
        // independentemente de quantidade
        int penalty = (LOW_PENALIZATION * dayStats.getOffTimeMeals());
        score -= penalty;
        if(penalty > 0)
            dayStats.getDayScore().getPenalties().add("Refeicao fora de hora ("+ dayStats.getOffTimeMeals() +"x): -" +penalty);

        //penalizacao por refeicao extra alem do agendado
        //Ou por refeicao que ficou devendo
        int meals = Math.abs(dayStats.getScheduledMeals() - dayStats.getLoggedMeals());
        penalty = (MEDIUM_PENALIZATION *meals);
        score -= penalty;
        if(penalty > 0)
            dayStats.getDayScore().getPenalties().add("Refeicao extra ou faltante (" + meals + "x): -" +penalty);

        //penalizacao por cada intervalo maior
        // que 3 horas sem comer
        penalty = (HIGH_PENALIZATION * dayStats.getTreePlusHourIntervals());
        score -= penalty;
        if(penalty > 0)
            dayStats.getDayScore().getPenalties().add("Intervalo maior que 3 horas (" + dayStats.getTreePlusHourIntervals() +"x) : -" +penalty);

        //penalizacao por diferenca grande entre intervalo
        //medio previsto e realizado. 30 min de tolerancia
        float diff = dayStats.getAvgSecondsBetweenMeals() - dayStats.getScheduledAvgSecondsBetweenMeals();
        if(Math.abs(diff) > (60 * 30)){
            penalty = HIGH_PENALIZATION;
            score -= penalty;
            dayStats.getDayScore().getPenalties().add("Interv. medio diff do esperado: -" +penalty);
        }

        dayStats.getDayScore().setScore(score);

        return dayStats;
    }


    private DayStats calculateMealIntervals(DayStats dayStats, User currentUser, List<MealLog> mealLogs) {

        float secondsSum = 0;
        float count = 0;

        int count3hourintervals = 0;
        int countOnTime = 0;
        int countOffTime = 0;

        Instant lastMealTime = null;
        for (MealLog mealLog: mealLogs){
            if(lastMealTime != null) {
                Instant current = mealLog.getMealDateTime();

                float seconds = Duration.between(current, lastMealTime).getSeconds();

                if(seconds > (60 * 60 * 3)){
                    count3hourintervals++;
                }
                if(mealLog.getScheduledMeal() != null){
                    countOnTime++;
                } else {
                    countOffTime++;
                }

                secondsSum += seconds;
                count += 1F;
                System.out.println(mealLog.getId() + "----> " + mealLog.getMealDateTime() + " ---> " + Duration.between(mealLog.getMealDateTime(), lastMealTime).getSeconds());
            }

            lastMealTime =  mealLog.getMealDateTime();
        }

        float avgSeconds = (secondsSum/count);

        if(avgSeconds > 1) {
            dayStats.setAvgSecondsBetweenMeals(avgSeconds);
            dayStats.setLoggedMeals((int) ++count);
            dayStats.setScheduledMeals(getScheduledMeals(currentUser));
            dayStats.setScheduledAvgSecondsBetweenMeals(calcScheduledAvgIntervals(currentUser));
            dayStats.setTreePlusHourIntervals(count3hourintervals);
            dayStats.setOnTimeMeals(countOnTime);
            dayStats.setOffTimeMeals(countOffTime);
        }
        return dayStats;
    }

    private float calcScheduledAvgIntervals(User currentUser) {

        ZonedDateTime lastMealTime = null;
        float secondsSum = 0;
        float count = 0;

        for(ScheduledMeal scheduledMeal:scheduledMealRepository.findByUserOrderByTargetTimeDesc(currentUser)){
            ZonedDateTime current = getZonedTargetTime(scheduledMeal);
            if(lastMealTime != null && !current.equals(lastMealTime)) {
                float seconds = Duration.between(current, lastMealTime).getSeconds();
                secondsSum += seconds;
                count += 1F;
            }
            lastMealTime = current;
        }
        float avgSeconds = (secondsSum/count);

        return avgSeconds;
    }

    private ZonedDateTime getZonedTargetTime(ScheduledMeal scheduledMeal) {
        String time[] = scheduledMeal.getTargetTime().split(":");

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));

        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        return now.with(LocalTime.of(hour, minute));
    }

    private int getScheduledMeals(User currentUser) {

        HashMap<String,String> aux = new HashMap<>();
        int count = 0;
        for(ScheduledMeal scheduledMeal:scheduledMealRepository.findByUserOrderByTargetTimeDesc(currentUser)){
            if (aux.get(scheduledMeal.getTargetTime()) == null){
                aux.put(scheduledMeal.getTargetTime(),scheduledMeal.getTargetTime());
                count++;
            }
        }
        return count;
    }

}
