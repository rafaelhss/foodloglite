package com.foodlog.foodlog.util;

import com.foodlog.domain.ScheduledMeal;
import com.foodlog.domain.User;
import com.foodlog.domain.UserTelegram;
import com.foodlog.repository.ScheduledMealRepository;
import com.foodlog.repository.UserRepository;
import com.foodlog.repository.UserTelegramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Random;

/**
 * Created by rafael on 07/11/17.
 */
@Component
public class TestUtil {
    @Autowired
    private UserTelegramRepository userTelegramRepository;

    @Autowired
    private ScheduledMealRepository scheduledMealRepository;

    @Autowired
    private UserRepository userRepository;

    public void createUser() {
        Integer id = 153350155;

        UserTelegram userTelegram = userTelegramRepository.findOneByTelegramId(id);
        if(userTelegram == null) {
            userTelegram = new UserTelegram();
        }



        userTelegram.setTelegramId(id);
        com.foodlog.domain.User admin = userRepository.findOneByLogin("admin").get();
        userTelegram.setUser(admin);

        userTelegramRepository.save(userTelegram);

    }


    public ScheduledMeal createScheduledMeal(ZonedDateTime time, String name) {
        String targetTime = time.toString();

        targetTime = targetTime.substring(targetTime.indexOf("T")+1);
        targetTime = targetTime.substring(0,targetTime.indexOf(":") + 3);

        name  = name.replace("@@TIME", targetTime);
        return createScheduledMeal(targetTime, name);
    }

    public ScheduledMeal createScheduledMeal(String targetTime, String name) {


        ScheduledMeal scheduledMeal = new ScheduledMeal();
        if(getUser() == null){
            createUser();
        }
        scheduledMeal.setUser(getUser());
        scheduledMeal.setTargetTime(targetTime);
        scheduledMeal.setName(name);
        scheduledMeal.setDescription("Panelada, buchada, sarrabu, tripa de porco");

        return scheduledMealRepository.save(scheduledMeal);
    }

    public User getUser(){
        return userRepository.findOneByLogin("admin").get();
    };

}
