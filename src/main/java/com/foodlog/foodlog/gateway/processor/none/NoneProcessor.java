package com.foodlog.foodlog.gateway.processor.none;

import com.foodlog.foodlog.gateway.processor.Processor;
import com.foodlog.foodlog.security.MyTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by rafael on 27/10/17.
 */
@Component
public class NoneProcessor extends Processor {

    @Autowired
    MyTokenProvider myTokenProvider;

    @Override
    public void process() {
       sendMessage("Não entendi o comando. Envie 'ajuda' ou 'tutorial'");
    }

    @Override
    public boolean check() {
        return false;
    }
}
