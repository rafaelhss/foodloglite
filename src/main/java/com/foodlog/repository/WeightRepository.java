package com.foodlog.repository;

import com.foodlog.domain.User;
import com.foodlog.domain.Weight;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for the Weight entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WeightRepository extends JpaRepository<Weight, Long> {

    @Query("select weight from Weight weight where weight.user.login = ?#{principal.username}")
    List<Weight> findByUserIsCurrentUser();

    List<Weight> findByUserAndWeightDateTimeBetweenOrderByWeightDateTimeDesc(User user, Instant initDate, Instant endDate);

    Weight findTop1ByUserOrderByWeightDateTimeDesc(User currentUser);


    Weight findTop1ByUserAndWeightDateTimeBetween(User user, Instant today, Instant tomorrow);
}
