package com.foodlog.foodlog.gateway.processor.photo;

import com.foodlog.domain.MealLog;
import com.foodlog.domain.ScheduledMeal;
import com.foodlog.domain.User;

import com.foodlog.foodlog.gateway.emoji.string.Objects;
import com.foodlog.foodlog.gateway.emoji.string.People;
import com.foodlog.foodlog.gateway.openCV.PeopleDetector;
import com.foodlog.foodlog.gateway.processor.Processor;
import com.foodlog.repository.MealLogRepository;
import com.foodlog.repository.ScheduledMealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by rafael on 27/10/17.
 */
@Component
public class PhotoProcessor extends Processor{

    @Autowired
    MealLogRepository mealLogRepository;

    @Autowired
    ScheduledMealRepository scheduledMealRepository;

    @Autowired
    private PeopleDetector peopleDetector;

    @Autowired
    private MealLogFactory mealLogFactory;

    @Override
    public void process() {
        MealLog mealLog = mealLogFactory.create(update, getCurrentUser(update));

        String message = saveMealLogAndGenerateMessage(mealLog);
        message += "   Avalie enviando uma nota entre 0 e 5";

        sendMessage(message);
    }

    public String saveMealLogAndGenerateMessage(MealLog mealLog) {
        String message;
        MealLog mealLog1 = mealLogRepository.save(mealLog);
        message = People.BLACK_SMILING_FACE.toString();
        if (mealLog1.getScheduledMeal() == null) {
            message += People.THUMBS_UP.toString();
        } else {
            message += People.RIGHT_POINTING_BACKHAND_INDEX.toString()
                + " "
                + mealLog1.getScheduledMeal().getName();
        }

        message += calculateMealIntervals(getCurrentUser(update));
        return message;
    }
    private String calculateMealIntervals(User currentUser) {
        Instant now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
        List<MealLog> mealLogs2Days = mealLogRepository.findByUserAndMealDateTimeAfterOrderByMealDateTimeDesc(currentUser, now.truncatedTo(ChronoUnit.DAYS).minus(2, ChronoUnit.DAYS));

        float secondsSum = 0;
        float count = 0;

        Instant lastMealTime = null;
        for (MealLog mealLog: mealLogs2Days){
            if(lastMealTime != null) {

                //ZonedDateTime brTime = mealLog.getMealDateTime().atZone(ZoneId.of("America/Sao_Paulo"));
                Instant current = mealLog.getMealDateTime();

                float seconds = Duration.between(current, lastMealTime).getSeconds();

                //if (!brTime.truncatedTo(ChronoUnit.DAYS).isBefore(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).truncatedTo(ChronoUnit.DAYS))) { // passou um dia. ignora
                if(seconds > (60 * 60 * 5)) { //se for mais que 5 horas chegou no dia anterior. ai para
                    break;
                } else {
                    secondsSum += seconds;
                    count += 1F;
                }
            }

            lastMealTime =  mealLog.getMealDateTime();
        }

        float avgSeconds = (secondsSum/count);


        float milliseconds = avgSeconds * 1000;

        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);

        if(avgSeconds > 1) {
            return "\n" + Objects.STOP_WATCH.toString()
                + hours + "h:"+ minutes + "m ["
                + (int) ++count + "/" + scheduledMealRepository.findByUser(currentUser).size() + "] (" + Objects.ALARM_CLOCK.toString() //TODO arrumar
                + calcScheduledAvgIntervals() +")";
        } else {
            return "";
        }


    }

    private String calcScheduledAvgIntervals() {

        ZonedDateTime lastMealTime = null;
        float secondsSum = 0;
        float count = 0;

        for(ScheduledMeal scheduledMeal:scheduledMealRepository.findByOrderByTargetTimeDesc()){
            ZonedDateTime current = getZonedTargetTime(scheduledMeal);
            if(lastMealTime != null && !current.equals(lastMealTime)) {
                float seconds = Duration.between(current, lastMealTime).getSeconds();
                secondsSum += seconds;
                count += 1F;
            }
            lastMealTime = current;
        }
        float avgSeconds = (secondsSum/count);


        float milliseconds = avgSeconds * 1000;

        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);


        return hours + "h:"+ minutes + "m";

    }

    private ZonedDateTime getZonedTargetTime(ScheduledMeal scheduledMeal) {
        String time[] = scheduledMeal.getTargetTime().split(":");

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));

        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        return now.with(LocalTime.of(hour, minute));

    }

    @Override
    public boolean check() {
        if (update.getMessage().getPhoto() != null && update.getMessage().getPhoto().size() > 0) {
            byte[] photo = mealLogFactory.getPicture(update);
            byte[] imagePeopleBytes = peopleDetector.getPeopleInPhoto(photo);
            if(imagePeopleBytes != null){
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    public void setMealLogFactory(MealLogFactory mealLogFactory) {
        this.mealLogFactory = mealLogFactory;
    }

    public void setPeopleDetector(PeopleDetector peopleDetector) {
        this.peopleDetector = peopleDetector;
    }
}
