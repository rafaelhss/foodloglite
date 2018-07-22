package com.foodlog.repository;

import com.foodlog.domain.Activity;
import com.foodlog.domain.MealLog;
import com.foodlog.domain.User;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for the Activity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("select activity from Activity activity where activity.user.login = ?#{principal.username}")
    List<Activity> findByUserIsCurrentUser();

    Activity findTop1ByNameContaining(String s);

    Activity findTop1ByUserOrderByActivitydatetimeDesc(User currentUser);



    List<Activity> findByUserAndActivitydatetimeBetween(User currentUser, Instant yesterday, Instant tomorrow);
}
