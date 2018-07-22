package com.foodlog.foodlog.security;

import com.foodlog.security.AuthoritiesConstants;
import com.foodlog.security.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rafael on 08/11/17.
 */
@Service
public class MyTokenProvider {

    @Autowired
    TokenProvider tokenProvider;

    public String createToken(String user){
        Authentication auth = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                ArrayList<GrantedAuthority> result = new ArrayList<>();
                result.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
                return result;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return user;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean b) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return getPrincipal().toString();
            }
        };
        return tokenProvider.createToken(auth,true);
    }
}
