package com.foodlog.foodlog.gateway.processor.textlog;

import com.foodlog.FoodlogbotadmApp;
import com.foodlog.foodlog.gateway.telegram.model.Message;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.gateway.telegram.model.User;
import com.foodlog.foodlog.gateway.telegram.sender.Sender;
import com.foodlog.foodlog.util.TestUtil;
import com.foodlog.repository.MealLogRepository;
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
public class TextLogProcessorTest {
    public static Integer CHAT_ID_RAFA = 153350155;

    @Autowired
    TextLogProcessor textLogProcessor;

    @Mock
    Sender senderMock;

    @Autowired
    MealLogRepository mealLogRepository;

    @Autowired
    TestUtil testUtil;



    @Before
    public void setup() {
        testUtil.createUser();
    }

    @Test
    public void process() throws Exception {
        long before = mealLogRepository.count();

        Update update = getUpdate();


        textLogProcessor.setUpdate(update);
        Mockito.doNothing().when(senderMock).sendResponse(null, "");
        textLogProcessor.setSender(senderMock);

        textLogProcessor.process();

        Assert.assertEquals(before+1, mealLogRepository.count());


    }


    @Test
    public void check() throws Exception {
        Update update = getUpdate();


        textLogProcessor.setUpdate(update);
        Mockito.doNothing().when(senderMock).sendResponse(null, "");
        textLogProcessor.setSender(senderMock);

        Assert.assertEquals(true, textLogProcessor.check());


    }

    private Update getUpdate() {
        Update update = new Update();
        update.setUpdate_id(1L);

        Message message = new Message();
        message.setText("Meal: Mistura, linguiça mocoto carne de lata");
        User from = new User();
        from.setId(CHAT_ID_RAFA);
        message.setFrom(from);
        update.setMessage(message);
        update.setUpdateDateTime(Instant.now());
        return update;
    }
}
