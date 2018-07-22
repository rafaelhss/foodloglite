package com.foodlog.foodlog.gateway.processor.photo;

import com.foodlog.FoodlogbotadmApp;
import com.foodlog.domain.MealLog;
import com.foodlog.domain.ScheduledMeal;
import com.foodlog.foodlog.gateway.telegram.model.Message;
import com.foodlog.foodlog.gateway.telegram.model.Photo;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.gateway.telegram.model.User;
import com.foodlog.foodlog.gateway.telegram.sender.Sender;
import com.foodlog.foodlog.util.TestUtil;
import com.foodlog.repository.MealLogRepository;
import com.foodlog.repository.ScheduledMealRepository;
import com.google.common.io.ByteStreams;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rafael on 03/11/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class PhotoProcessorTest {
    public static final long UPDATE_ID = new Random().nextLong();
    public static Integer CHAT_ID_RAFA = 153350155;

    @Autowired
    private PhotoProcessor photoProcessor;

    @Autowired
    private ScheduledMealRepository scheduledMealRepository;

    @Autowired
    private MealLogRepository mealLogRepository;

    @Autowired
    private TestUtil testUtil;

    @Spy
    private MealLogFactory mealLogFactory;


    private ScheduledMeal scheduledMeal;

    @Mock
    private Sender senderMock;

    @Test
    public void process() throws Exception {

        Update update = getUpdate();

        InputStream imagem = this.getClass().getResourceAsStream("/images/meal.jpg");

        Mockito.doReturn(ByteStreams.toByteArray(imagem)).when(mealLogFactory).getPicture(update);

        mealLogFactory.setScheduledMealRepository(scheduledMealRepository);
        photoProcessor.setMealLogFactory(mealLogFactory);
        photoProcessor.setUpdate(update);

        Mockito.doNothing().when(senderMock).sendResponse(null, "");

        photoProcessor.setSender(senderMock);


        MealLog mealLog = buildMealLog(imagem);

        photoProcessor.process();





        boolean found = false;
        for(MealLog saved :mealLogRepository.findAll()){
            if(saved.getUpdateId() == UPDATE_ID){
                found = true;
                Assert.assertNotNull(saved.getScheduledMeal());
                Assert.assertEquals(mealLog.getScheduledMeal().getName(), saved.getScheduledMeal().getName());
                Assert.assertEquals(mealLog.getUpdateId(), saved.getUpdateId());
                Assert.assertEquals(mealLog.getRating(), saved.getRating());
                Assert.assertEquals(mealLog.getUser(), saved.getUser());
            }
        }

        Assert.assertEquals(true, found);



    }

    private MealLog buildMealLog(InputStream imagem) {
        MealLog mealLog = new MealLog();
        mealLog.setRating(3);
        mealLog.setScheduledMeal(scheduledMeal);
        mealLog.setUpdateId(UPDATE_ID);
        mealLog.setUser(testUtil.getUser());

        return  mealLog;
    }

    @Before
    public void setup(){
        testUtil.createUser();

        scheduledMeal = testUtil.createScheduledMeal(ZonedDateTime.now(), "Scheduled Meal @TIME@ Test ");
    }



    @Test
    public void checkFalse() throws Exception {

        Update update = getUpdate();

        InputStream imagem = this.getClass().getResourceAsStream("/images/body.jpg");

        MealLogFactory mealLogFactory = Mockito.mock(MealLogFactory.class);
        Mockito.when(mealLogFactory.getPicture(update)).thenReturn(ByteStreams.toByteArray(imagem));

        photoProcessor.setMealLogFactory(mealLogFactory);
        photoProcessor.setUpdate(update);

        Assert.assertEquals(false, photoProcessor.check());


    }
    @Test
    public void checkTrue() throws Exception {

        Update update = getUpdate();

        InputStream imagem = this.getClass().getResourceAsStream("/images/meal.jpg");

        MealLogFactory mealLogFactory = Mockito.mock(MealLogFactory.class);
        Mockito.when(mealLogFactory.getPicture(update)).thenReturn(ByteStreams.toByteArray(imagem));

        photoProcessor.setMealLogFactory(mealLogFactory);
        photoProcessor.setUpdate(update);

        Assert.assertEquals(true, photoProcessor.check());
    }

    private Update getUpdate() {
        Update update = new Update();
        update.setUpdate_id(UPDATE_ID);

        Message message = new Message();

        List<Photo> photos = new ArrayList<>();
        Photo photo = new Photo();
        photo.setFile_id("1");


        photos.add(photo);
        message.setPhoto(photos);

        User from = new User();
        from.setId(CHAT_ID_RAFA);
        message.setFrom(from);
        update.setMessage(message);
        update.setUpdateDateTime(Instant.now());
        return update;
    }

}
