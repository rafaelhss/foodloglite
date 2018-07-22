package com.foodlog.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Activity.
 */
@Entity
@Table(name = "activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "activitydatetime", nullable = false)
    private Instant activitydatetime;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    @Column(name = "intensity", nullable = false)
    private Integer intensity;

    @ManyToOne(optional = false)
    @NotNull
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Activity name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getActivitydatetime() {
        return activitydatetime;
    }

    public Activity activitydatetime(Instant activitydatetime) {
        this.activitydatetime = activitydatetime;
        return this;
    }

    public void setActivitydatetime(Instant activitydatetime) {
        this.activitydatetime = activitydatetime;
    }

    public Integer getIntensity() {
        return intensity;
    }

    public Activity intensity(Integer intensity) {
        this.intensity = intensity;
        return this;
    }

    public void setIntensity(Integer intensity) {
        this.intensity = intensity;
    }

    public User getUser() {
        return user;
    }

    public Activity user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
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
        Activity activity = (Activity) o;
        if (activity.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), activity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Activity{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", activitydatetime='" + getActivitydatetime() + "'" +
            ", intensity=" + getIntensity() +
            "}";
    }
}
