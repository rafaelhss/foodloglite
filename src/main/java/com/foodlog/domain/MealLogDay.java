package com.foodlog.domain;


import javax.persistence.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A MealLogDay.
 */
@Entity
@Table(name = "meal_log_day")
public class MealLogDay implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "meal_log_day_date")
    private ZonedDateTime mealLogDayDate;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getMealLogDayDate() {
        return mealLogDayDate;
    }

    public MealLogDay mealLogDayDate(ZonedDateTime mealLogDayDate) {
        this.mealLogDayDate = mealLogDayDate;
        return this;
    }

    public void setMealLogDayDate(ZonedDateTime mealLogDayDate) {
        this.mealLogDayDate = mealLogDayDate;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MealLogDay mealLogDay = (MealLogDay) o;
        if (mealLogDay.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), mealLogDay.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MealLogDay{" +
            "id=" + getId() +
            ", mealLogDayDate='" + getMealLogDayDate() + "'" +
            "}";
    }
}
