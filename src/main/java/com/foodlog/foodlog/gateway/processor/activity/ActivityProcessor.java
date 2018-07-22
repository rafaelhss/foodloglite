package com.foodlog.foodlog.gateway.processor.activity;

import com.foodlog.domain.Activity;
import com.foodlog.domain.Jaca;
import com.foodlog.foodlog.gateway.processor.Processor;
import com.foodlog.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by rafael on 17/01/18.
 */
@Service
public class ActivityProcessor extends Processor {

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public void process() {

        System.out.println("Process " + this.getClass().getName());
        Activity activity = new Activity();

        activity.setActivitydatetime(update.getUpdateDateTime());
        activity.setUser(getCurrentUser(update));
        String fulltext = update.getMessage().getText().toLowerCase();
        fulltext = fulltext.replace("activity" , ""); //activity jiu 4


        String[] text = fulltext.trim().split(" "); // jiu 4

        activity.setName(text[0]);
        Activity known = activityRepository.findTop1ByNameContaining(text[0]);

        if(known != null){
            activity.setIntensity(known.getIntensity());
            activity.setName(known.getName());
        } else if(text.length == 2){
            try {
                activity.setIntensity(Integer.parseInt(text[1]));
            } catch (Exception e){
                activity.setIntensity(1);
            }
        } else {
            activity.setIntensity(1);
        }

        activityRepository.save(activity);

        sendMessage("Atividade " + activity.getName() + " salva comsuceso.");
    }

    @Override
    public boolean check() {
        try {
            return update.getMessage().getText().toLowerCase().indexOf("activity") == 0 || checkKnownActivity();
        } catch (Exception e){
            return false;
        }
    }

    private boolean checkKnownActivity() {
        try {
            String first = update.getMessage().getText().toLowerCase().split(" ")[0];
            return activityRepository.findTop1ByNameContaining(first) != null;
        } catch (Exception e){
            return false;
        }

    }
}
