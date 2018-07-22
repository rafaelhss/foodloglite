package com.foodlog.foodlog.gateway.service;

import com.foodlog.FoodlogbotadmApp;
import com.foodlog.foodlog.gateway.telegram.model.Message;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.gateway.telegram.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by rafael on 08/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class UpdateServiceTest {
    public static Integer CHAT_ID_RAFA = 153350155;

    @Autowired
    private UpdateService updateService;

    @Test
    public void test() throws Exception {
        Update update = getUpdate();
        Update updateajustado = updateService.adjustTime(update);

        org.junit.Assert.assertEquals(update.getMessage(), updateajustado.getMessage());
        org.junit.Assert.assertNotNull(update.getUpdateDateTime());



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
        //update.setUpdateDateTime(Instant.now());
        return update;
    }
}
