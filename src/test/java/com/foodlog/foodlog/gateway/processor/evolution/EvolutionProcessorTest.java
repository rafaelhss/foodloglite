package com.foodlog.foodlog.gateway.processor.evolution;

import com.foodlog.FoodlogbotadmApp;
import com.foodlog.foodlog.gateway.telegram.model.Message;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.gateway.telegram.model.User;
import com.foodlog.foodlog.gateway.telegram.sender.Sender;
import com.foodlog.foodlog.security.MyTokenProvider;
import com.foodlog.foodlog.util.TestUtil;
import com.foodlog.foodlog.util.Util;
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

import java.time.Instant;

/**
 * Created by rafael on 07/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class EvolutionProcessorTest {
    public static Integer CHAT_ID_RAFA = 153350155;

    @Autowired
    EvolutionProcessor evolutionProcessor;

    @Mock
    Sender senderMock;

    @Spy
    Util utilMock;

    @Autowired
    MyTokenProvider myTokenProvider;


    @Autowired
    TestUtil testUtil;

    @Before
    public void setup() {
        testUtil.createUser();
    }

    @Test
    public void process() throws Exception {

        Mockito.doNothing().when(senderMock).sendResponse(Mockito.any(), Mockito.any());
        evolutionProcessor.setSender(senderMock);

        Mockito.doNothing().when(utilMock).call(Mockito.any());
        utilMock.setMyTokenProvider(myTokenProvider);
        evolutionProcessor.setUtil(utilMock);

        evolutionProcessor.setUpdate(getUpdate());

        evolutionProcessor.process();

        Mockito.verify(senderMock, Mockito.times(1)).sendResponse(Mockito.eq(153350155), Mockito.any());

        Mockito.verify(utilMock, Mockito.times(1)).performHttpGet(Mockito.any(), Mockito.eq(testUtil.getUser().getLogin()));

    }

    @Test
    public void check() throws Exception {
        Update update = getUpdate();


        evolutionProcessor.setUpdate(update);
        Mockito.doNothing().when(senderMock).sendResponse(null, "");
        evolutionProcessor.setSender(senderMock);

        Assert.assertEquals(true, evolutionProcessor.check());


    }

    private Update getUpdate() {
        Update update = new Update();
        update.setUpdate_id(1L);

        Message message = new Message();
        message.setText("evolution");
        User from = new User();
        from.setId(CHAT_ID_RAFA);
        message.setFrom(from);
        update.setMessage(message);
        update.setUpdateDateTime(Instant.now());
        return update;
    }

}
