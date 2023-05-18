# Spring Security

此文档只是对流程图的补充。

Spring Security是一个框架，提供 [认证（authentication）](https://springdoc.cn/spring-security/features/authentication/index.html)、[授权（authorization）](https://springdoc.cn/spring-security/features/authorization/index.html) 和 [保护，以抵御常见的攻击](https://springdoc.cn/spring-security/features/exploits/index.html)。它对保护命令式和响应式应用程序有一流的支持，是保护基于Spring的应用程序的事实标准。

中文文档:

+ https://springdoc.cn/spring-security/

+ https://www.springcloud.cc/spring-security.html (单页)

  > 这个版本比较老，但是文档全部在一个网页，主要是方便查询。

官方案例: [spring-security-samples](https://github.com/spring-projects/spring-security-samples)



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



## HttpFirewall

作用：

+ 规范化请求URL, 便于匹配

  例如，一个原始的请求路径为 `/secure;hack=1/somefile.html;hack=2`，被返回为 `/secure/somefile.html`

+ 防止 HTTP 响应拆分（HTTP Response Splitting，HRS）攻击

  也称为CRLF注入漏洞，恶意攻击者将CRLF换行符加入到请求中，从而使一个请求产生两个响应，前一个响应是服务器的响应，而后一个则是攻击者设计的响应。

  参考：[CRLF注入（HTTP响应拆分/截断）](https://zhuanlan.zhihu.com/p/617476453)

+ 防止 [跨站追踪（XST）](https://www.owasp.org/index.php/Cross_Site_Tracing) 和 [HTTP Verb Tampering](https://www.owasp.org/index.php/Test_HTTP_Methods_(OTG-CONFIG-006))



## [异步请求处理](https://springdoc.cn/spring-security/servlet/integrations/mvc.html#mvc-async)

Spring Web MVC 3.2+对 异步请求处理有很好的支持。不需要额外的配置，Spring Security会自动将 SecurityContext 设置为调用 Controller 返回的 Callable 的 Thread。例如，下面的方法会自动用创建 Callable 时可用的 SecurityContext 来调用它的 Callable。

```java
@RequestMapping(method=RequestMethod.POST)
public Callable<String> processUpload(final MultipartFile file) {
    return new Callable<String>() {
        public Object call() throws Exception {
        // ...
        return "someView";
        }
    };
}
```

Spring MVC 异步请求场景包括：

1. 处理需要长时间才能完成的请求。例如：搜索引擎模糊搜索、大文件下载、视频文件转换等。
2. 处理大量并发请求。例如：高并发的在线聊天、商品秒杀活动等。

参考：[高性能关键技术之---体验Spring MVC的异步模式（Callable、WebAsyncTask、DeferredResult） 基础使用篇](https://cloud.tencent.com/developer/article/1497796)

