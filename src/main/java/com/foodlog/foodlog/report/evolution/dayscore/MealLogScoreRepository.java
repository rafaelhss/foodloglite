package com.foodlog.foodlog.report.evolution.dayscore;

import com.foodlog.domain.MealLog;
import com.foodlog.foodlog.report.evolution.dayscore.MealLogScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Created by rafael on 11/01/18.
 */

// Se regerar a app e der UnsatisfiedDependencyException, colocar @EnableJpaRepositories na classe principal FoodlogbotadmApp.java
@Repository
public interface MealLogScoreRepository extends JpaRepository<MealLog, Long> {
    @Query("select m.mealDateTime as mealDateTime, m.rating as rating from MealLog m where m.user.id = :userid and m.mealDateTime between :init and :end order by m.mealDateTime desc")
    List<MealLogScore> getMealLogScore(@Param("userid") Long userid, @Param("init") Instant init, @Param("end") Instant end);
}
