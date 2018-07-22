package com.foodlog.foodlog;

import com.foodlog.domain.*;
import com.foodlog.foodlog.gateway.service.UpdateService;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.report.bodylog.BodyLogImage;
import com.foodlog.foodlog.report.bodylog.BodyLogService;
import com.foodlog.foodlog.report.evolution.EvolutionTimeline;
import com.foodlog.foodlog.report.evolution.EvolutionTimelineService;
import com.foodlog.foodlog.report.timeline.MealLogDayService;
import com.foodlog.foodlog.report.timeline.dayStats.DayStats;
import com.foodlog.foodlog.report.timeline.dayStats.DayStatsService;
import com.foodlog.repository.JacaRepository;
import com.foodlog.repository.ScheduledMealRepository;
import com.foodlog.repository.UserRepository;
import com.foodlog.repository.WeightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by rafael on 09/11/17.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BodyLogService bodyLogService;

    @Autowired
    private JacaRepository jacaRepository;

    @Autowired
    private WeightRepository weightRepository;

    @Autowired
    private ScheduledMealRepository scheduledMealRepository;

    @Autowired
    private DayStatsService dayStatsService;

    @Autowired
    private MealLogDayService mealLogDayService;

    @Autowired
    private EvolutionTimelineService evolutionTimelineService;


    @CrossOrigin(origins = "*")
    @RequestMapping("/body-log")
    public BodyLogImage getBodyPanel(@RequestParam(value="userid") Long userid,
                                     @RequestParam(defaultValue = "panel", value="image-type") String type) {

        User user = userRepository.findOne(userid);

        if(type != null && type.trim().equals("gif")){
            return bodyLogService.getBodyGif(user);
        }
        return bodyLogService.getBodyPanel(user);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) throws Exception {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final CustomDateEditor dateEditor = new CustomDateEditor(df, true) {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if ("today".equals(text)) {
                    setValue(Instant.now());
                } else if ("yesterday".equals(text)){
                    setValue(Instant.now().minus(1, ChronoUnit.DAYS));
                } else if ("30daysago".equals(text)){
                    setValue(Instant.now().minus(30, ChronoUnit.DAYS));
                } else {
                    try {
                        setValue(df.parse(text));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        binder.registerCustomEditor(Instant.class, dateEditor);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping("/weights")
    public List<Weight> listWeightsByUser(@RequestParam(value="userid") Long userid,
                                          @RequestParam(value="init-date", defaultValue = "30daysago") Instant initDate,
                                          @RequestParam(value="end-date", defaultValue = "today") Instant endDate) {
        User user = userRepository.findOne(userid);
        return weightRepository.findByUserAndWeightDateTimeBetweenOrderByWeightDateTimeDesc(user, initDate, endDate);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping("/evolution-timeline")
    public EvolutionTimeline getEvolutionTimeline(@RequestParam(value="userid") Long userid,
                                                  @RequestParam(value="init-date", defaultValue = "30daysago") Instant initDate,
                                                  @RequestParam(value="end-date", defaultValue = "today") Instant endDate) {

        return evolutionTimelineService.getEvolutionTimeline(userid, initDate, endDate);
    }


    @CrossOrigin(origins = "*")
    @RequestMapping("/jacas")
    public List<Jaca> listJacass(@RequestParam(value="userid") Long userid) {
        User user = userRepository.findOne(userid);
        return jacaRepository.findTop30ByUserOrderByJacaDateTime(user);
    }


    @CrossOrigin(origins = "*")
    @RequestMapping("/meal-log")
    public List<MealLog> getAllMealLogDays(@RequestParam(value="userid") Long userid) {
        return mealLogDayService.findAll(userRepository.findOne(userid));
    }

    @CrossOrigin(origins = "*")
    @RequestMapping("/scheduled-meals")
    public List<ScheduledMeal> getAllScheduledMeals(@RequestParam(value="userid") Long userid) {
        return scheduledMealRepository.findByUserOrderByTargetTime(userRepository.findOne(userid));
    }

    @CrossOrigin(origins = "*")
    @RequestMapping("/day-stats")
    public DayStats getDayStats(@RequestParam(value="userid") Long userid){

        return dayStatsService.generateStats(userRepository.findOne(userid));
    }
}
