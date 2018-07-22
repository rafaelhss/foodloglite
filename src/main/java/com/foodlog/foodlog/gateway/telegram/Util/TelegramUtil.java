package com.foodlog.foodlog.gateway.telegram.Util;

import com.foodlog.foodlog.gateway.telegram.ApiUrlBuilder;
import com.foodlog.foodlog.gateway.telegram.model.GetFile;
import com.foodlog.foodlog.gateway.telegram.model.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Created by rafael on 08/11/17.
 */
@Service
public class TelegramUtil {
    public byte[] getPicture(Update update) {
        int id = update.getMessage().getPhoto().size() -1 ;
        String file_id = update.getMessage().getPhoto().get(id).getFile_id();

        RestTemplate restTemplate = new RestTemplate();
        URI getFileurl = ApiUrlBuilder.getGetFile(file_id);
        GetFile getFile = (GetFile) restTemplate.getForObject(getFileurl, GetFile.class);

        //https://api.telegram.org/file/bot<token>/<file_path>

        String file_path = getFile.getResult().getFile_path();
        URI getBytesurl = ApiUrlBuilder.getBytesUrl(file_path);
        return restTemplate.getForObject(getBytesurl, byte[].class);
    }
}
