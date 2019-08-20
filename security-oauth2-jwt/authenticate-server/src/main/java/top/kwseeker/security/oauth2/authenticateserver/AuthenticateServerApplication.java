package top.kwseeker.security.oauth2.authenticateserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@EnableAuthorizationServer  //指定此应用是 OAuth2 的授权服务器
@SpringBootApplication
public class AuthenticateServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticateServerApplication.class, args);
    }

}
