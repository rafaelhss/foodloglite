package com.foodlog.foodlog.gateway.processor.rating;

import com.foodlog.domain.BodyLog;
import com.foodlog.domain.MealLog;
import com.foodlog.domain.Weight;
import com.foodlog.foodlog.gateway.processor.Processor;
import com.foodlog.foodlog.util.Util;
import com.foodlog.repository.BodyLogRepository;
import com.foodlog.repository.MealLogRepository;
import com.foodlog.repository.WeightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Created by rafael on 27/10/17.
 */
@Component
public class RatingProcessor extends Processor{

    @Autowired
    private MealLogRepository mealLogRepository;
    @Autowired
    private WeightRepository weightRepository;

    @Autowired
    private BodyLogRepository bodyLogRepository;

    @Override
    public void process() {
        MealLog mealLog = mealLogRepository
            .findTop1ByUserOrderByMealDateTimeDesc(getCurrentUser(update));
        Integer rating = Integer.parseInt(update.getMessage().getText().trim());
        mealLog.setRating(rating);
        mealLogRepository.save(mealLog);
        sendMessage("Rating atualizado no meallog");
    }

    @Override
    public boolean check() {
        if(Util.checkRegex(update, "^[0-5]+$")){
            MealLog mealLog = mealLogRepository
                    .findTop1ByUserOrderByMealDateTimeDesc(getCurrentUser(update));
            if(mealLog == null) {
                return false;
            }

            Instant refDate = mealLog.getMealDateTime();

            Weight weight = weightRepository
                .findTop1ByUserOrderByWeightDateTimeDesc(getCurrentUser(update));


            if (weight != null && weight.getWeightDateTime().isAfter(refDate)) {
                return false;
            }

            BodyLog bodyLog = bodyLogRepository
                .findTop1ByUserOrderByBodyLogDatetimeDesc(getCurrentUser(update));
            if (bodyLog != null && bodyLog.getBodyLogDatetime().isAfter(refDate)) {
                return false;
            }

            return true;

        } else {
            return false;
        }

    }
}
