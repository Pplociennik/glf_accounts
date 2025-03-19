package com.goaleaf.accounts.service.impl;

import com.goaleaf.accounts.service.AuthenticationService;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * TODO: Describe this class.
 *
 * @author Created by: Pplociennik at 19.03.2025 19:03
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public boolean registerUserAccount() {




        WebClient client = WebClient.create("http://localhost:8080/accounts/register");
    }


}
