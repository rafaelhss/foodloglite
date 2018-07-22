package com.foodlog.foodlog.gateway.processor.prox;

import com.foodlog.FoodlogbotadmApp;
import com.foodlog.domain.ScheduledMeal;
import com.foodlog.foodlog.gateway.telegram.model.Message;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.gateway.telegram.model.User;
import com.foodlog.foodlog.gateway.telegram.sender.Sender;
import com.foodlog.foodlog.util.TestUtil;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by rafael on 07/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class ProxProcessorTest {
    public static Integer CHAT_ID_RAFA = 153350155;

    @Autowired
    ProxProcessor proxProcessor;

    @Autowired
    TestUtil testUtil;

    @Mock
    Sender senderMock;

    private ScheduledMeal ProxScheduledMeal;

    @Before
    public void setup() {
        testUtil.createUser();
    }

    @Test
    public void process() throws Exception {
        Update update = getUpdate();


        proxProcessor.setUpdate(update);


        Mockito.doNothing().when(senderMock).sendResponse(null, "");

        proxProcessor.setSender(senderMock);

        //cria varias. tem que mostrar a proxima
        testUtil.createScheduledMeal(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusSeconds(10 * 60), "mais 10 min");
        testUtil.createScheduledMeal(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusSeconds(20 * 60), "mais 20 min");
        testUtil.createScheduledMeal(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusSeconds(30 * 60), "mais 30 min");
        testUtil.createScheduledMeal(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).minusSeconds(10 * 60),"menos 10 min");
        testUtil.createScheduledMeal(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).minusSeconds(20 * 60),"menos 20 min");
        testUtil.createScheduledMeal(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).minusSeconds(30 * 60), "menos 30 min");
        ProxScheduledMeal = testUtil.createScheduledMeal(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusSeconds(1 * 60), "Essa!");

        proxProcessor.process();

        Mockito.verify(senderMock, Mockito.times(1)).sendResponse(Mockito.eq(153350155), Mockito.contains(ProxScheduledMeal.getName()));



    }

    @Test
    public void check() throws Exception {
        Update update = getUpdate();


        proxProcessor.setUpdate(update);
        Mockito.doNothing().when(senderMock).sendResponse(null, "");

        proxProcessor.setSender(senderMock);

        Assert.assertEquals(true, proxProcessor.check());


    }

    private Update getUpdate() {
        Update update = new Update();
        update.setUpdate_id(1L);

        Message message = new Message();
        message.setText("Prox");
        User from = new User();
        from.setId(CHAT_ID_RAFA);
        message.setFrom(from);
        update.setMessage(message);
        update.setUpdateDateTime(Instant.now());
        return update;
    }
}
