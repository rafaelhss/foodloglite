package com.foodlog.foodlog.gateway.processor.weight;

import com.foodlog.domain.User;
import com.foodlog.domain.Weight;
import com.foodlog.foodlog.gateway.processor.Processor;
import com.foodlog.foodlog.util.Util;
import com.foodlog.repository.UserTelegramRepository;
import com.foodlog.repository.WeightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

/**
 * Created by rafael on 27/10/17.
 */
@Component
public class WeightProcessor extends Processor{

    @Autowired
    WeightRepository weightRepository;

    @Autowired
    private Util util;

    @Autowired
    private UserTelegramRepository userTelegramRepository;

    @Override
    public void process() {
        Weight weight = new Weight();
        Float value = Float.parseFloat(update.getMessage().getText());
        weight.setValue(value);
        weight.setWeightDateTime(update.getUpdateDateTime());
        weight.setUpdateId(update.getUpdate_id());

        User currentUser = getCurrentUser(update);
        weight.setUser(currentUser);

        weightRepository.save(weight);

        sendMessage("Peso (" + value + ") salvo com sucesso.");

        try {
            Integer userTelegram = userTelegramRepository.findOneByUser(getCurrentUser(update)).getTelegramId();
            URL url = new URL("https://foodlogbotimagebatch.herokuapp.com/weight?userid=" + getCurrentUser(update).getId() + "&usertelegram=" + userTelegram);

            util.performHttpGet(url, currentUser.getLogin());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean check() {
        return Util.checkRegex(update, "^[+-]?([0-9]*[.])+[0-9]+$") || Util.checkRegex(update, "^[+-]?([0-9]*[,])+[0-9]+$");
    }

    public void setUtil(Util util) {
        this.util = util;
    }
}
