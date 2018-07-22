package com.foodlog.foodlog.gateway.processor.activity;

import com.foodlog.FoodlogbotadmApp;
import com.foodlog.foodlog.gateway.telegram.model.Message;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.gateway.telegram.model.User;
import com.foodlog.foodlog.gateway.telegram.sender.Sender;
import com.foodlog.foodlog.util.TestUtil;
import com.foodlog.repository.ActivityRepository;
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

import static org.junit.Assert.*;

/**
 * Created by rafael on 17/01/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class ActivityProcessorTest {

    public static Integer CHAT_ID_RAFA = 153350155;

    @Autowired
    private ActivityProcessor activityProcessor;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TestUtil testUtil;

    @Mock
    private Sender senderMock;

    @Before
    public void setup() {
        testUtil.createUser();
    }

    @Test
    public void process() throws Exception {
        Update update = getUpdate("activity jiu 5");


        activityProcessor.setUpdate(update);
        Mockito.doNothing().when(senderMock).sendResponse(null, "");

        activityProcessor.setSender(senderMock);

        int qtd = (int) activityRepository.count();

        activityProcessor.process();

        Assert.assertEquals(qtd + 1, activityRepository.count());
    }

    @Test
    public void processExisting() throws Exception {
        Update update = getUpdate("jiu 5");


        activityProcessor.setUpdate(update);
        Mockito.doNothing().when(senderMock).sendResponse(null, "");

        activityProcessor.setSender(senderMock);

        int qtd = (int) activityRepository.count();

        activityProcessor.process();

        Assert.assertEquals(qtd + 1, activityRepository.count());
    }

    @Test
    public void check() throws Exception {
        Update update = getUpdate("activity jiu 5");


        activityProcessor.setUpdate(update);
        Mockito.doNothing().when(senderMock).sendResponse(null, "");

        activityProcessor.setSender(senderMock);

        Assert.assertEquals(true, activityProcessor.check());

    }

    @Test
    public void checkNoKeyword() throws Exception {
        Update update = getUpdate("jiu 5");


        activityProcessor.setUpdate(update);
        Mockito.doNothing().when(senderMock).sendResponse(null, "");

        activityProcessor.setSender(senderMock);

        Assert.assertEquals(true, activityProcessor.check());

    }


    private Update getUpdate(String text) {
        Update update = new Update();
        update.setUpdate_id(1L);

        Message message = new Message();
        message.setText(text);
        User from = new User();
        from.setId(CHAT_ID_RAFA);
        message.setFrom(from);
        update.setMessage(message);
        update.setUpdateDateTime(Instant.now());
        return update;
    }

}
