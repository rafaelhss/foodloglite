package com.foodlog.foodlog.gateway.processor.bodylog;

import com.foodlog.FoodlogbotadmApp;
import com.foodlog.foodlog.gateway.telegram.model.Message;
import com.foodlog.foodlog.gateway.telegram.model.Photo;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.gateway.telegram.model.User;
import com.foodlog.foodlog.gateway.telegram.sender.Sender;
import com.foodlog.foodlog.util.TestUtil;
import com.foodlog.foodlog.util.Util;
import com.foodlog.repository.BodyLogRepository;
import com.foodlog.repository.MealLogRepository;
import com.foodlog.repository.UserTelegramRepository;
import com.google.common.io.ByteStreams;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rafael on 08/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BodyLogProcessorTest {

    @Autowired
    private TestUtil testUtil;

    @Mock
    private Sender senderMock;

    @Autowired
    private BodyLogProcessor bodyLogProcessor;

    @Autowired
    private MealLogRepository mealLogRepository;

    @Autowired
    private BodyLogRepository bodyLogRepository;

    private Long UPDATE_ID = new Random().nextLong();

    public static Integer CHAT_ID_RAFA = 153350155;

    @Spy
    private Util utilMock;

    @Autowired
    private UserTelegramRepository userTelegramRepository;

    @Test
    public void processBody() throws Exception {

        long qtdMeal = mealLogRepository.count();
        long qtdBody = bodyLogRepository.count();

        Update update = getUpdate();

        InputStream imagem = this.getClass().getResourceAsStream("/images/body.jpg");

        BodyLogFactory bodyLogFactory = Mockito.mock(BodyLogFactory.class);
        Mockito.when(bodyLogFactory.getPicture(update)).thenReturn(ByteStreams.toByteArray(imagem));

        bodyLogProcessor.setBodyLogFactory(bodyLogFactory);
        bodyLogProcessor.setUpdate(update);

        Mockito.doNothing().when(senderMock).sendResponse(null, "");
        bodyLogProcessor.setSender(senderMock);

        Mockito.doNothing().when(utilMock).performHttpGet(Mockito.any(),Mockito.any());
        bodyLogProcessor.setUtil(utilMock);


        bodyLogProcessor.process();


        Assert.assertEquals(qtdBody + 1, bodyLogRepository.count());
        Assert.assertEquals(qtdMeal, mealLogRepository.count());

        Integer userTelegram = userTelegramRepository.findOneByUser(testUtil.getUser()).getTelegramId();
        URL url = new URL("https://foodlogbotimagebatch.herokuapp.com/bodylog?userid=" + testUtil.getUser().getId() + "&usertelegram=" + userTelegram);

        Mockito.verify(utilMock, Mockito.times(1)).performHttpGet(Mockito.eq(url), Mockito.eq(testUtil.getUser().getLogin()));

    }

    @Test
    public void processMeal() throws Exception {

        long qtdMeal = mealLogRepository.count();
        long qtdBody = bodyLogRepository.count();

        Update update = getUpdate();

        InputStream imagem = this.getClass().getResourceAsStream("/images/meal.jpg");

        BodyLogFactory bodyLogFactory = Mockito.mock(BodyLogFactory.class);
        Mockito.when(bodyLogFactory.getPicture(update)).thenReturn(ByteStreams.toByteArray(imagem));

        bodyLogProcessor.setBodyLogFactory(bodyLogFactory);
        bodyLogProcessor.setUpdate(update);

        Mockito.doNothing().when(senderMock).sendResponse(null, "");
        bodyLogProcessor.setSender(senderMock);


        bodyLogProcessor.process();


        Assert.assertEquals(qtdBody, bodyLogRepository.count());
        Assert.assertEquals(qtdMeal, mealLogRepository.count());

    }

    @Before
    public void setup(){
        testUtil.createUser();
    }

    @After
    public void clear(){

    }



    @Test
    public void checkTrue() throws Exception {

        Update update = getUpdate();

        InputStream imagem = this.getClass().getResourceAsStream("/images/body.jpg");

        BodyLogFactory bodyLogFactory = Mockito.mock(BodyLogFactory.class);
        Mockito.when(bodyLogFactory.getPicture(update)).thenReturn(ByteStreams.toByteArray(imagem));

        bodyLogProcessor.setBodyLogFactory(bodyLogFactory);
        bodyLogProcessor.setUpdate(update);

        Assert.assertEquals(true, bodyLogProcessor.check());
    }

    @Test
    public void checkFalse() throws Exception {

        Update update = getUpdate();

        InputStream imagem = this.getClass().getResourceAsStream("/images/meal.jpg");

        BodyLogFactory bodyLogFactory = Mockito.mock(BodyLogFactory.class);
        Mockito.when(bodyLogFactory.getPicture(update)).thenReturn(ByteStreams.toByteArray(imagem));

        bodyLogProcessor.setBodyLogFactory(bodyLogFactory);
        bodyLogProcessor.setUpdate(update);

        Assert.assertEquals(false, bodyLogProcessor.check());
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
