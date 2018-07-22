package com.foodlog.repository;

import com.foodlog.domain.BodyLog;
import com.foodlog.domain.User;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for the BodyLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BodyLogRepository extends JpaRepository<BodyLog, Long> {

    @Query("select body_log from BodyLog body_log where body_log.user.login = ?#{principal.username}")
    List<BodyLog> findByUserIsCurrentUser();

    List<BodyLog> findByUserOrderByBodyLogDatetime(User user);

    List<BodyLog> findByUserAndBodyLogDatetimeBetweenOrderByBodyLogDatetimeDesc(User user, Instant initDate, Instant endDate);

    BodyLog findTop1ByUserOrderByBodyLogDatetimeDesc(User currentUser);
}
