package com.foodlog.foodlog.gateway.processor.bodylog;

import com.foodlog.domain.BodyLog;
import com.foodlog.domain.User;
import com.foodlog.foodlog.gateway.telegram.Util.TelegramUtil;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.gateway.openCV.PeopleDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;

/**
 * Created by rafael on 08/11/17.
 */
@Service
public class BodyLogFactory {

    @Autowired
    private TelegramUtil telegramUtil;

    @Autowired
    private PeopleDetector peopleDetector;

    public BodyLog createBodyLog(Update update, User user){

        byte[] photo = getPicture(update);
        byte[] imagePeopleBytes = peopleDetector.getPeopleInPhoto(photo);

        BodyLog bodyLog = new BodyLog();
        //byte[] imageBytes = new MealLogFactory().getPicture(update);
        bodyLog.setPhoto(DatatypeConverter.parseBase64Binary(DatatypeConverter.printBase64Binary(imagePeopleBytes)));
        bodyLog.setPhotoContentType("image/jpg");
        bodyLog.setBodyLogDatetime(update.getUpdateDateTime());
        bodyLog.setUser(user);
        bodyLog.setUpdateId(update.getUpdate_id());

        return bodyLog;
    }

    public byte[] getPicture(Update update) {
        return telegramUtil.getPicture(update);
    }
}
