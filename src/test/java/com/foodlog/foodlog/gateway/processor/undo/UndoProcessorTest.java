package com.foodlog.foodlog.gateway.processor.undo;

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
import java.util.Random;

/**
 * Created by rafael on 07/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class UndoProcessorTest {

    private static final Long UPDATE_ID = new Random().nextLong();
    public static Integer CHAT_ID_RAFA = 153350155;


    @Autowired
    UndoProcessor undoProcessor;

    @Autowired
    TestUtil testUtil;

    @Mock
    Sender senderMock;

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


    @Test
    public void processMealogFoiUltimo() throws Exception {

        mealLogRepository.save(buildMealLog(Instant.now().plusSeconds(10000)));
        bodyLogRepository.save(buildBodyLog(Instant.now().plusSeconds(1000)));
        weightRepository.save(buildWeight(Instant.now().plusSeconds(2000)));

        long mealLogCount = mealLogRepository.count();
        long bodyLogCount = bodyLogRepository.count();
        long weightCount = weightRepository.count();



        undoProcessor.setUpdate(getUpdate());

        Mockito.doNothing().when(senderMock).sendResponse(null, "");
        undoProcessor.setSender(senderMock);


        undoProcessor.process();

        Assert.assertEquals(weightCount, weightRepository.count());
        Assert.assertEquals(bodyLogCount, bodyLogRepository.count());
        Assert.assertEquals(mealLogCount-1, mealLogRepository.count());
    }

    @Test
    public void processMealogNAOFoiUltimo() throws Exception {

        mealLogRepository.save(buildMealLog(Instant.now().minusSeconds(3000)));
        bodyLogRepository.save(buildBodyLog(Instant.now().plusSeconds(10000)));
        weightRepository.save(buildWeight(Instant.now().plusSeconds(2000)));

        long mealLogCount = mealLogRepository.count();
        long bodyLogCount = bodyLogRepository.count();
        long weightCount = weightRepository.count();



        undoProcessor.setUpdate(getUpdate());

        Mockito.doNothing().when(senderMock).sendResponse(null, "");
        undoProcessor.setSender(senderMock);


        undoProcessor.process();

        Assert.assertEquals(weightCount, weightRepository.count());

        long somaEsperada = bodyLogCount + weightCount - 1;
        long somaActual = bodyLogRepository.count() + weightRepository.count();


        Assert.assertEquals(somaEsperada, somaActual );
        Assert.assertEquals(mealLogCount, mealLogRepository.count());
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
    public void check() throws Exception {
        Update update = getUpdate();


        undoProcessor.setUpdate(update);
        Mockito.doNothing().when(senderMock).sendResponse(null, "");
        undoProcessor.setSender(senderMock);

        Assert.assertEquals(true, undoProcessor.check());


    }

    private Update getUpdate() {
        Update update = new Update();
        update.setUpdate_id(1L);

        Message message = new Message();
        message.setText("undo");
        User from = new User();
        from.setId(CHAT_ID_RAFA);
        message.setFrom(from);
        update.setMessage(message);
        update.setUpdateDateTime(Instant.now());
        return update;
    }

}
