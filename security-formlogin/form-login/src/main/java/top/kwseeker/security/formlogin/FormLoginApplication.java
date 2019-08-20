package top.kwseeker.security.formlogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(
		prePostEnabled = true      //可以使用 @PreAuthorize/@PostAuthorize, 支持 SpEL（Spring Expression Language）
		//,securedEnabled = true      //可以使用 @Secured
		//,jsr250Enabled = true        //可以使用 @RoleAllowed
)   //官方文档说可以放在任意@Configuration类上面，只加在这个类上，可以作用于全局么（如SpringSecurityApiConfig限定的方法呢）？
public class FormLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(FormLoginApplication.class, args);
	}

}
