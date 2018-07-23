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
import com.foodlog.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private ActivityRepository activityRepository;

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
                        setValue(df.parse(text).toInstant() );
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
    @RequestMapping("/weight")
    public Weight listWeightsByUser(@RequestParam(value="userid") Long userid,
                                    @RequestParam(value="ref-date", defaultValue = "today") Instant refDate) {

        System.out.println("refDate = " + refDate);

        Instant today = refDate.truncatedTo(ChronoUnit.DAYS);

        Instant tomorrow = refDate.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);

        User user = userRepository.findOne(userid);
        return weightRepository.findTop1ByUserAndWeightDateTimeBetween(user, today, tomorrow);
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
    public List<MealLog> findAllMealLogByDate(
            @RequestParam(value="userid") Long userid,
            @RequestParam(value="ref-date", defaultValue = "today") Instant refDate) {
        return mealLogDayService.findAllByDate(userRepository.findOne(userid), refDate);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping("/activity")
    public List<Activity> getAllActivities(@RequestParam(value="userid") Long userid,
                                           @RequestParam(value="ref-date", defaultValue = "today") Instant refDate) {
        Instant today4am = refDate.atZone(ZoneId.of("America/Sao_Paulo"))
                .truncatedTo(ChronoUnit.DAYS)
                .withZoneSameInstant(ZoneId.of("America/Sao_Paulo"))
                .plusHours(4L)
                .toInstant();

        System.out.println("activity today4am: " + today4am);

        Instant tomorrow4am = today4am.plus(1, ChronoUnit.DAYS);
        System.out.println("activity tomorrow4am: " + tomorrow4am);



        User currentUser = userRepository.findOne(userid);
        return activityRepository.findByUserAndActivitydatetimeBetween(currentUser, today4am, tomorrow4am);
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
