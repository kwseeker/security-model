## 手机短信验证码认证实现

Spring Security中并没有实现这种认证方式，但是可以参考 UsernamePasswordAuthentication 认证的实现原理
实现手机短信验证码认证。

#### 基于表单的认证原理

 UsernamePasswordAuthentication 认证是表单认证的一部分，如下框图所示。

```
graph TD
    SecurityContextPersistenceFilter-->AbstractAuthenticationProcessingFilter
    AbstractAuthenticationProcessingFilter--> UsernamePasswordAuthenticationFilter
    subgraph 用户名密码认证
        UsernamePasswordAuthenticationFilter-->|UsernamePasswordAuthenticationToken|AuthenticationManager
        AuthenticationManager-->AuthenticationProvider
        AuthenticationProvider-->UserDetailsService
        UserDetailsService-->UserDetails
        UserDetails-->Authentication认证通过
    end
    Authentication认证通过-->SecurityContext
    SecurityContext-->SecurityContextHolder
    SecurityContextHolder-->RememberMeServices
    RememberMeServices-->AuthenticationSuccessHandler
```

#### 实现手机短信验证码认证(添加自定义认证)

```
graph TD
    SmsCodeAuenticationFilter-->|SmsCodeAuthenticationToken|AuthenticationManager共用的类
    AuthenticationManager共用的类-->SmsCodeAuthenticationProvider
    SmsCodeAuthenticationProvider-->UserDetailsService
    UserDetailsService-->UserDetails
    UserDetails-->Authentication认证通过
```

主要分为下面五个部分

+ SmsCodeAuthenticationFilter
    1. 继承 AbstractAuthenticationProcessingFilter
    2. 通过 AntPathRequestMatcher() 方法指定Filter过滤的URL
    3. 重新实现 attemptAuthentication() 方法逻辑，进行条件审查并指定 SmsCodeAuthenticationToken 的实例作为认证结果的存储容器
+ SmsCodeAuthenticationToken
    继承 UsernamePasswordAuthenticationToken
+ SmsCodeAuthenticationProvider
    认证逻辑以及认证成功数据的存储（存储逻辑Security做好了，只需要指定存成什么类型 SmsCodeAuthenticationToken）  
    以及实现成功认证后检索用户的详细信息 UserDetails 的方法
+ SmsCodeAuthenticationConfig 将手机短信验证码认证配置到后台应用中  
    1. 实例化过滤器 SmsCodeAuenticationFilter
    2. 指定过滤器使用认证管理器 AuthenticationManager
    3. 设置认证成功(AuthenticationSuccessHandler)或失败(AuthenticationFailureHandler)的处理器
    4. 实例化手机短信验证码认证实现Provider SmsCodeAuthenticationProvider，并设置使用UserDetailsService数据验证
    5. 指定 SmsCodeAuthenticationProvider 和 SmsCodeAuthenticationFilter 对应关系，并将 SmsCodeAuthenticationFilter
        添加到过滤器链 AbstractPreAuthenticatedProcessingFilter 后面
+ 在总的配置文件中，将 SmsCodeAuthenticationConfig 添加进去
```
    @Autowired
    private SmsCodeAuthenticationConfig smsCodeAuthenticationConfig;
    http.apply(smsCodeAuthenticationConfig).and()
        ...   
```
    
