# Spring Security 

## Spring Security 与 鉴授权框架的关系

+ Spring Security 与 OAuth2、JWT、Shiro、Social、LDAP、Basic、Digest 的关系？

    Spring Security 与 OAuth2 的关系个人感觉就好像 Slf4j 与 Logback 的关系。
    而 OAuth2 和 Social 类似都是做第三方登录的，不过OAuth2是使用token，做授权，Social是使用session，做认证；所以二者可以搭配使用。
    JWT是优化 Token 存储的。
    而LDAP、Basic、Digest 是进行认证的具体实现，他们拥有不同的使用场景。
    Spring Security 和 Apache Shiro 是两套开源的权限管理框架，为企业提供了全面的安全管理服务。
    
## Spring Security 工作原理

![Spring Security 与 Web应用的关系](https://github.com/spring-guides/top-spring-security-architecture/raw/master/images/security-filters.png)  
很关键的一句总结：  
Spring Security 就是在 Spring web应用的过滤器链，通过 FilterChainProxy 插入了一个认证的过滤器链。

工作原理猜想（遇到复杂的逻辑要根据文档上零碎的知识大胆假设然后去调试验证自己的想法）：  
认证过滤器链中每个filter应该是对应一种认证方式，如OAuth2、LDAP、Basic等；
多个filter对应一组 AuthenticationProvider，通过 ProviderManager（AuthenticationManager的实现类）管理，
认证时轮询 AuthenticationProvider（对应filter） 进行认证。
每个过滤器或者说 AuthenticationProvider 共用 一个ThreadLocal类型的 SecurityContextHolder 存储 SecurityContext，
SecurityContext 用于存储 Authentication（认证状态），Filter 根据认证方式
做实际的认证处理会产生认证的结果，成功的话记录认证成功状态并break，还会返回用户信息 UserDetails,
用户信息是从 UserDetailsService中获取，然后还可以授予用户权限 GrantedAuthority。

工作流程图：

+ Spring Security FormLogin 工作流程图

    ![](https://ss.csdn.net/p?https://mmbiz.qpic.cn/mmbiz_png/GvtDGKK4uYngjiaicZ6UqibkEHRhzUg8JYLz2G6ILGaaibJe3fOl7LSDyBmHFJy3wdJqmVKdYKUYxQALGibSef9QJRA/640?wx_fmt=png)

+ Spring Security LDAP 工作流程图

+ Spring Security OAuth2 工作流程图


其实这里面还有很多细节需要弄明白：  
+ 认证过滤器的定义是什么样的？如果要插入一个自定义的过滤器应该怎么做？

源码分析：

+ 过滤器

    - SecurityContextPersistenceFilter（真正意义上的第一个安全过滤器，认证结果持久化存储过滤器）   
    
        org.springframework.security.web.context
        
        首先判断SecurityContextRepository中的HttpSession是否已经存在SecurityContext；
        是则返回否则新建然后返回。将返回的SecurityContext存储到SecurityContextHolder中共过滤器链其他部分使用。
        
        待过滤器链执行完后执行 finally 块，清空 SecurityContextHolder中的内容，清空ThreadLocal内容；而过滤器处理的结果
        SecurityContext 则转存到 SecurityContextRepository 中.
        
        总结： 
        请求到来，首先需要知道请求所属session现在的认证状态，供后面的过滤器判断是否需要认证，为了方便传递将这个认证状态
        从 SecurityContextRepository 中拿出来放到线程ThreadLocal，最后处理完了获得一个新的认证结果，再存放到 SecurityContextPersistenceFilter
        中。这个Filter的功能和名称一样，做认证结果持久化存储的。
        
        ThreadLocal就像是一条传送带(chain.doFilter)上的货仓，SecurityContext 在第一个过滤器被从HttpSession中取出装载到传送带的货仓（ThreadLocal），
        然后每级doFilter()调用将其传递给下一个过滤器，而不是通过传参数的方式；经过一个个的过滤器处理后，SecurityContext 转了一圈后又被返还给第一个过滤器，
        存在HttpSession中。 
        
        * SecurityContextRepository
        
        * SecurityContext
            
            内部通过设置或获取Authentication，Authentication 是认证状态和认证详情信息。
        
        疑问：
        1）HttpSessionSecurityContextRepository
            
            SecurityContextRepository 用于保存 SecurityContext。HttpSessionSecurityContextRepository 是将 SecurityContext
            存储到 HttpSession 中，这样属于同一个 HttpSession 的多个请求就能用此机制访问同一安全上下文了。
        
    - LogoutFilter
    
    - AbstractAuthenticationProcessingFilter (TODO：自定义Filter好像都是继承的这个)
    
    - DefaultLoginPageGeneratingFilter
    
        生成默认的登录页面。
        
    - BasicAuthenticationFilter
    
        Basic认证。
        
    - SecurityContextHolderAwareRequestFilter
    
    - RememberMeAuthenticationFilter
    
    - AnonymousAuthenticationFilter
    
        为用户分配匿名用户权限。
    
    - ExceptionTranslationFilter
    
    - SessionManagementFilter
    
    - FilterSecurityInterceptor
    
+ FilterChainProxy 过滤器链代理    

    用于管理过滤器的执行顺序。

+ Spring Security 核心处理流程  

    ![](./imgs/Spring%20Security%20核心处理流程.png)  
    主要分为两个阶段：  
    1）认证：  
    2）授权：  
    
    - 数据库管理（认证信息的存储管理）
        
        * UserDetailService（I）
    
            通过用户名获取用户详情信息。
            如果我们使用数据库存储用户认证详情信息，则需要创建 用户表、角色表、权限表、用户和角色关系表、权限和角色关系表（RBAC模型）。
        
            * UserDetail（信息一般是从资源服务器获取的，比如数据库、LDAP服务器）
                
                记录用户名、密码、账户是否过期、是否锁定、证书是否过期、账户是否有效，
                以及用户的权限集合。
                
            * Authentication
                
                Authentication 对象在用户访问时创建，获取用户传入的认证信息（如从页面传入的用户名密码、从cookie传入的token等）；
                使用这个未认证的对象与从资源服务器获取的UserDetail对象进行对比，判定是否认证通过，通过则将UserDetail中的数据
                拷贝到Authentication组成一个完整的认证通过的对象。
    
    - 权限缓存
    
        * CachingUserDetailsService（内部使用 NullUserCache）
        
            实现 UserDetailService 接口。对象如其名，内部实现和redis 做 MySQL 缓存差不多；
            也是先从缓存中通过用户名查找用户详情信息，如果没有找到就去 资源服务器（数据库、LDAP等）找。
            
        * EhCacheBasedUserCache
        
            基于EhCache实现。
            
        * 也可以仿造上面的代码自行实现权限缓存(一般继承抽象类，而不是实现接口)，比如使用 Redis、Memcache。
        
    - 访问决策管理 AccessDecisionManager(I)
        
        * AbstractAccessDecisionManager (自定义投票器时继承此抽象类实现，如果某个操作要同时有两种权限才能通过这里自己实现投票器)
            
            support()方法的返回值作为是否投票通过的结果，即是否能执行某个操作。
            
            * AccessDecisionVoter（访问权限投票器）
            
                * RoleVoter (基于角色的投票器)
        
                    核心方法是 vote() ,即投票决定是否授予用户权限；
                    首先提取已经被认证的权限，通过认证阶段，会从数据库等地获取到被认证的权限；
                    vote() 的参数 Collection<ConfigAttribute> attributes 代表某个操作要求的权限集合，只要被认证的权限与 attributes 中的权限有一项相同就返回1，否则放回-1。
                    即只要有一项权限被认证通过就允许访问资源。
            
            * AffirmativeBased（一票通过）
            
            * ConsensusBased （半数通过）
                
            * UnanimousBased (全票才通过)
   
### Spring Security 自动装配

看完上面的还是比较懵逼，感觉还是缺了很多东西，脑力里组件不起来一个模型。继续看看Spring Security的自动装配。
看看上面的组件是怎么配置的。

+ 四个配置类

    - WebSecurityConfiguration
    
        里面创建了几个bean：
        DelegatingApplicationListener 
        SecurityExpressionHandler 
        springSecurityFilterChain
        WebInvocationPrivilegeEvaluator
        FilterChainProxySecurityConfigurer
        AutowiredWebSecurityConfigurersIgnoreParents
    
        * SpringSecurityFilterChain
        
            其他的都不知道是干什么的只有一个 SpringSecurityFilterChain 眼熟。
    
            * WebSecurity
                
                由 WebSecurityConfiguration 创建用于创建 FilterChainProxy。
                不懂设计思想看代码很痛苦，不看源码哪里去体会他的设计思想，矛盾？FilterChainProxy是怎么代理的？
                
                内部结构：
                1) List<RequestMatcher> 用于添加 Spring Security 应该忽略的请求的匹配器。
                2) List<SecurityBuilder<? extends SecurityFilterChain>>
                3) IgnoredRequestConfigurer
                4) FilterSecurityInterceptor
                5) HttpFirewall
                6) WebInvocationPrivilegeEvaluator
                7) DefaultWebSecurityExpressionHandler
                8) SecurityExpressionHandler<FilterInvocation>
             
            * HttpSecurity
            
                可以为指定的http请求配置基于web的安全设置。
    
        * FilterChainProxySecurityConfigurer
    
    - SpringWebMvcImportSelector
    
        根据是否有引入指定的类，决定返回的要载入的bean的全路径。这个应该是配合其他代码实现配置类 WebMvcSecurityConfiguration 注入的。
     
    - OAuth2ImportSelector
    
        同上。
    
    - WebSecurityConfigurerAdapter（可以被用户修改自定义内部组件）
                    
        用户配置类继承这个对象实现自定义配置。
        源码里面对每个可以 Override 的方法都做了说明，指出了覆盖这个这方法可以自定义配置哪些功能。
        
## Spring Security 架构实现

+ 安全拦截器

    - 认证管理器
    
        * Basic  
        
            将用户名密码进行base64编码传输  
        
        * Digest
            
            添加头信息在密码上然后使用MD5编码再取Hash值，传输此Hash值
                
        * X.509
        
            数字证书，适用于邮件签名，程序中认证。
            
        * LDAP（Lightweight Direct Access Protocol）
        
            认证服务器，所有服务都通过LDAP统一管理权限。
            
        * Form
        
            表单认证
  
        * ...
    
    - 访问决策管理器
    
    - 运行身份管理器

## Spring Security 编程

1）配置

2）指定鉴授权方式

3）请求授权控制

4）处理注销登录

### Spring Security 集成 OAuth2

1）OAuth2 client

2）OAuth2 Login

3）OAuth2 Resource Server

4）OAuth2 Authentication

5）


## Spring Security 常用功能 


