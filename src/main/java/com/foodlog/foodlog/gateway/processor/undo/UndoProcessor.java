package com.foodlog.foodlog.gateway.processor.undo;

import com.foodlog.domain.Activity;
import com.foodlog.domain.BodyLog;
import com.foodlog.domain.MealLog;
import com.foodlog.domain.Weight;
import com.foodlog.foodlog.gateway.processor.Processor;
import com.foodlog.repository.ActivityRepository;
import com.foodlog.repository.BodyLogRepository;
import com.foodlog.repository.MealLogRepository;
import com.foodlog.repository.WeightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Created by rafael on 27/10/17.
 */
@Component
public class UndoProcessor extends Processor{
    @Autowired
    MealLogRepository mealLogRepository;
    @Autowired
    BodyLogRepository bodyLogRepository;
    @Autowired
    WeightRepository weightRepository;
    @Autowired
    ActivityRepository activityRepository;

    @Override
    public void process() {
        JpaRepository repo = null;
        Long idToDelete = 0L;
        Instant refDate = Instant.MIN;

        String message = "Nada ";


        MealLog mealLog = mealLogRepository
            .findTop1ByUserOrderByMealDateTimeDesc(getCurrentUser(update));
        if(mealLog != null && mealLog.getMealDateTime().isAfter(refDate)){
            repo = mealLogRepository;
            idToDelete = mealLog.getId();
            refDate = mealLog.getMealDateTime();
            message = "MealLog ";
        }

        Weight weight = weightRepository
            .findTop1ByUserOrderByWeightDateTimeDesc(getCurrentUser(update));
        if(weight != null && weight.getWeightDateTime().isAfter(refDate)){
            repo = weightRepository;
            idToDelete = weight.getId();
            refDate = weight.getWeightDateTime();
            message = "Peso ";
        }

        BodyLog bodyLog = bodyLogRepository
            .findTop1ByUserOrderByBodyLogDatetimeDesc(getCurrentUser(update));
        if(bodyLog != null && bodyLog.getBodyLogDatetime().isAfter(refDate)){
            repo = bodyLogRepository;
            idToDelete = bodyLog.getId();
            refDate = bodyLog.getBodyLogDatetime();
            message = "Bodylog ";
        }

        Activity activity = activityRepository
            .findTop1ByUserOrderByActivitydatetimeDesc(getCurrentUser(update));
        if(activity != null && activity.getActivitydatetime().isAfter(refDate)){
            repo = activityRepository;
            idToDelete = activity.getId();
            refDate = activity.getActivitydatetime();
            message = "activity ";
        }

        if(repo != null) {
            repo.delete(idToDelete);
        }

        sendMessage(message + "removido");
    }

    @Override
    public boolean check() {
        return update.getMessage().getText() != null &&
                update.getMessage().getText().trim().toLowerCase().equals("undo");
    }
}
