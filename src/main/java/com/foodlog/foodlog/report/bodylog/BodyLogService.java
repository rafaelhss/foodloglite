package com.foodlog.foodlog.report.bodylog;

import com.foodlog.domain.BodyLog;
import com.foodlog.domain.User;
import com.foodlog.foodlog.report.bodylog.gif.AnimatedGIFWriter;
import com.foodlog.repository.BodyLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

/**
 * Created by rafael on 09/11/17.
 */
@Service
public class BodyLogService {

    @Autowired
    private BodyLogRepository bodyLogRepository;


    public BodyLogImage getBodyGif(User user) {

        try {
            List<BodyLog> logs = bodyLogRepository.findByUserOrderByBodyLogDatetime(user);

            String fileName = user.getId() + "imagem.gif";


            // True for dither. Will use more memory and CPU
            AnimatedGIFWriter writer = new AnimatedGIFWriter(true);
            OutputStream os = new FileOutputStream(fileName);
            // Grab the BufferedImage whatever way you can

            // Use -1 for both logical screen width and height to use the first frame dimension
            writer.prepareForWrite(os, -1, -1);

            for(BodyLog bodyLog : logs) {


                InputStream in = new ByteArrayInputStream(bodyLog.getPhoto());
                BufferedImage frame = ImageIO.read(in);
                DateTimeFormatter formatter =
                    DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                        .withLocale( Locale.UK )
                        .withZone( ZoneId.systemDefault() );
                String text = formatter.format(bodyLog.getBodyLogDatetime());

                frame = insertTextToImage(frame, text);

                writer.writeFrame(os, frame,2500);
                System.out.println("opa");
            }

            //System.out.println("vou mandar:" + fileName);

            // new Sender("380968235:AAGqnrSERR8ABcw-_avcPN2ES3KH5SeZtNM").sendDocument(153350155, fileName);

            return new BodyLogImage(Files.readAllBytes(Paths.get(fileName)));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public BodyLogImage getBodyPanel(User user) {
        try {
            int rows = 2;   //we assume the no. of rows and cols are known and each chunk has equal width and height
            int cols = 2;
            int chunks = rows * cols;

            int chunkWidth, chunkHeight;
            int type;


            //fetching image files
            List<BodyLog> logs = bodyLogRepository.findByUserOrderByBodyLogDatetime(user);
            if(logs.size() < chunks){
                rows = 1;
                cols = logs.size();
                chunks = rows * cols;
            }


            //creating a bufferd image array from image files
            BufferedImage[] buffImages = new BufferedImage[chunks];

            System.out.println("logs size:" + logs.size());

            //A primeira imagem eh a primeira (0 tercos)
            //A proxima esta a 1 terco do fim
            //A proxima esta a 2 tercos do fim
            //a ultima eh a ultima (3 tercos)
            for (int i = 0; i < chunks; i++) {
                float factor = Float.valueOf((Float.valueOf(i)/Float.valueOf(chunks-1)));
                System.out.println("factor: " + factor );
                int index = Math.round(Float.valueOf((float) ((logs.size()-1)*factor)));
                System.out.println("index: " + index);
                BufferedImage img = getBufferedImage(logs.get(index));



                buffImages[i] = insertDate(img, logs.get(0).getBodyLogDatetime(), logs.get(index).getBodyLogDatetime());
            }



            type = buffImages[0].getType();
            chunkWidth = buffImages[0].getWidth();
            chunkHeight = buffImages[0].getHeight();

            //Initializing the final image
            BufferedImage finalImg = new BufferedImage(chunkWidth * cols, chunkHeight * rows, type);

            int num = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    finalImg.createGraphics().drawImage(buffImages[num], chunkWidth * j, chunkHeight * i, null);
                    num++;
                }
            }
            String fileName = user.getId() + "image.jpg";

            System.out.println("Image concatenated: " + fileName);

            ImageIO.write(finalImg, "jpeg", new File(fileName));
            return new BodyLogImage(Files.readAllBytes(Paths.get(fileName)));


        } catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }
    private BufferedImage insertDate(BufferedImage img, Instant baseDate, Instant photoDate) {

        String text = "";
        LocalDate base = baseDate.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate photo = photoDate.atZone(ZoneId.systemDefault()).toLocalDate();

        Period p = Period.between(base, photo);

        if(p.getYears() > 0){
            if(p.getYears() > 1){
                text += p.getYears() + " anos ";
            } else {
                text += p.getYears() + " ano ";
            }
        }
        if(p.getMonths() > 0){
            if(p.getMonths() > 1){
                text += p.getMonths() + " meses ";
            } else {
                text += p.getMonths() + " mes ";
            }
        }
        if(p.getDays() > 0){
            if(p.getDays() > 1){
                text += p.getDays() + " dias ";
            } else {
                text += p.getDays() + " dia ";
            }
        }
        System.out.println("You are " + text);

        return insertTextToImage(img, text);
    }


    private BufferedImage getBufferedImage(BodyLog log) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(log.getPhoto()));
    }

    private BufferedImage insertTextToImage(BufferedImage frame, String text) {
        Graphics2D g2d = frame.createGraphics();

        g2d.setFont(new Font("Arial", Font.PLAIN, 60));
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2d.setColor(Color.BLACK);
        g2d.setBackground(Color.white);

        //g2d.drawString(text, 0, fm.getAscent());


        g2d.drawString(text, 0, g2d.getFontMetrics().getHeight());

        g2d.dispose();
        return frame;
    }
}
