package com.foodlog.foodlog.gateway.processor.newuser;

import com.foodlog.domain.Authority;
import com.foodlog.domain.User;
import com.foodlog.domain.UserTelegram;
import com.foodlog.foodlog.gateway.processor.Processor;
import com.foodlog.repository.AuthorityRepository;
import com.foodlog.repository.UserRepository;
import com.foodlog.repository.UserTelegramRepository;
import com.foodlog.security.AuthoritiesConstants;
import com.foodlog.service.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by rafael on 06/02/18.
 */
@Service
public class NewUserProcessor extends Processor {

    @Autowired
    private UserTelegramRepository userTelegramRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;


    public User createUser(String firstName, String lastName, String password) {
        User user = new User();
        user.setLogin(firstName + "." + lastName );
        user.setFirstName(firstName);
        user.setLastName(lastName);
        //user.setEmail("teste@teste.com");
        user.setLangKey("en"); // default language

        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authorityRepository.findOne(AuthoritiesConstants.USER));
        user.setAuthorities(authorities);


        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        userRepository.save(user);
        return user;

    }
    @Override
    public void process() {

        String password = String.valueOf(new Random().nextInt() % 10000);
        User user = createUser(update.getMessage().getFrom().getFirst_name(),update.getMessage().getFrom().getLast_name(), password);
        UserTelegram userTelegram = new UserTelegram();
        userTelegram.setTelegramId(update.getMessage().getFrom().getId());
        userTelegram.setUser(user);
        userTelegram.setFirst_name(update.getMessage().getFrom().getFirst_name());
        userTelegram.setLast_name(update.getMessage().getFrom().getLast_name());

        userTelegramRepository.save(userTelegram);

        sendMessage("Oi! Bem vindx ao foodlog");
        sendMessage("Para acessar detalhes das suas informações acesse: http://foodlogbot2adm.herokuapp.com usuario:" + user.getLogin() + " senha:" + password);
        sendMessage("Sempre que comer algo, envie uma foto. Quando se pesar, envie o peso. Envie fotos do seu corpo regularmente para acompanhara evolução. Se chutar o balde algum dia, envie 'jaca'");
        sendMessage("Para mais informações envie 'help' ou 'tutorial'");

    }

    @Override
    public boolean check() {
        try {
            return (null == userTelegramRepository.findOneByTelegramId(update.getMessage().getFrom().getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
