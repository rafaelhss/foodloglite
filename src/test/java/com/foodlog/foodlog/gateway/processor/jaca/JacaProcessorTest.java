package com.foodlog.foodlog.gateway.processor.jaca;

import com.foodlog.FoodlogbotadmApp;
import com.foodlog.foodlog.gateway.telegram.model.Message;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.gateway.telegram.model.User;
import com.foodlog.foodlog.gateway.telegram.sender.Sender;
import com.foodlog.foodlog.util.TestUtil;
import com.foodlog.repository.JacaRepository;
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
 * Created by rafael on 03/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class JacaProcessorTest {
    public static Integer CHAT_ID_RAFA = 153350155;

    @Autowired
    private JacaProcessor jacaProcessor;

    @Autowired
    private JacaRepository jacaRepository;

    @Autowired
    private TestUtil testUtil;

    @Mock
    private Sender senderMock;

    @Test
    public void process() throws Exception {

        Update update = getUpdate();


        jacaProcessor.setUpdate(update);
        Mockito.doNothing().when(senderMock).sendResponse(null, "");

        jacaProcessor.setSender(senderMock);

        int qtd = (int) jacaRepository.count();

        jacaProcessor.process();

        Assert.assertEquals(qtd + 1, jacaRepository.count());

    }

    @Before
    public void setup() {
        testUtil.createUser();
    }

    @Test
    public void check() throws Exception {
        Update update = getUpdate();


        jacaProcessor.setUpdate(update);
        Mockito.doNothing().when(senderMock).sendResponse(null, "");

        jacaProcessor.setSender(senderMock);

        Assert.assertEquals(true, jacaProcessor.check());


    }

    private Update getUpdate() {
        Update update = new Update();
        update.setUpdate_id(1L);

        Message message = new Message();
        message.setText("jaca");
        User from = new User();
        from.setId(CHAT_ID_RAFA);
        message.setFrom(from);
        update.setMessage(message);
        update.setUpdateDateTime(Instant.now());
        return update;
    }

}
