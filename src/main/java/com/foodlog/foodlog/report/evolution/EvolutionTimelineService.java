package com.foodlog.foodlog.report.evolution;

import com.foodlog.domain.BodyLog;
import com.foodlog.domain.User;
import com.foodlog.foodlog.report.evolution.dayscore.DayScore;
import com.foodlog.foodlog.report.evolution.dayscore.MealLogScore;
import com.foodlog.foodlog.report.evolution.dayscore.MealLogScoreRepository;
import com.foodlog.repository.BodyLogRepository;
import com.foodlog.repository.UserRepository;
import com.foodlog.repository.WeightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by rafael on 11/01/18.
 */
@Service
public class EvolutionTimelineService {
    private static final int MAX_IMAGES = 4;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeightRepository weightRepository;

    @Autowired
    private BodyLogRepository bodyLogRepository;

    @Autowired
    private MealLogScoreRepository mealLogScoreRepository;

    private BufferedImage getBufferedImage(BodyLog log)  {
        try {
            return ImageIO.read(new ByteArrayInputStream(log.getPhoto()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public EvolutionTimeline getEvolutionTimeline(Long userid, Instant initDate, Instant endDate) {
        EvolutionTimeline result = new EvolutionTimeline();

        User user = userRepository.findOne(userid);

        //Weight
        getWeight(initDate, endDate, result, user);


        //BodyLog
        getBodyLog(initDate, endDate, result, user);


        //DayStats
        getDayStats(userid, initDate, endDate, result);


        return result;
    }

    private void getDayStats(Long userid, Instant initDate, Instant endDate, EvolutionTimeline result) {
        List<MealLogScore> mealLogScoreList = mealLogScoreRepository.getMealLogScore(userid, initDate, endDate);

        mealLogScoreList.stream().forEach(mealLogScore -> System.out.println("MeallogScore: " + mealLogScore.getRating() + " - " + mealLogScore.getMealDateTime()));


        Map<Instant, Double> averageRating = mealLogScoreList.stream().collect(
            Collectors.groupingBy(m -> m.getMealDateTime().truncatedTo(ChronoUnit.DAYS),
                Collectors.averagingInt(m -> { if(m.getRating() == null) {
                                                    return 3;
                                                } else {
                                                    return m.getRating();
                                                }
                                            })));


        List<DayScore> dayScoreList = averageRating.entrySet().stream()
            .map(a -> {
                    DayScore dayScore = new DayScore();
                    dayScore.setDay(a.getKey());
                    dayScore.setScore(a.getValue());
                    return dayScore;
             })
            .collect(Collectors.toList());


        result.setDayScoreList(dayScoreList);
    }

    private void getBodyLog(Instant initDate, Instant endDate, EvolutionTimeline result, User user) {
        List<BodyLog> bodyLogs = bodyLogRepository.findByUserAndBodyLogDatetimeBetweenOrderByBodyLogDatetimeDesc(user, initDate, endDate);
        result.setBodyLogList(new ArrayList<>());

        if(bodyLogs != null && !bodyLogs.isEmpty()) {
            //A primeira imagem eh a primeira (0 tercos)
            //A proxima esta a 1 terco do fim
            //A proxima esta a 2 tercos do fim
            //a ultima eh a ultima (3 tercos)
            for (int i = 0; i < MAX_IMAGES; i++) {
                float factor = Float.valueOf((Float.valueOf(i) / Float.valueOf(MAX_IMAGES - 1)));
                System.out.println("factor: " + factor);
                int index = Math.round(Float.valueOf((float) ((bodyLogs.size() - 1) * factor)));
                System.out.println("index: " + index);
                BodyLog bodyLog = bodyLogs.get(index);
                bodyLog.setUser(null);// da uma limpada so pra economizar dados
                result.getBodyLogList().add(bodyLog);
            }
        }
    }

    private void getWeight(Instant initDate, Instant endDate, EvolutionTimeline result, User user) {
        result.setWeightList(weightRepository.findByUserAndWeightDateTimeBetweenOrderByWeightDateTimeDesc(user, initDate, endDate));
    }
}
