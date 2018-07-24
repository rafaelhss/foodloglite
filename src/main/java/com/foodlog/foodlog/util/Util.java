package com.foodlog.foodlog.util;

import com.foodlog.foodlog.gateway.telegram.model.Update;
import com.foodlog.foodlog.security.MyTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rafael on 01/11/17.
 */
@Service
public class Util {
    private static final int MAX_LINE_LENGHT = 50;

    @Autowired
    private MyTokenProvider myTokenProvider;

    public static boolean checkRegex(Update update, String regex){
        // Create a Pattern object
        Pattern r = Pattern.compile(regex);

        // Now create matcher object.
        if(update.getMessage().getText() != null) {
            Matcher m = r.matcher(update.getMessage().getText());
            return m.find();
        }
        if(update.getMessage().getCaption() != null) {
            Matcher m = r.matcher(update.getMessage().getCaption());
            return m.find();
        }
        return false;
    }

    public byte[] convertTextToGraphic(String text, Font font) {

        int max = MAX_LINE_LENGHT;
        if(text.length() < MAX_LINE_LENGHT)
            max = text.length();
        String finaltext = text.substring(0, max);
        text = text.substring(max);
        while(text != null && !text.trim().equals("")){
            finaltext += "\n" + text;

            if(text.length() < MAX_LINE_LENGHT)
                max = text.length();
            text = text.substring(max);
        }


        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(finaltext);
        if(finaltext.length() > MAX_LINE_LENGHT){
            width = fm.stringWidth(finaltext.split("\n")[0]);
        }
        int height = fm.getHeight()*5;
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        //g2d.drawString(text, 0, fm.getAscent());

        int y = 0;
        for (String line : finaltext.split("\n"))
            g2d.drawString(line, 0, y += g2d.getFontMetrics().getHeight());

        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", baos );
            baos.flush();
            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }


    private void performHttpGet(URL url){
        performHttpGet(url, "");
    }

    public void performHttpGet(URL url, String user){
        try {
            if(user != null && !user.equals("")){
                String token = myTokenProvider.createToken(user);
                if(url.toString().indexOf("?") > 0) {
                    url = new URL(url.toString().replace("?", "?auth-token=" + token + "&"));
                } else {
                    url = new URL(url.toString() + "?auth-token=" + token);
                }
            }
            System.out.println(url);
            call(url);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void call(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }

    public Instant getInstant4AM(Instant refDate) {
        return refDate
                .truncatedTo(ChronoUnit.DAYS)
                .plus(4, ChronoUnit.HOURS)
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toInstant();
    }

    //tests
    public void setMyTokenProvider(MyTokenProvider myTokenProvider) {
        this.myTokenProvider = myTokenProvider;
    }
}
