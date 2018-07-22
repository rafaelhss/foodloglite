package com.foodlog.foodlog.gateway.processor;

import com.foodlog.domain.User;
import com.foodlog.domain.UserTelegram;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.gateway.telegram.config.TelegramConfig;
import com.foodlog.foodlog.gateway.telegram.sender.Sender;
import com.foodlog.repository.UserTelegramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by rafael on 27/10/17.
 */
@Component
public abstract class Processor {
    @Autowired
    private UserTelegramRepository userTelegramRepository;

    protected Update update;

    private Sender sender;

    public abstract void process();

    public abstract boolean check();

    public void setUpdate(Update update) {
        this.update = update;
    }

    protected void sendMessage(String message){
        int user_id = update.getMessage().getFrom().getId();

        try {
            if(sender == null){
                sender = new Sender(TelegramConfig.BOT_ID);
            }
            sender.sendResponse(user_id, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected User getCurrentUser(Update update) {
        final Integer id = update.getMessage().getFrom().getId();
        UserTelegram user = userTelegramRepository.findOneByTelegramId(id);
        return user.getUser();
    }


    public void setSender(Sender sender) {
        this.sender = sender;
    }
}
