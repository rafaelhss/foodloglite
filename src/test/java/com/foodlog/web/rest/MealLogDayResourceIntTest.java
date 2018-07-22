package com.foodlog.web.rest;

import com.foodlog.FoodlogbotadmApp;

import com.foodlog.domain.MealLogDay;
import com.foodlog.repository.MealLogDayRepository;
import com.foodlog.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.foodlog.web.rest.TestUtil.sameInstant;
import static com.foodlog.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MealLogDayResource REST controller.
 *
 * @see MealLogDayResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class MealLogDayResourceIntTest {

    private static final ZonedDateTime DEFAULT_MEAL_LOG_DAY_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_MEAL_LOG_DAY_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private MealLogDayRepository mealLogDayRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMealLogDayMockMvc;

    private MealLogDay mealLogDay;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MealLogDayResource mealLogDayResource = new MealLogDayResource(mealLogDayRepository);
        this.restMealLogDayMockMvc = MockMvcBuilders.standaloneSetup(mealLogDayResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MealLogDay createEntity(EntityManager em) {
        MealLogDay mealLogDay = new MealLogDay()
            .mealLogDayDate(DEFAULT_MEAL_LOG_DAY_DATE);
        return mealLogDay;
    }

    @Before
    public void initTest() {
        mealLogDay = createEntity(em);
    }

    @Test
    @Transactional
    public void createMealLogDay() throws Exception {
        int databaseSizeBeforeCreate = mealLogDayRepository.findAll().size();

        // Create the MealLogDay
        restMealLogDayMockMvc.perform(post("/api/meal-log-days")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mealLogDay)))
            .andExpect(status().isCreated());

        // Validate the MealLogDay in the database
        List<MealLogDay> mealLogDayList = mealLogDayRepository.findAll();
        assertThat(mealLogDayList).hasSize(databaseSizeBeforeCreate + 1);
        MealLogDay testMealLogDay = mealLogDayList.get(mealLogDayList.size() - 1);
        assertThat(testMealLogDay.getMealLogDayDate()).isEqualTo(DEFAULT_MEAL_LOG_DAY_DATE);
    }

    @Test
    @Transactional
    public void createMealLogDayWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = mealLogDayRepository.findAll().size();

        // Create the MealLogDay with an existing ID
        mealLogDay.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMealLogDayMockMvc.perform(post("/api/meal-log-days")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mealLogDay)))
            .andExpect(status().isBadRequest());

        // Validate the MealLogDay in the database
        List<MealLogDay> mealLogDayList = mealLogDayRepository.findAll();
        assertThat(mealLogDayList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMealLogDays() throws Exception {
        // Initialize the database
        mealLogDayRepository.saveAndFlush(mealLogDay);

        // Get all the mealLogDayList
        restMealLogDayMockMvc.perform(get("/api/meal-log-days?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mealLogDay.getId().intValue())))
            .andExpect(jsonPath("$.[*].mealLogDayDate").value(hasItem(sameInstant(DEFAULT_MEAL_LOG_DAY_DATE))));
    }

    @Test
    @Transactional
    public void getMealLogDay() throws Exception {
        // Initialize the database
        mealLogDayRepository.saveAndFlush(mealLogDay);

        // Get the mealLogDay
        restMealLogDayMockMvc.perform(get("/api/meal-log-days/{id}", mealLogDay.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(mealLogDay.getId().intValue()))
            .andExpect(jsonPath("$.mealLogDayDate").value(sameInstant(DEFAULT_MEAL_LOG_DAY_DATE)));
    }

    @Test
    @Transactional
    public void getNonExistingMealLogDay() throws Exception {
        // Get the mealLogDay
        restMealLogDayMockMvc.perform(get("/api/meal-log-days/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMealLogDay() throws Exception {
        // Initialize the database
        mealLogDayRepository.saveAndFlush(mealLogDay);
        int databaseSizeBeforeUpdate = mealLogDayRepository.findAll().size();

        // Update the mealLogDay
        MealLogDay updatedMealLogDay = mealLogDayRepository.findOne(mealLogDay.getId());
        // Disconnect from session so that the updates on updatedMealLogDay are not directly saved in db
        em.detach(updatedMealLogDay);
        updatedMealLogDay
            .mealLogDayDate(UPDATED_MEAL_LOG_DAY_DATE);

        restMealLogDayMockMvc.perform(put("/api/meal-log-days")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMealLogDay)))
            .andExpect(status().isOk());

        // Validate the MealLogDay in the database
        List<MealLogDay> mealLogDayList = mealLogDayRepository.findAll();
        assertThat(mealLogDayList).hasSize(databaseSizeBeforeUpdate);
        MealLogDay testMealLogDay = mealLogDayList.get(mealLogDayList.size() - 1);
        assertThat(testMealLogDay.getMealLogDayDate()).isEqualTo(UPDATED_MEAL_LOG_DAY_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingMealLogDay() throws Exception {
        int databaseSizeBeforeUpdate = mealLogDayRepository.findAll().size();

        // Create the MealLogDay

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMealLogDayMockMvc.perform(put("/api/meal-log-days")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mealLogDay)))
            .andExpect(status().isCreated());

        // Validate the MealLogDay in the database
        List<MealLogDay> mealLogDayList = mealLogDayRepository.findAll();
        assertThat(mealLogDayList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMealLogDay() throws Exception {
        // Initialize the database
        mealLogDayRepository.saveAndFlush(mealLogDay);
        int databaseSizeBeforeDelete = mealLogDayRepository.findAll().size();

        // Get the mealLogDay
        restMealLogDayMockMvc.perform(delete("/api/meal-log-days/{id}", mealLogDay.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<MealLogDay> mealLogDayList = mealLogDayRepository.findAll();
        assertThat(mealLogDayList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MealLogDay.class);
        MealLogDay mealLogDay1 = new MealLogDay();
        mealLogDay1.setId(1L);
        MealLogDay mealLogDay2 = new MealLogDay();
        mealLogDay2.setId(mealLogDay1.getId());
        assertThat(mealLogDay1).isEqualTo(mealLogDay2);
        mealLogDay2.setId(2L);
        assertThat(mealLogDay1).isNotEqualTo(mealLogDay2);
        mealLogDay1.setId(null);
        assertThat(mealLogDay1).isNotEqualTo(mealLogDay2);
    }
}
