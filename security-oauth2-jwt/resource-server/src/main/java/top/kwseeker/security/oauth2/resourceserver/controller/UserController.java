package top.kwseeker.security.oauth2.resourceserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {

    @GetMapping("/user")
    public Authentication getUser(Authentication authentication) {
        log.info("resource-server: user {}", authentication);
        return authentication;
    }
}
