package com.foodlog.foodlog.gateway.processor.prox;

import com.foodlog.domain.ScheduledMeal;
import com.foodlog.domain.User;
import com.foodlog.foodlog.gateway.processor.Processor;
import com.foodlog.repository.ScheduledMealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by rafael on 27/10/17.
 */
@Component
public class ProxProcessor extends Processor {

    @Autowired
    private ScheduledMealRepository scheduledMealRepository;

    @Override
    public void process() {

        String message = "";

        ZonedDateTime nextTime = ZonedDateTime.now().plus(1, ChronoUnit.DAYS);
        ScheduledMeal next = null;

        try {
            User current = getCurrentUser(update);
            System.out.println(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")));
            for (ScheduledMeal scheduledMeal : scheduledMealRepository.findByUser(current)) {
                if(next != null) {

                    System.out.println(scheduledMeal.getTargetTime() + "(" + scheduledMeal.getName() +") " + getZonedTargetTime(scheduledMeal));
                    //System.out.println(getZonedTargetTime(scheduledMeal) + " " + getZonedTargetTime(next));
                    System.out.println(getZonedTargetTime(scheduledMeal).isAfter(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")))
                        && getZonedTargetTime(scheduledMeal).isBefore(getZonedTargetTime(next)));
                } else {
                    System.out.println(scheduledMeal.getTargetTime() + "(" + scheduledMeal.getName() +") " + getZonedTargetTime(scheduledMeal));
                }


                if(getZonedTargetTime(scheduledMeal).isAfter(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")))
                    && getZonedTargetTime(scheduledMeal).isBefore(nextTime)) {
                    next = scheduledMeal;
                    nextTime = getZonedTargetTime(scheduledMeal);
                }
            }

            if (next == null) {
                message = "Não achei a proxima.";
            } else {
                message = "Sua proxima refeição agendada: " + next.getName() + "(" + next.getTargetTime() + ") " + next.getDescription();
            }

            sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
        }


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
        return update.getMessage().getText() != null && update.getMessage().getText().trim().toLowerCase().equals("prox");
    }
}
