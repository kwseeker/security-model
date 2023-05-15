# Spring Security

Spring Security是一个框架，它提供身份验证、授权和针对常见攻击的保护。

官方案例 [spring-security-samples](https://github.com/spring-projects/spring-security-samples)



## Maven 依赖关系

Spring Boot 只需要引入 `spring-boot-starter-security`即可使用；

这个包，只是包含了一个 pom 文件，引入了下面依赖：

+ spring-boot-starter

  Spring Security 的自动配置类在这个包下定义。

  相关配置类：

  ```
  org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration,\
  ```

+ spring-aop

+ spring-security-config

+ spring-security-web



## 配置属性