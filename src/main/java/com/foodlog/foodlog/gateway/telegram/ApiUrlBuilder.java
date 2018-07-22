package com.foodlog.foodlog.gateway.telegram;

import com.foodlog.foodlog.gateway.telegram.config.TelegramConfig;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by rafael on 04/06/17.
 */
public class ApiUrlBuilder {

    private static String bot_id = TelegramConfig.BOT_ID;
    private static String base_url = "https://api.telegram.org/bot@@BOTID@@/getFile?file_id=@@FILEID@@";

    private static String base_file_url = "https://api.telegram.org/file/bot@@BOTID@@/@@FILEPATH@@";

    public static URI getGetFile(String file_id) {
        try {
            return new URI(base_url.replace("@@BOTID@@", bot_id).replace("@@FILEID@@", file_id));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URI getBytesUrl(String file_path) {
        try {
            return new URI(base_file_url.replace("@@BOTID@@", bot_id).replace("@@FILEPATH@@", file_path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    //

}
