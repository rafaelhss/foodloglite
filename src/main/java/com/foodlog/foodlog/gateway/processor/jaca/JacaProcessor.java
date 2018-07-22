package com.foodlog.foodlog.gateway.processor.jaca;

import com.foodlog.domain.Jaca;
import com.foodlog.foodlog.gateway.processor.Processor;
import com.foodlog.repository.JacaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by rafael on 27/10/17.
 */
@Component
public class JacaProcessor extends Processor{

    @Autowired
    private JacaRepository jacaRepository;


    @Override
    public void process() {

        System.out.println("Process " + this.getClass().getName());
        Jaca jaca = new Jaca();
        jaca.setJacaDateTime(update.getUpdateDateTime());
        jaca.setUser(getCurrentUser(update));

        jacaRepository.save(jaca);
        sendMessage("Jaca! A vida eh um trem bala! Aproveite. Amanha a gente volta com tudo!");
    }

    @Override
    public boolean check() {
        try{
            return this.update.getMessage().getText().trim().toLowerCase().equals("jaca");
        } catch (Exception e) {
            return false;
        }
    }
}
