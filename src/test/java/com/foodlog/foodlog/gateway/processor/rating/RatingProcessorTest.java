package com.foodlog.foodlog.gateway.processor.rating;

import com.foodlog.FoodlogbotadmApp;
import com.foodlog.domain.BodyLog;
import com.foodlog.domain.MealLog;
import com.foodlog.domain.Weight;
import com.foodlog.foodlog.gateway.telegram.model.Message;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.gateway.telegram.model.User;
import com.foodlog.foodlog.gateway.telegram.sender.Sender;
import com.foodlog.foodlog.util.TestUtil;
import com.foodlog.repository.BodyLogRepository;
import com.foodlog.repository.MealLogRepository;
import com.foodlog.repository.WeightRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

/**
 * Created by rafael on 07/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class RatingProcessorTest {
    public static Integer CHAT_ID_RAFA = 153350155;


    @Autowired
    RatingProcessor ratingProcessor;


    @Autowired
    TestUtil testUtil;

    @Mock
    Sender senderMock;

    private Long UPDATE_ID = 123456789L;

    @Autowired
    MealLogRepository mealLogRepository;
    @Autowired
    BodyLogRepository bodyLogRepository;
    @Autowired
    WeightRepository weightRepository;


    @Before
    public void setup() {
        testUtil.createUser();

    }

    private MealLog buildMealLog(Instant time) {
        MealLog mealLog = new MealLog();
        mealLog.setRating(3);
        mealLog.setUpdateId(UPDATE_ID);
        mealLog.setUser(testUtil.getUser());
        mealLog.setMealDateTime(time);
        mealLog.setPhoto(new byte[] {
            (byte)0xe3, 0x00, //APDU CHOICE Type(AareApdu)
            0x00, 0x2c, //CHOICE.length = 44
            0x00, 0x00, //result=accept
            0x50, 0x79, //data-proto-id = 20601
            0x00, 0x26,
            (byte)0x80 });
        mealLog.setPhotoContentType("fake");

        return  mealLog;
    }

    private BodyLog buildBodyLog(Instant time){
        BodyLog bodyLog = new BodyLog();
        bodyLog.setPhotoContentType("fake");
        bodyLog.setPhoto(new byte[] {
            (byte)0xe3, 0x00, //APDU CHOICE Type(AareApdu)
            0x00, 0x2c, //CHOICE.length = 44
            0x00, 0x00, //result=accept
            0x50, 0x79, //data-proto-id = 20601
            0x00, 0x26,
            (byte)0x80 });
        bodyLog.setUser(testUtil.getUser());
        bodyLog.setBodyLogDatetime(time);
        bodyLog.setUpdateId(UPDATE_ID);
        return bodyLog;
    }

    private Weight buildWeight(Instant time){
        Weight weight = new Weight();
        weight.setUpdateId(UPDATE_ID);
        weight.setUser(testUtil.getUser());
        weight.setValue(100F);
        weight.setWeightDateTime(time);

        return weight;

    }


    @Test
    public void process() throws Exception {
        mealLogRepository.save(buildMealLog(Instant.now()));

        Update update = getUpdate();
        ratingProcessor.setUpdate(update);

        Mockito.doNothing().when(senderMock).sendResponse(null, "");
        ratingProcessor.setSender(senderMock);

        ratingProcessor.process();

        MealLog ultimo = mealLogRepository.findTop1ByUserOrderByMealDateTimeDesc(testUtil.getUser());

        Integer expected = 5;
        Assert.assertEquals(expected, ultimo.getRating());

    }

    @Test
    public void checkMealLogFoiOUltimo() throws Exception {
        mealLogRepository.save(buildMealLog(Instant.now()));
        bodyLogRepository.save(buildBodyLog(Instant.now().minusSeconds(1000)));
        weightRepository.save(buildWeight(Instant.now().minusSeconds(2000)));

        Update update = getUpdate();
        ratingProcessor.setUpdate(update);

        Mockito.doNothing().when(senderMock).sendResponse(null, "");
        ratingProcessor.setSender(senderMock);

        Assert.assertEquals(true, ratingProcessor.check());

    }
    @Test
    public void checkMealLogNaoFoiOUltimo() throws Exception {
        mealLogRepository.save(buildMealLog(Instant.now().minusSeconds(1000)));
        bodyLogRepository.save(buildBodyLog(Instant.now()));
        weightRepository.save(buildWeight(Instant.now()));

        Update update = getUpdate();
        ratingProcessor.setUpdate(update);

        Mockito.doNothing().when(senderMock).sendResponse(null, "");
        ratingProcessor.setSender(senderMock);

        Assert.assertEquals(false, ratingProcessor.check());

    }

    private Update getUpdate() {
        Update update = new Update();
        update.setUpdate_id(1L);

        Message message = new Message();
        message.setText("5");
        User from = new User();
        from.setId(CHAT_ID_RAFA);
        message.setFrom(from);
        update.setMessage(message);
        update.setUpdateDateTime(Instant.now());
        return update;
    }
}
