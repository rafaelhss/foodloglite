package com.foodlog.foodlog.gateway.processor.evolution;

import com.foodlog.foodlog.gateway.processor.Processor;
import com.foodlog.foodlog.util.Util;
import com.foodlog.repository.UserTelegramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by rafael on 08/02/18.
 */
@Service
public class EvolutionProcessor extends Processor {

    @Autowired
    private Util util;

    @Autowired
    private UserTelegramRepository userTelegramRepository;

    @Override
    public void process() {
        sendMessage("Sua evolution sera gerada...");

        try {
            Integer userTelegram = userTelegramRepository.findOneByUser(getCurrentUser(update)).getTelegramId();
            URL url = new URL("https://foodlogbotimagebatch.herokuapp.com/evolution?userid=" + getCurrentUser(update).getId() + "&usertelegram=" + userTelegram);

            util.performHttpGet(url, getCurrentUser(update).getLogin());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean check() {
        try {
            return update.getMessage().getText().trim().toLowerCase().equals("evolution");
        } catch (Exception e) {
            return false;
        }
    }

    //para testes
    public void setUtil(Util util) {
        this.util = util;
    }
}
