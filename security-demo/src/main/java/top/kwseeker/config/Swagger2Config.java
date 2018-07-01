package top.kwseeker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("top.kwseeker.controller")) // 指定controller存放的目录路径，没有这个限制的的话会默认为应用中所有的API生成文档
                .paths(PathSelectors.any())     //过滤器，用于限制对那些API生成文档
                .build();
    }

    private ApiInfo apiInfo() {     // 指定API文档开头部分信息
        return new ApiInfoBuilder()
                .title("Spring Security Demo RESTful API")     // 文档标题
                .description("https://www.kwseeker.top/security")       // 文档描述
                .termsOfServiceUrl("https://www.kwseeker.top/security")
                .version("v1")
                .build();
    }

}
