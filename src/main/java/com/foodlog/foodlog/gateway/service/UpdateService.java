package com.foodlog.foodlog.gateway.service;

import com.foodlog.foodlog.gateway.processor.activity.ActivityProcessor;
import com.foodlog.foodlog.gateway.processor.evolution.EvolutionProcessor;
import com.foodlog.foodlog.gateway.processor.newuser.NewUserProcessor;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.gateway.processor.Processor;
import com.foodlog.foodlog.gateway.processor.bodylog.BodyLogProcessor;
import com.foodlog.foodlog.gateway.processor.jaca.JacaProcessor;
import com.foodlog.foodlog.gateway.processor.none.NoneProcessor;
import com.foodlog.foodlog.gateway.processor.photo.PhotoProcessor;
import com.foodlog.foodlog.gateway.processor.prox.ProxProcessor;
import com.foodlog.foodlog.gateway.processor.rating.RatingProcessor;
import com.foodlog.foodlog.gateway.processor.textlog.TextLogProcessor;
import com.foodlog.foodlog.gateway.processor.timeline.TimelineProcessor;
import com.foodlog.foodlog.gateway.processor.undo.UndoProcessor;
import com.foodlog.foodlog.gateway.processor.weight.WeightProcessor;
import com.foodlog.foodlog.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rafael on 27/10/17.
 */
@Service
public class UpdateService {

    @Autowired
    public ProxProcessor proxProcessor;
    @Autowired
    public NoneProcessor noneProcessor;
    @Autowired
    public WeightProcessor weightProcessor;
    @Autowired
    public TimelineProcessor timelineProcessor;
    @Autowired
    public TextLogProcessor textLogProcessor;
    @Autowired
    public UndoProcessor undoProcessor;
    @Autowired
    public JacaProcessor jacaProcessor;
    @Autowired
    public RatingProcessor ratingProcessor;
    @Autowired
    public PhotoProcessor photoProcessor;
    @Autowired
    public BodyLogProcessor bodyLogProcessor;
    @Autowired
    public ActivityProcessor activityProcessor;
    @Autowired
    public NewUserProcessor newUserProcessor;
    @Autowired
    public EvolutionProcessor evolutionProcessor;




    private List<Processor> processors;


    private void init(){
        processors = new ArrayList<>();

        processors.add(proxProcessor);
        processors.add(noneProcessor);
        processors.add(weightProcessor);
        processors.add(timelineProcessor);
        processors.add(textLogProcessor);
        processors.add(undoProcessor);
        processors.add(jacaProcessor);
        processors.add(ratingProcessor);
        processors.add(photoProcessor);
        processors.add(bodyLogProcessor);
        processors.add(activityProcessor);
        processors.add(newUserProcessor);
        processors.add(evolutionProcessor);


    }


    public void processUpdate(Update update){
        update = adjustTime(update);
        if(processors ==  null){
            init();
        }

        Processor processor = noneProcessor;

        for (Processor p : processors){
            p.setUpdate(update);
            System.out.println("### Checando: " + p.getClass());
            if(p.check()){
                processor = p;
                break;
            }
        }
        System.out.println("### Processando: " + processor.getClass());
        processor.process();
    }


    protected Update adjustTime(Update update) {
        String regex = "([0-1]\\d|2[0-3]):([0-5]\\d)";

        if(Util.checkRegex(update, regex)){  //verifica xx:xx)

            Pattern r = Pattern.compile(regex);

            String text = update.getMessage().getText();
            if(text == null || text.trim().equals("")){
                text = update.getMessage().getCaption();
            }

            if(text != null && !text.trim().equals("")) {
                Matcher m = r.matcher(text);
                m.find();

                String newtime = m.group(0);


                for (int i = 0; i < m.groupCount(); i++) {
                    System.out.println("group " + i + ":" + m.group(i));
                }




                String time[] = newtime.split(":");

                ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));

                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);
                ZonedDateTime target = now.with(LocalTime.of(hour, minute));

                System.out.println("m.replaceAll(\"\"):" + m.replaceAll(""));
                update.setUpdateDateTime(Instant.from(target));
                update.getMessage().setCaption(m.replaceAll("").trim());
                update.getMessage().setText(m.replaceAll("").trim());
                return update;
            }
        }

        update.setUpdateDateTime(Instant.now());
        return update;

    }


}
