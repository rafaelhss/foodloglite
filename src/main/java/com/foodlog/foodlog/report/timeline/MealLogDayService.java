package com.foodlog.foodlog.report.timeline;



import com.foodlog.domain.MealLog;
import com.foodlog.domain.User;
import com.foodlog.foodlog.util.Util;
import com.foodlog.repository.MealLogRepository;
import com.foodlog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafael on 07/07/17.
*/
@Service
public class MealLogDayService {

    @Autowired
    private MealLogRepository mealLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    Util util;

    public List<MealLog> findAllByDate(User currentUser, Instant refDate) {


       // ZonedDateTime baseDate = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));

        //baseDate = baseDate.minus(1, ChronoUnit.DAYS);

        Instant today4am = util.getInstant4AM(refDate);

        System.out.println("today4am: " + today4am);

        Instant tomorrow4am = today4am.plus(1, ChronoUnit.DAYS);
        System.out.println("tomorrow4am: " + tomorrow4am);



        return mealLogRepository.findByUserAndMealDateTimeBetweenOrderByMealDateTimeDesc(currentUser, today4am, tomorrow4am);

        /*

        List<MealLog> result = new ArrayList<>();

        MealLog lastProcessed = null;

       for (MealLog mealLog : mealLogRepository.findByUserAndMealDateTimeBetweenOrderByMealDateTimeDesc(currentUser, yesterday, tomorrow)) {
            System.out.println(mealLog.getId() + " " + mealLog.getMealDateTime() + "     " + mealLog.getMealDateTime().atZone(ZoneId.of("America/Sao_Paulo")));

            if (lastProcessed == null) {
                lastProcessed = mealLog;
            }

            long elapsedMealTime = Duration.between(mealLog.getMealDateTime(), lastProcessed.getMealDateTime()).getSeconds() / (60 * 60);

            System.out.println("Achando a noite. " + mealLog.getMealDateTime() + "(epalsed: " + elapsedMealTime + "): " + (elapsedMealTime < BatchConfigs.SLEEP_INTERVAL));
            // Nao achei a noite, entao computa. Isso eh pra atender casos em que come de madruga
            if (elapsedMealTime < BatchConfigs.SLEEP_INTERVAL) {

                result.add(mealLog);

                lastProcessed = mealLog;
            } else {
                //achei a noite, acaba o processamento
                break;
            }
        }

        return result;
        */
    }


}
