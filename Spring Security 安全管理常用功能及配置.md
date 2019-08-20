# Spring Security 安全管理常用功能及配置

参考：
[Spring Security Web 5.1.2 源码解析 -- 安全相关Filter清单](https://blog.csdn.net/andy_zhang2007/article/details/84726992)

## 总结

这是分析下面文档、源码、实例代码共同分析出来的结果：

+ 1）两个重要的配置类：`WebSecurityConfigurerAdapter` `WebSecurityConfiguration`

    `WebSecurityConfigurerAdapter`是配置的第一个入口类；应用启动时依次初始化或依赖注入此类依赖的每个组件Bean；如密码编码器、AuthenticationBuilder、
    DefaultPasswordEncoderAuthenticationManagerBuilder (AuthenticationManager)、ContentNegotationStrategy等。

    `WebSecurityConfiguration`是配置的第二个入口类，如上初始化了 AutowiredWebSecurityConfigurersIgnoreParents、
    FilterChainProxySecurityConfigurer、DelegatingApplicationListener、SpringSecurityFilterChain等。

+ 2）`springSecurityFilterChain` Bean的初始化(WebSecurityConfiguration#springSecurityChain())

    重点分析下 springSecurityFilterChain, 如果 webSecurityConfigurers(WebSecurityConfigurerAdapter的实例list) 不为空，则执行构造方法 AbstractConfiguredSecurityBuilder#doBuild()。
    读取所有 WebSecurityConfigurerAdapter 类实例依次进行配置和初始化，

    ```text
    0 = {LinkedHashMap$Entry@5391} "class top.kwseeker.security.formlogin.config.SpringSecurityApiConfig$$EnhancerBySpringCGLIB$$bbffae31" -> " size = 1"
    1 = {LinkedHashMap$Entry@5392} "class top.kwseeker.security.formlogin.config.SpringSecurityCommonConfig$$EnhancerBySpringCGLIB$$b0fcc32e" -> " size = 1"
    ```text
    然后读取每个 WebSecurityConfigurerAdapter实例的 HttpSecurity，如果为空就用
    authenticationBuilder(即configure方法的传参 AuthenticationManagerBuilder auth)、sharedObjects新建，并附加默认的HttpSecurity设置和用户添加的设置。

    HttpSecurity的创建依赖
    ```text
    HttpSecurity
        -> AuthenticationManagerBuilder (默认在setApplicationContext()方法中实例化，类型为DefaultPasswordEncoderAuthenticationManagerBuilder)
            -> AuthenticationManager (父AuthenticationManager)
            -> DefaultAuthenticationEventPublisher
        -> 
    ```

    其中 AuthenticationManager 的创建也经历的 doBuild() 方法的初始化、配置、创建阶段。
    分别对内部的list中的每个 SecurityConfigurer(如下) 进行初始化配置（应该是配置类的configure()方法设置的配置）

    ```text
    0 = {LinkedHashMap$Entry@5432} "class org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration$EnableGlobalAuthenticationAutowiredConfigurer" -> " size = 1"
    1 = {LinkedHashMap$Entry@5433} "class org.springframework.security.config.annotation.authentication.configuration.InitializeAuthenticationProviderBeanManagerConfigurer" -> " size = 1"
    2 = {LinkedHashMap$Entry@5434} "class org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer" -> " size = 1"
    ```

    然后将 HttpSecurity（SecurityFilterChainBuilder）传入 WebSecurity 的 SecurityFilterChainBuidler，用于创建过滤器链。
    每一个WebSecurityConfigurerAdapter都对应一个HttpSecurity;

    然后执行配置阶段

    然后执行构建阶段，构建阶段又经历了初始化，配置，和构建流程。
    不过这次对象变为了

    ```text
    0 = {LinkedHashMap$Entry@6807} "class org.springframework.security.config.annotation.web.configurers.CsrfConfigurer" -> " size = 1"
    1 = {LinkedHashMap$Entry@6808} "class org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer" -> " size = 1"
    2 = {LinkedHashMap$Entry@6809} "class org.springframework.security.config.annotation.web.configurers.HeadersConfigurer" -> " size = 1"
    3 = {LinkedHashMap$Entry@6810} "class org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer" -> " size = 1"
    4 = {LinkedHashMap$Entry@6811} "class org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer" -> " size = 1"
    5 = {LinkedHashMap$Entry@6812} "class org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer" -> " size = 1"
    6 = {LinkedHashMap$Entry@6813} "class org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer" -> " size = 1"
    7 = {LinkedHashMap$Entry@6814} "class org.springframework.security.config.annotation.web.configurers.ServletApiConfigurer" -> " size = 1"
    8 = {LinkedHashMap$Entry@6815} "class org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer" -> " size = 1"
    9 = {LinkedHashMap$Entry@6816} "class org.springframework.security.config.annotation.web.configurers.LogoutConfigurer" -> " size = 1"
    10 = {LinkedHashMap$Entry@6817} "class org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer" -> " size = 1"
    11 = {LinkedHashMap$Entry@6818} "class org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer" -> " size = 1"
    ```

    反正就是把配置转成Filter添加到过滤器链中。源码中频繁使用代理使代码可读性变的好差。

    log中打印出来了最终生成的过滤器链,因为配置了两个 WebSecurityConfigurerAdapter 所以生成了两个过滤器链。

    ```text
    2019-08-19 18:25:54.999  INFO 64782 --- [           main] o.s.s.web.DefaultSecurityFilterChain     : Creating filter chain: Ant [pattern='/api/**'], [org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@3c5dbdf8, org.springframework.security.web.context.SecurityContextPersistenceFilter@7f353d99, 
    org.springframework.security.web.header.HeaderWriterFilter@22a736d7, 
    org.springframework.security.web.csrf.CsrfFilter@5e1d03d7, 
    org.springframework.security.web.authentication.logout.LogoutFilter@102ecc22, 
    org.springframework.security.web.authentication.www.BasicAuthenticationFilter@43e9089, 
    org.springframework.security.web.savedrequest.RequestCacheAwareFilter@4649d70a, org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@31ee2fdb, org.springframework.security.web.authentication.AnonymousAuthenticationFilter@352c44a8, 
    org.springframework.security.web.session.SessionManagementFilter@23b8d9f3, 
    org.springframework.security.web.access.ExceptionTranslationFilter@210308d5, 
    org.springframework.security.web.access.intercept.FilterSecurityInterceptor@54e3658c]
    2019-08-19 18:26:45.917  INFO 64782 --- [           main] o.s.s.web.DefaultSecurityFilterChain     : Creating filter chain: any request, [org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@561b61ed, org.springframework.security.web.context.SecurityContextPersistenceFilter@6981f8f3, 
    org.springframework.security.web.header.HeaderWriterFilter@4c6007fb, 
    org.springframework.security.web.csrf.CsrfFilter@80bfdc6, 
    org.springframework.security.web.authentication.logout.LogoutFilter@68a4dcc6, 
    org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter@4b6e1c0, org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter@e84fb85, org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter@654c7d2d, org.springframework.security.web.savedrequest.RequestCacheAwareFilter@623dcf2a, org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@2eae4349, org.springframework.security.web.authentication.AnonymousAuthenticationFilter@26cb5207, 
    org.springframework.security.web.session.SessionManagementFilter@3e33d73e, 
    org.springframework.security.web.access.ExceptionTranslationFilter@6edcad64, 
    org.springframework.security.web.access.intercept.FilterSecurityInterceptor@3dffc764]
    ```

    ```java
    protected final O doBuild() throws Exception {
        synchronized (configurers) {
            buildState = BuildState.INITIALIZING;

            beforeInit();
            init();             //读取auth中所有配置项，如 InMemoryUserDetailsManagerConfigurer，然后初始化每个配置

            buildState = BuildState.CONFIGURING;

            beforeConfigure();
            configure();        //执行auth中所有的配置，如 InMemoryUserDetailsManagerConfigurer.configure(WebSecurityConfigurerAdapter builder)
                                //针对上面的配置就是在内存中创建三个用户。

            buildState = BuildState.BUILDING;

            O result = performBuild();  //创建 ProviderManager，authenticationProviders中的值又是何时加进去的？ProviderManager又是干什么的？

            buildState = BuildState.BUILT;

            return result;
        }
    }
    ```

+ 3）认证流程

    http请求发过来, Spring Security 处理的入口是哪里？前面的代码看晕了，只能从调用堆栈看了。  
    调用堆栈：  

    ```text
    doFilter:174, FilterChainProxy (org.springframework.security.web)               //这一行是调用 Spring Security 的 FilterChainProxy，正式进入 Spring Security 的认证处理流程
    invokeDelegate:357, DelegatingFilterProxy (org.springframework.web.filter)
    doFilter:270, DelegatingFilterProxy (org.springframework.web.filter)
    internalDoFilter:193, ApplicationFilterChain (org.apache.catalina.core)
    //... 此处省略一些Spring web filter
    doFilterInternal:200, CharacterEncodingFilter (org.springframework.web.filter)
    doFilter:118, OncePerRequestFilter (org.springframework.web.filter)
    internalDoFilter:193, ApplicationFilterChain (org.apache.catalina.core)
    //... 此处省略一些Tomcat的处理
    run:49, SocketProcessorBase (org.apache.tomcat.util.net)
    runWorker:1149, ThreadPoolExecutor (java.util.concurrent)
    run:624, ThreadPoolExecutor$Worker (java.util.concurrent)
    run:61, TaskThread$WrappingRunnable (org.apache.tomcat.util.threads)
    run:748, Thread (java.lang)
    ```

    从调用堆栈可以看到是通过 ApplicationFilterChain(Tomcat) -> DelegatingFilterProxy(Spring) -> FilterChainProxy(Spring Security)。
    这里就有问题，ApplicationFilterChain是什么，DelegatingFilterProxy 是什么？Spring Security 的 FilterChainProxy 又是怎么注册到 DelegatingFilterProxy的？
    这个设计思想是什么？但是这个问题先放一放,后面有时间再研究，具体可以参考：[Spring Security(六)—SpringSecurityFilterChain加载流程深度解析](https://www.cnkirito.moe/spring-security-7/)

    - 3.1) FilterChainProxy 执行过滤处理 doFilter

        DelegatingChainProxy代理的过滤器列表；
        内部判断过滤器是否被委托给其他代理处理，springSecurityFilterChain 就是被委托给 Spring Security 的 FilterChainProxy 处理。

        ```text
        0 = {ApplicationFilterConfig@6752} "ApplicationFilterConfig[name=characterEncodingFilter,filterClass=org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter]"
        1 = {ApplicationFilterConfig@6824} "ApplicationFilterConfig[name=hiddenHttpMethodFilter, filterClass=org.springframework.boot.web.servlet.filter.OrderedHiddenHttpMethodFilter]"
        2 = {ApplicationFilterConfig@6825} "ApplicationFilterConfig[name=formContentFilter, filterClass=org.springframework.boot.web.servlet.filter.OrderedFormContentFilter]"
        3 = {ApplicationFilterConfig@6826} "ApplicationFilterConfig[name=requestContextFilter, filterClass=org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter]"
        4 = {ApplicationFilterConfig@6501} "ApplicationFilterConfig[name=springSecurityFilterChain, filterClass=org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean$1]"
        5 = {ApplicationFilterConfig@6827} "ApplicationFilterConfig[name=Tomcat WebSocket (JSR356) Filter, filterClass=org.apache.tomcat.websocket.server.WsFilter]"
        ```

        其中index=4的就是 Spring Security 的过滤器链，但是这个过滤器链(ApplicationFilterConfig)并不是真正的过滤器链而是类似过滤器代理；
        而这个过滤器代理中的filter.delegate.filterChains (DefaultSecurityFilterChain) 同样不是过滤器而是请求url的匹配器，通过 requestMatcher对请求进行匹配，而匹配器内部的 filters 才是真正的过滤器链。

        所以针对 Spring Security WebSecurityConfigurerAdapter 中配置的 web 匹配规则，每一个匹配规则都有一条过滤器链。

        ```text
        delegate = {FilterChainProxy@6468} "FilterChainProxy[Filter Chains: [[ Ant [pattern='/resources/**'], []], [ Ant [pattern='/static/**'], []], [ Ant [pattern='/actuator/**'], []], [ Ant [pattern='/user/login'], []], [ Ant [pattern='/api/**'], [org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@17f3eefb, org.springframework.security.web.context.SecurityContextPersistenceFilter@48b4a043, org.springframework.security.web.header.HeaderWriterFilter@74ea46e2, org.springframework.security.web.csrf.CsrfFilter@372ca2d6, org.springframework.security.web.authentication.logout.LogoutFilter@2b6fcb9f, org.springframework.security.web.authentication.www.BasicAuthenticationFilter@54e02f6a, org.springframework.security.web.savedrequest.RequestCacheAwareFilter@4821aa9f, org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@2370ac7a, org.springframework.security.web.authentication.AnonymousAuthenticationFilter@3ba46e63, org.springframework.security.web.session.SessionManagem"
            filterChains = {ArrayList@6477}  size = 6
                0 = {DefaultSecurityFilterChain@6936} "[ Ant [pattern='/resources/**'], []]"
                requestMatcher = {AntPathRequestMatcher@7031} "Ant [pattern='/resources/**']"
                filters = {ArrayList@7032}  size = 0
                1 = {DefaultSecurityFilterChain@6937} "[ Ant [pattern='/static/**'], []]"
                requestMatcher = {AntPathRequestMatcher@7036} "Ant [pattern='/static/**']"
                filters = {ArrayList@7037}  size = 0
                2 = {DefaultSecurityFilterChain@6938} "[ Ant [pattern='/actuator/**'], []]"
                requestMatcher = {AntPathRequestMatcher@7039} "Ant [pattern='/actuator/**']"
                filters = {ArrayList@7040}  size = 0
                3 = {DefaultSecurityFilterChain@6939} "[ Ant [pattern='/user/login'], []]"
                requestMatcher = {AntPathRequestMatcher@7042} "Ant [pattern='/user/login']"
                filters = {ArrayList@7043}  size = 0
                4 = {DefaultSecurityFilterChain@6940} "[ Ant [pattern='/api/**'],
                    0 = {WebAsyncManagerIntegrationFilter@7049} 
                    1 = {SecurityContextPersistenceFilter@7050} 
                    2 = {HeaderWriterFilter@7051} 
                    3 = {CsrfFilter@7052} 
                    4 = {LogoutFilter@7053} 
                    5 = {BasicAuthenticationFilter@7054} 
                    6 = {RequestCacheAwareFilter@7055} 
                    7 = {SecurityContextHolderAwareRequestFilter@7056} 
                    8 = {AnonymousAuthenticationFilter@7057} 
                    9 = {SessionManagementFilter@7058} 
                    10 = {ExceptionTranslationFilter@7059} 
                    11 = {FilterSecurityInterceptor@7060} 
                requestMatcher = {AntPathRequestMatcher@7045} "Ant [pattern='/api/**']"
                filters = {ArrayList@7046}  size = 12
                5 = {DefaultSecurityFilterChain@6941} "[ any request, 
                    0 = {WebAsyncManagerIntegrationFilter@6522} 
                    1 = {SecurityContextPersistenceFilter@6532} 
                    2 = {HeaderWriterFilter@6860} 
                    3 = {CsrfFilter@6861} 
                    4 = {LogoutFilter@6862} 
                    5 = {UsernamePasswordAuthenticationFilter@6863} 
                    6 = {DefaultLoginPageGeneratingFilter@6864} 
                    7 = {DefaultLogoutPageGeneratingFilter@6865} 
                    8 = {RequestCacheAwareFilter@6866} 
                    9 = {SecurityContextHolderAwareRequestFilter@6867} 
                    10 = {AnonymousAuthenticationFilter@6868} 
                    11 = {SessionManagementFilter@6869} 
                    12 = {ExceptionTranslationFilter@6870} 
                    13 = {FilterSecurityInterceptor@6871} 
                    requestMatcher = {AnyRequestMatcher@5756} "any request"
                filters = {ArrayList@6855}  size = 14
        ```

        前四个因为配置了 ignoring(), 所以初始化的时候不会分配任何的安全过滤器进行过滤。

        FilterChainProxy doFilterInternal() 内部先匹配请求获取应该使用哪个过滤器链（就是从上面6个路径中匹配出来一条过滤器链做过滤处理）；
        然后判断这个过滤器链中 filters 的 size 是否为0（如上面前四种匹配URL，filters的size为0，后两种分别为12、14），
        如果为0，则交还给 ApplicationFilterChain 继续处理（这个是Tomcat的类），否则 Spring Security FilterChainProxy 构造一个 VirtualFilterChain 进行过滤处理。

        关于 Tomcat 和 Spring 的过滤器链数据结构的猜想（没有仔细看，待验证）：

        核心接口是FilterChain(Java标准接口)，ApplicationFilterChain(Tomcat实现类)，VirtualFilterChain(Spring Security实现类)均实现此接口（ 看源码发现VirtualFilterChain 和 ApplicationFilterChain 结构完全一致，所以Spring是直接借鉴的Tomcat的过滤器链的设计），
        它们内部维持实际的过滤器链 List<Filter> ，然后还维持了记录当前执行到哪个过滤器的位置信息 currentPosition 和过滤器链中过滤器的数量 size。
        然后利用递归，实现依次执行过滤器链的过滤器。

        关于Spring Security 的 FilterChainProxy 这个类实际上相当于一个容器，用于记录 Spring Security 的匹配规则及对应的过滤器链；
        在请求到来时，匹配请求信息获取对应的过滤器链，构造用于实际过滤操作的过滤器链结构 VirtualFilterChain。
        所以此处大胆猜测一下，初始化阶段估计就是在解析配置构造 FilterChainProxy 这个代理类（TODO：验证）。

    + 3.2) 过滤器链中过滤器的链式执行

        过滤器的链式执行就是递归自己调自己的doFilter接口，根据 currentPosition 和 size 判断是否执行完所有过滤器（退出）。
        下面分析每个过滤器的过滤流程。再跟下初始化流程，看每个过滤器是怎么配置的。

        下面并不全（查看全部Filter使用find去源码搜索一下）：

        -  WebAsyncManagerIntegrationFilter

            由 `http.addFilter(new WebAsyncManagerIntegrationFilter())` 配置添加。

            此过滤器的注释说：用于集成SecurityContext到Spring异步执行机制中的WebAsyncManager。

            ```
            Provides integration between the {@link SecurityContext} and Spring Web's
            {@link WebAsyncManager} by using the
            {@link SecurityContextCallableProcessingInterceptor#beforeConcurrentHandling(org.springframework.web.context.request.NativeWebRequest, Callable)}
            to populate the {@link SecurityContext} on the {@link Callable}.
            ```

            * WebAsyncManager

            * SecurityContextCallableProcessingInterceptor

        - SecurityContextPersistenceFilter（真正意义上的第一个安全过滤器，认证结果持久化存储过滤器）

            包：org.springframework.security.web.context

            由 `http.securityContext()` 配置添加。

            首先判断SecurityContextRepository中的HttpSession是否已经存在SecurityContext；
            是则返回否则新建然后返回。将返回的SecurityContext存储到SecurityContextHolder中共过滤器链其他部分使用。

            待过滤器链执行完后执行 finally 块，清空 SecurityContextHolder中的内容，清空ThreadLocal内容；而过滤器处理的结果
            SecurityContext 则转存到 SecurityContextRepository 中.

            总结： 

            请求进入到这个过滤器，首先需要知道请求所属session现在的认证状态，供后面的过滤器判断是否需要认证，为了方便传递将这个认证状态
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

        - HeaderWriterFilter

            由 `http.headers()` 配置添加。

            在一个请求的处理过程中为响应对象(注意这里是响应对象)增加一些头部信息。头部信息由外部提供，比如用于增加一些浏览器保护的头部，比如X-Frame-Options, X-XSS-Protection和X-Content-Type-Options等。
            这种细节的代码参考别人的博客即可。

        - CsrfFilter

            由 `http.csrf()` 配置添加。

            Spring Security Web使用该Filter解决Cross-Site Request Forgery (CSRF)攻击,使用的模式是Synchronizer token pattern (STP)。

        - LogoutFilter

            由 `http.logout()` 配置添加。

            判断请求是否是 "/logout", 是否需要执行退出登录，如果有自定义logoutUrl，则匹配自定义logoutUrl。
            如果有配置 logoutSuccessHandler 还要回调 onLogoutSuccess() 方法。

            官方默认提供了几种LogoutSuccessHandler实现参考下面章节。

        - UsernamePasswordAuthenticationFilter

            由 `http.formLogin()` 配置添加。

            检测用户名/密码表单登录认证请求并作相应认证处理:

            ```text
              1.session管理，比如为新登录用户创建新session(session fixation防护)和设置新的csrf token等
              2.经过完全认证的Authentication对象设置到SecurityContextHolder中的SecurityContext上;
              3.发布登录认证成功事件InteractiveAuthenticationSuccessEvent
              4.登录认证成功时的Remember Me处理
              5.登录认证成功时的页面跳转
            ```

        - DefaultLoginPageGeneratingFilter

        - DefaultLogoutPageGeneratingFilter

        - RequestCacheAwareFilter

            由 `http.requestCache()` 配置添加。

            提取请求缓存中缓存的请求:
            Spring Security Web认证机制(通常指表单登录)中登录成功后页面需要跳转到原来客户请求的URL。该过程中首先需要将原来的客户请求缓存下来，然后登录成功后将缓存的请求从缓存中提取出来。  
            针对该需求，Spring Security Web 提供了在http session中缓存请求的能力，也就是HttpSessionRequestCache。HttpSessionRequestCache所保存的请求必须封装成一个SavedRequest接口对象，实际上，HttpSessionRequestCache总是使用自己的SavedRequest缺省实现DefaultSavedRequest。

            ```text
            1.请求缓存在安全机制启动时指定
            2.请求写入缓存在其他地方完成
            3.典型应用场景:
                1.用户请求保护的页面，
                2.系统引导用户完成登录认证,
                3.然后自动跳转到到用户最初请求页面
            ```

        - SecurityContextHolderAwareRequestFilter

            包装请求对象使之可以访问SecurityContextHolder,从而使请求真正意义上拥有接口HttpServletRequest中定义的getUserPrincipal这种访问安全信息的能力。

        - AnonymousAuthenticationFilter

            由 `http.anonymous()` 配置添加。

            如果当前SecurityContext属性Authentication为null，将其替换为一个AnonymousAuthenticationToken。

        - SessionManagementFilter

            由 `http.sessionManagement()` 配置添加。

            该过滤器会检测从当前请求处理开始到目前为止的过程中是否发生了用户登录认证行为(比如这是一个用户名/密码表单提交的请求处理过程)，如果检测到这一情况，执行相应的session认证策略(一个SessionAuthenticationStrategy)，然后继续继续请求的处理。

            针对Servlet 3.1+,缺省所使用的SessionAuthenticationStrategy会是一个ChangeSessionIdAuthenticationStrategy和CsrfAuthenticationStrategy组合。ChangeSessionIdAuthenticationStrategy会为登录的用户创建一个新的session，而CsrfAuthenticationStrategy会创建新的csrf token用于CSRF保护。

            如果当前过滤器链中启用了UsernamePasswordAuthenticationFilter,实际上本过滤器SessionManagementFilter并不会真正被执行到上面所说的逻辑。因为在UsernamePasswordAuthenticationFilter中，一旦用户登录认证发生它会先执行上述的逻辑。因此到SessionManagementFilter执行的时候，它会发现安全上下文存储库中已经有相应的安全上下文了，从而不再重复执行上面的逻辑。

            另外需要注意的是，如果相应的session认证策略执行失败的话，整个成功的用户登录认证行为会被该过滤器否定，相应新建的SecurityContextHolder中的安全上下文会被清除，所设定的AuthenticationFailureHandler逻辑会被执行。

        - ExceptionTranslationFilter

            由 `http.exceptionHandling()` 配置添加。

            处理AccessDeniedException 和 AuthenticationException异常，将它们转换成相应的HTTP响应。

        - FilterSecurityInterceptor

            一个请求处理的安全处理过滤器链的最后一个，检查用户是否已经认证,如果未认证执行必要的认证，对目标资源的权限检查，如果认证或者权限不足，
            抛出相应的异常:AccessDeniedException或者AuthenticationException

    + 3.3) Spring Security fromLogin 默认的认证流程

        源码中默认的配置

        ```java
        http
            .csrf().and()
            .addFilter(new WebAsyncManagerIntegrationFilter())
            .exceptionHandling().and()
            .headers().and()
            .sessionManagement().and()
            .securityContext().and()
            .requestCache().and()
            .anonymous().and()
            .servletApi().and()
            .apply(new DefaultLoginPageConfigurer<>()).and()
            .logout();
        ```
        
        对应加载的过滤器

        ```
        0 = {WebAsyncManagerIntegrationFilter@7049} 
        1 = {SecurityContextPersistenceFilter@7050} 
        2 = {HeaderWriterFilter@7051} 
        3 = {CsrfFilter@7052} 
        4 = {LogoutFilter@7053} 
        5 = {RequestCacheAwareFilter@7055} 
        6 = {SecurityContextHolderAwareRequestFilter@7056} 
        7 = {AnonymousAuthenticationFilter@7057} 
        8 = {SessionManagementFilter@7058} 
        9 = {ExceptionTranslationFilter@7059} 
        10 = {FilterSecurityInterceptor@7060} 
        ```

        ```
        UsernamePasswordAuthenticationFilter  
            -> AbstractAuthenticationProcessingFilter
        ```

        认证流程：  
        a）输入用户名密码，登录；  
        b）SecurityContextPersistentFilter 创建HttpRequestResponseHolder(用于存储Request和Response), 然后从请求中获取session;
        以此session为凭据从HttpSessionSecurityContextRepository（所有SecurityContextPersistentFilter应该共享同一个此类的实例）中读取安全上下文，如果安全上下文为空（首次登录总是为空）；则新建安全上下文，并保存在 HttpSession中， 而HttpSession先保存在 HttpRequestResponseHolder中，再保存在 SecurityContextHolder（默认是ThreadLocal策略存储）中。  
        c）UsernamePasswordAuthenticationFilter 中 attempAuthentication 认证用户信息。  
        ```java
        public Authentication attemptAuthentication(HttpServletRequest request,
                HttpServletResponse response) throws AuthenticationException {
            if (postOnly && !request.getMethod().equals("POST")) {
                throw new AuthenticationServiceException(
                        "Authentication method not supported: " + request.getMethod());
            }

            String username = obtainUsername(request);
            String password = obtainPassword(request);

            if (username == null) {
                username = "";
            }

            if (password == null) {
                password = "";
            }

            username = username.trim();

            //新建一个空的认证token，只赋值用户名密码
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    username, password);

            // Allow subclasses to set the "details" property (添加请求来源地址、sessionId)
            setDetails(request, authRequest);

            // 获取 AuthenticationManager 调用 认证方法
            // 获取 AuthenticationProvider (AnonymousAuthenticationProvider/DaoAuthenticationProvider)
            // 调用 DaoAuthenticationProvider#retrieveUser() 获取用户详情（包括权限）
            // 检查用户账户是否可用是否被锁是否未过期，检查传入的密码加密后和存储的密码是否相同，再检查密码是否过期
            // 通过检查后使用用户详情信息构造认证成功的实例 UsernamePasswordAuthenticationToken
            /* token 包含如下信息
                principal = {User@6662} "org.springframework.security.core.userdetails.User@3c9922a:
                    password = "$2a$10$Z1OzdB7Xaqfcr5pqx5gBROqeVvkX/cNysza1aLpYO7FhGQ6uc/u1a"
                    username = "Arvin"
                    authorities = {Collections$UnmodifiableSet@6673}  size = 2
                        0 = {SimpleGrantedAuthority@6680} "ROLE_ADMIN"
                        1 = {SimpleGrantedAuthority@6681} "ROLE_STAFF"
                    accountNonExpired = true
                    accountNonLocked = true
                    credentialsNonExpired = true
                    enabled = true
                credentials = "1234561"
                authorities = {Collections$UnmodifiableRandomAccessList@6744}  size = 2
                    0 = {SimpleGrantedAuthority@6680} "ROLE_ADMIN"
                    1 = {SimpleGrantedAuthority@6681} "ROLE_STAFF"
                details = {WebAuthenticationDetails@6455} "org.springframework.security.web.authentication.WebAuthenticationDetails@fffde5d4:
                    remoteAddress = "0:0:0:0:0:0:0:1"
                    sessionId = "FDA9476F2046ADD633AE6AFDAE545B37"
                authenticated = true */
            // 然后将 token 信息复制到 传入的参数 authRequest 中
            return this.getAuthenticationManager().authenticate(authRequest);
        }
        ```
        d）认证成功后，还要判断sessionId是否有效，有效的话存储认证信息到session。同样检查一下csrfToken（？）。
            接着调用 successfulAuthentication() 将认证结果（Authentication类型）存储到 SecurityContextHolder（线程本地变量） 中。
            发布 InteractiveAuthenticationSuccessEvent 事件，调用 SavedRequestAwareAuthenticationSuccessHandler。
            获取缓存的请求（如果有的话）重定向这个请求的路由地址。  
        e）认证过程结束（登录也是只走到认证完成就返回了）。

        如果启动formLogin, 用户未登录访问某个受保护的路由。
        则会在AnonymousAuthenticationFilter以匿名身份登录，生成匿名用户token，
        在SessionManagementFilter生成一个session，并用于在 FilterSecurityInterceptor 中进行校验；
        判断用户是否拥有访问这个路由的权限。   
        如果用户已经登录（登录过程中在 UsernamePasswordAuthenticationFilter 先校验用户名密码，成功的话会生成 session, 并存储到安全上下文），访问这个受保护的路由。
        则直接执行到 FilterSecurityInterceptor 从安全上下文获取安全上下，进行权限校验。

+ 4）授权流程 (FilterSecurityInterceptor)

    AbstractAuthenticationProcessiongFilter#doFilter()
    是认证和授权的分水岭。

    ```java
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

        //认证和授权的分水岭
        //认证流程走到这里不继续执行过滤器链后面的过滤处理了，而授权流程不执行认证而是继续向下执行过滤器
		if (!requiresAuthentication(request, response)) {
			chain.doFilter(request, response);  //继续执行后面的授权流程
			return;
		}

        //... 省略的认证流程
		successfulAuthentication(request, response, chain, authResult);
	}
    ```

## II. Servlet Applications （Spring Security 官方文档解读）

### 6. Java Configuration

#### 6.1. Hello Web Security Java Configuration

官方文档说 Servlet 应用使用 Spring Security 第一步是创建 Spring Security Java Configuration，使用 `@EnableWebSecurity`, 配置会默认创建
一个 springSecurityFilterChain 过滤器链。

文档给了一个简单的配置类demo，配置中默认已经添加了很多基础功能。  
默认是 FormLogin 使用用户名密码登录，对所有请求进行认证检查，提供了退出、CSRF防护、Session固定保护、Security Header集成、集成了几个 Servlet API。

##### 6.1.1. AbstractSecurityWebApplicationInitializer

然后需要注册 `springSecurityFilterChain`, 官方文档主要简单交代了非Spring应用和Spring MVC中的注册方法；如后两小节。

但是没有讲 Spring Boot 注册方法； 但是看 `@EnableWebSecurity` 中有如下一段代码
完成了 `springSecurityFilterChain` 的注入。

```
@Bean(name = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME) // "springSecurityFilterChain"
public Filter springSecurityFilterChain() throws Exception {
    boolean hasConfigurers = webSecurityConfigurers != null
            && !webSecurityConfigurers.isEmpty();
    if (!hasConfigurers) {
        WebSecurityConfigurerAdapter adapter = objectObjectPostProcessor
                .postProcess(new WebSecurityConfigurerAdapter() {
                });
        webSecurity.apply(adapter);
    }
    return webSecurity.build();
}
```

除了 springSecurityFilterChain 之外还注入了其他几个 Bean

+ DelegatingApplicationListener

+ SecurityExpressionHandler

+ WebInvocationPrivilegeEvaluator

+ AutowiredWebSecurityConfigurersIgnoreParents

另外 WebSecurityConfiguration 还包含了几个重要的成员

```
private WebSecurity webSecurity;

private List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers;

@Autowired(required = false)
private ObjectPostProcessor<Object> objectObjectPostProcessor;
```

TODO: 上面成员变量和Bean的作用？

debug代码从 `WebSecurity` 的成员变量和值看，`WebSecurity` 应该相当于一个安全上下文。
而 `HttpSecurity` 则是 http请求的安全上下文，使用 `HttpSecurity` 构建 `SpringSecurityFilterChain`。

```
// WebSecurityConfigurerAdapter.java line:321
public void init(final WebSecurity web) throws Exception {
    final HttpSecurity http = getHttp();
    web.addSecurityFilterChainBuilder(http).postBuildAction(new Runnable() {
        public void run() {
            FilterSecurityInterceptor securityInterceptor = http
                    .getSharedObject(FilterSecurityInterceptor.class);
            web.securityInterceptor(securityInterceptor);
        }
    });
}
```

`List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers` 中存放的应该是手动添加的配置项，
（TODO）如 代码里面添加了 `auth.inMemoryAuthentication()`, 然后 webSecurityConfigurers中包含 `InMemoryUserDetailsManagerConfigurer`

##### 6.1.2. AbstractSecurityWebApplicationInitializer without Existing Spring

##### 6.1.3. AbstractSecurityWebApplicationInitializer with Spring MVC

#### 6.2. HttpSecurity

到上面的配置为止 `WebSecurityConfig`（自定义）只包含了 如何认证用户（？），
然后指定 http 的认证规则（下面都是默认的）：  
1）指定所有请求均需要通过认证；
2）支持使用formLogin()登录校验；
3）支持使用httpBasic()认证校验。

#### 6.3. Java Configuration and Form Login

```
protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
            .anyRequest().authenticated()
            .and()
        .formLogin()
            .loginPage("/login")
            .permitAll();
}
```

#### 6.4. Authorize Requests

#### 6.5. Handling Logouts

```
protected void configure(HttpSecurity http) throws Exception {
    http
        .logout()
            .logoutUrl("/my/logout")            //默认是 "/logout"
            .logoutSuccessUrl("/my/index")                  //默认是"/login?logout"
            .logoutSuccessHandler(logoutSuccessHandler)     //会使 logoutSuccessUrl() 无效化
            .invalidateHttpSession(true)        //Spring Security 内置了 httpsession ？
            .addLogoutHandler(logoutHandler)  
            .deleteCookies(cookieNamesToClear)
            .and()
        ...
}
```

访问"/logout"会退出登录，并使 HTTP Session 无效，清除 RememberMe 认证信息，清除 SecurityContextHolder，
重定向到 /login?logout 。

##### 6.5.1. LogoutHandler

继承此接口实现注销登录的处理，如清理token等资源；

官方提供了几种实现：

+ PersistentTokenBaseRememberMeServices

+ TokenBasedRememberMeServices

+ CookieClearingLogoutHandler

+ CsrfLogoutHandler

+ SecurityContextLogoutHandler

##### 6.5.2. LogoutSuccessHandler

LogoutFilter 判定成功退出登录后调用 LogoutSuccessHandler; 使用此接口实现类似重定向转发等功能。
和 LogoutHandler 差不多，但是可以抛出异常。

```
//测试 /logout 确实有调用上述两种注销处理器
2019-08-19 13:13:22.711  INFO 63493 --- [nio-8080-exec-7] t.k.s.formlogin.handler.MyLogoutHandler  : MyLogoutHandler#logout() called
2019-08-19 13:13:22.711  INFO 63493 --- [nio-8080-exec-7] t.k.s.f.handler.MyLogoutSuccessHandler   : MyLogoutSuccessHandler#onLogoutSuccess() called
```

##### 6.5.3. Further Logout-Related References

登录注销处理；
登录注销的Spring MVC Test；
登录注销的接口 HttpServletRequest.logout();
在记住我功能中的应用；
登录注销的CSRF处理；
CAS SSO 单点登录注销的处理；
logout 其他的XML配置元素。

具体参考后面的章节。

#### 6.6. OAuth 2.0 Client

6.6.1. ClientRegistration
6.6.2. ClientRegistrationRepository
6.6.3. OAuth2AuthorizedClient
6.6.4. OAuth2AuthorizedClientRepository / OAuth2AuthorizedClientService
6.6.5. RegisteredOAuth2AuthorizedClient
6.6.6. AuthorizationRequestRepository
6.6.7. OAuth2AuthorizationRequestResolver
6.6.8. OAuth2AccessTokenResponseClient

#### 6.7. OAuth 2.0 Login

6.7.1. Spring Boot 2.x Sample
Initial setup
Setting the redirect URI
Configure application.yml
Boot up the application
6.7.2. Spring Boot 2.x Property Mappings
6.7.3. CommonOAuth2Provider
6.7.4. Configuring Custom Provider Properties
6.7.5. Overriding Spring Boot 2.x Auto-configuration
Register a ClientRegistrationRepository @Bean
Provide a WebSecurityConfigurerAdapter
Completely Override the Auto-configuration
6.7.6. Java Configuration without Spring Boot 2.x
6.7.7. Additional Resources

#### 6.8. OAuth 2.0 Resource Server

6.8.1. Dependencies
6.8.2. Minimal Configuration
Specifying the Authorization Server
Startup Expectations
Runtime Expectations
6.8.3. Specifying the Authorization Server JWK Set Uri Directly
6.8.4. Overriding or Replacing Boot Auto Configuration
Using jwkSetUri()
Using decoder()
Exposing a JwtDecoder @Bean
6.8.5. Configuring Authorization
Extracting Authorities Manually
6.8.6. Configuring Validation
Customizing Timestamp Validation
Configuring a Custom Validator
6.8.7. Configuring Claim Set Mapping
Customizing the Conversion of a Single Claim
Adding a Claim
Removing a Claim
Renaming a Claim
6.8.8. Configuring Timeouts

#### 6.9. Authentication

这一节讲解常用的认证信息的配置。

##### 6.9.1. In-Memory Authentication

基于内存的认证配置。

##### 6.9.2. JDBC Authentication

基于JDBC驱动的数据库的认证配置。

##### 6.9.3. LDAP Authentication

基于LDAP服务器的认证配置。

##### 6.9.4. AuthenticationProvider

可以通过 AuthenticationProvider实现自己的认证配置，前面的三种其实也是实现的这个类。
如果需要自定义可以参考前面三种实现。

只有当 AuthenticationManagerBuilder 没有被引入的情况下使用；
```
@Bean
public SpringAuthenticationProvider springAuthenticationProvider() {
    return new SpringAuthenticationProvider();
}
```

##### 6.9.5. UserDetailsService

同样用于 实现自己的认证配置，但是只有当 AuthenticationManagerBuilder 没有被引入且没有AuthenticationProviderBean被定义的情况下使用；

所以优先级是 AuthenticationManagerBuilder > AuthenticationProvider > UserDetailService。

#### 6.10. Multiple HttpSecurity

配置多 HttpSecurity，这意味着可以配置多套 HttpSecurity 的认证规则；
对不同路由路径下的接口使用不同的认证规则。

WebSecurityConfigurerAdapter 加载优先级默认是 100，值越小优先级越高。

#### 6.11. Method Security

为 web 方法添加访问限制与决策。

##### 6.11.1. EnableGlobalMethodSecurity

##### 6.11.2. GlobalMethodSecurityConfiguration

支持自定义对 SpEL 表达式进行处理，只需要重写 GlobalMethodSecurityConfiguration 的 createExpressionHandler()方法；
在方法中重新实现 MethodSecurityExpressionHandler，并实例化返回。

#### 6.12. Post Processing Configured Objects

详细参考 ObjectPostProcess。

#### 6.13. Custom DSLs

### 7. Security Namespace Configuration

这一章讲的是如何通过XML配置 Spring Security 组件以及处理的规则。  
Spring Boot 主要是在代码中通过配置类的方式进行配置，但是也支持部分配置在application.properties中指定。

#### 7.1. Introduction
7.1.1. Design of the Namespace

#### 7.2. Getting Started with Security Namespace Configuration
7.2.1. web.xml Configuration
7.2.2. A Minimal <http> Configuration
7.2.3. Form and Basic Login Options
Setting a Default Post-Login Destination

##### 7.2.4. Logout Handling

支持从指定的 URL 触发登录注销，默认 URL 是 "/logout", 但是可以通过 logout-url（XML属性） 指定使用其他的 URL。
代码配置在被重写的 WebSecurityConfigurerAdapter#configure(HttpSecurity http), 如 http.logoutUrl("/my/logout") 。

7.2.5. Using other Authentication Providers
Adding a Password Encoder

#### 7.3. Advanced Web Features

7.3.1. Remember-Me Authentication
7.3.2. Adding HTTP/HTTPS Channel Security
7.3.3. Session Management
Detecting Timeouts
Concurrent Session Control
Session Fixation Attack Protection
7.3.4. OpenID Support
Attribute Exchange
7.3.5. Response Headers
7.3.6. Adding in Your Own Filters
Setting a Custom AuthenticationEntryPoint

#### 7.4. Method Security

7.4.1. The <global-method-security> Element
Adding Security Pointcuts using protect-pointcut

#### 7.5. The Default AccessDecisionManager

7.5.1. Customizing the AccessDecisionManager

#### 7.6. The Authentication Manager and the Namespace

### 8. Architecture and Implementation

#### 8.1. Technical Overview
8.1.1. Runtime Environment
8.1.2. Core Components
SecurityContextHolder, SecurityContext and Authentication Objects
The UserDetailsService
GrantedAuthority
Summary
8.1.3. Authentication
What is authentication in Spring Security?
Setting the SecurityContextHolder Contents Directly
8.1.4. Authentication in a Web Application
ExceptionTranslationFilter
AuthenticationEntryPoint
Authentication Mechanism
Storing the SecurityContext between requests
8.1.5. Access-Control (Authorization) in Spring Security
Security and AOP Advice
Secure Objects and the AbstractSecurityInterceptor
8.1.6. Localization

#### 8.2. Core Services
8.2.1. The AuthenticationManager, ProviderManager and AuthenticationProvider
Erasing Credentials on Successful Authentication
DaoAuthenticationProvider
8.2.2. UserDetailsService Implementations
In-Memory Authentication
JdbcDaoImpl
8.2.3. Password Encoding
Password History
DelegatingPasswordEncoder
BCryptPasswordEncoder
Pbkdf2PasswordEncoder
SCryptPasswordEncoder
Other PasswordEncoders
8.2.4. Jackson Support

### 9. Testing

#### 9.1. Testing Method Security

9.1.1. Security Test Setup
9.1.2. @WithMockUser
9.1.3. @WithAnonymousUser
9.1.4. @WithUserDetails
9.1.5. @WithSecurityContext
9.1.6. Test Meta Annotations

#### 9.2. Spring MVC Test Integration

9.2.1. Setting Up MockMvc and Spring Security
9.2.2. SecurityMockMvcRequestPostProcessors
Testing with CSRF Protection
Running a Test as a User in Spring MVC Test
Running as a User in Spring MVC Test with RequestPostProcessor
Testing HTTP Basic Authentication
##### 9.2.3. SecurityMockMvcRequestBuilders

+ Testing Form Based Authentication

+ Testing Logout

    使用 Spring MVC 测试退出
    ```
    mvc.perform(logout())   //如果没有使用 logout-url 修改默认url的情况下
    mvc.perform(logout("/my/logout"))
    ```

9.2.4. SecurityMockMvcResultMatchers
Unauthenticated Assertion
Authenticated Assertion

### 10. Web Application Security

#### 10.1. The Security Filter Chain

10.1.1. DelegatingFilterProxy
10.1.2. FilterChainProxy
Bypassing the Filter Chain
10.1.3. Filter Ordering
10.1.4. Request Matching and HttpFirewall
10.1.5. Use with other Filter-Based Frameworks
10.1.6. Advanced Namespace Configuration

#### 10.2. Core Security Filters

10.2.1. FilterSecurityInterceptor
10.2.2. ExceptionTranslationFilter
AuthenticationEntryPoint
AccessDeniedHandler
SavedRequest s and the RequestCache Interface
10.2.3. SecurityContextPersistenceFilter
SecurityContextRepository
10.2.4. UsernamePasswordAuthenticationFilter
Application Flow on Authentication Success and Failure

#### 10.3. Servlet API integration

10.3.1. Servlet 2.5+ Integration
HttpServletRequest.getRemoteUser()
HttpServletRequest.getUserPrincipal()
HttpServletRequest.isUserInRole(String)
10.3.2. Servlet 3+ Integration
HttpServletRequest.authenticate(HttpServletRequest,HttpServletResponse)
HttpServletRequest.login(String,String)
HttpServletRequest.logout()
AsyncContext.start(Runnable)
Async Servlet Support
10.3.3. Servlet 3.1+ Integration
HttpServletRequest#changeSessionId()

#### 10.4. Basic and Digest Authentication

10.4.1. BasicAuthenticationFilter
Configuration
10.4.2. DigestAuthenticationFilter
Configuration

#### 10.5. Remember-Me Authentication

10.5.1. Overview
10.5.2. Simple Hash-Based Token Approach
10.5.3. Persistent Token Approach
10.5.4. Remember-Me Interfaces and Implementations
TokenBasedRememberMeServices
PersistentTokenBasedRememberMeServices

#### 10.6. Cross Site Request Forgery (CSRF)

10.6.1. CSRF Attacks
10.6.2. Synchronizer Token Pattern
10.6.3. When to use CSRF protection
CSRF protection and JSON
CSRF and Stateless Browser Applications
10.6.4. Using Spring Security CSRF Protection
Use proper HTTP verbs
Configure CSRF Protection
Include the CSRF Token
10.6.5. CSRF Caveats
Timeouts
Logging In
Logging Out
Multipart (file upload)
HiddenHttpMethodFilter
10.6.6. Overriding Defaults

#### 10.7. CORS

#### 10.8. Security HTTP Response Headers

10.8.1. Default Security Headers
Cache Control
Content Type Options
HTTP Strict Transport Security (HSTS)
HTTP Public Key Pinning (HPKP)
X-Frame-Options
X-XSS-Protection
Content Security Policy (CSP)
Referrer Policy
Feature Policy
10.8.2. Custom Headers
Static Headers
Headers Writer
DelegatingRequestMatcherHeaderWriter

#### 10.9. Session Management

10.9.1. SessionManagementFilter
10.9.2. SessionAuthenticationStrategy
10.9.3. Concurrency Control
Querying the SessionRegistry for currently authenticated users and their sessions

#### 10.10. Anonymous Authentication

10.10.1. Overview
10.10.2. Configuration
10.10.3. AuthenticationTrustResolver

#### 10.11. WebSocket Security

10.11.1. WebSocket Configuration
10.11.2. WebSocket Authentication
10.11.3. WebSocket Authorization
WebSocket Authorization Notes
Outbound Messages
10.11.4. Enforcing Same Origin Policy
Why Same Origin?
Spring WebSocket Allowed Origin
Adding CSRF to Stomp Headers
Disable CSRF within WebSockets
10.11.5. Working with SockJS
SockJS & frame-options
SockJS & Relaxing CSRF

### 11. Authorization

#### 11.1. Authorization Architecture

11.1.1. Authorities
11.1.2. Pre-Invocation Handling
The AccessDecisionManager
Voting-Based AccessDecisionManager Implementations
11.1.3. After Invocation Handling
11.1.4. Hierarchical Roles

#### 11.2. Secure Object Implementations

11.2.1. AOP Alliance (MethodInvocation) Security Interceptor
Explicit MethodSecurityInterceptor Configuration
11.2.2. AspectJ (JoinPoint) Security Interceptor

#### 11.3. Expression-Based Access Control

11.3.1. Overview
Common Built-In Expressions
11.3.2. Web Security Expressions
Referring to Beans in Web Security Expressions
Path Variables in Web Security Expressions
11.3.3. Method Security Expressions
@Pre and @Post Annotations
Built-In Expressions

### 12. Additional Topics

#### 12.1. Domain Object Security (ACLs)

12.1.1. Overview
12.1.2. Key Concepts
12.1.3. Getting Started

#### 12.2. Pre-Authentication Scenarios

12.2.1. Pre-Authentication Framework Classes
AbstractPreAuthenticatedProcessingFilter
PreAuthenticatedAuthenticationProvider
Http403ForbiddenEntryPoint
12.2.2. Concrete Implementations
Request-Header Authentication (Siteminder)
Java EE Container Authentication

#### 12.3. LDAP Authentication

12.3.1. Overview
12.3.2. Using LDAP with Spring Security
12.3.3. Configuring an LDAP Server
Using an Embedded Test Server
Using Bind Authentication
Loading Authorities
12.3.4. Implementation Classes
LdapAuthenticator Implementations
Connecting to the LDAP Server
LDAP Search Objects
LdapAuthoritiesPopulator
Spring Bean Configuration
LDAP Attributes and Customized UserDetails
12.3.5. Active Directory Authentication
ActiveDirectoryLdapAuthenticationProvider

#### 12.4. OAuth 2.0 Login — Advanced Configuration

12.4.1. OAuth 2.0 Login Page
12.4.2. Redirection Endpoint
12.4.3. UserInfo Endpoint
Mapping User Authorities
Configuring a Custom OAuth2User
OAuth 2.0 UserService
OpenID Connect 1.0 UserService

### 13. WebClient for Servlet Environments

#### 13.1. WebClient OAuth2 Setup

#### 13.2. Implicit OAuth2AuthorizedClient

#### 13.3. Explicit OAuth2AuthorizedClient

#### 13.4. clientRegistrationId

#### 13.5. JSP Tag Libraries

13.5.1. Declaring the Taglib
13.5.2. The authorize Tag
Disabling Tag Authorization for Testing
13.5.3. The authentication Tag
13.5.4. The accesscontrollist Tag
13.5.5. The csrfInput Tag
13.5.6. The csrfMetaTags Tag

#### 13.6. Java Authentication and Authorization Service (JAAS) Provider

13.6.1. Overview
13.6.2. AbstractJaasAuthenticationProvider
JAAS CallbackHandler
JAAS AuthorityGranter
13.6.3. DefaultJaasAuthenticationProvider
InMemoryConfiguration
DefaultJaasAuthenticationProvider Example Configuration
13.6.4. JaasAuthenticationProvider
13.6.5. Running as a Subject

#### 13.7. CAS Authentication

13.7.1. Overview
13.7.2. How CAS Works
Spring Security and CAS Interaction Sequence
13.7.3. Configuration of CAS Client
Service Ticket Authentication
Single Logout
Authenticating to a Stateless Service with CAS
Proxy Ticket Authentication

#### 13.8. X.509 Authentication

13.8.1. Overview
13.8.2. Adding X.509 Authentication to Your Web Application
13.8.3. Setting up SSL in Tomcat

#### 13.9. Run-As Authentication Replacement

13.9.1. Overview
13.9.2. Configuration

#### 13.10. Spring Security Crypto Module

13.10.1. Introduction
13.10.2. Encryptors
BytesEncryptor
TextEncryptor
13.10.3. Key Generators
BytesKeyGenerator
StringKeyGenerator
13.10.4. Password Encoding

#### 13.11. Concurrency Support

13.11.1. DelegatingSecurityContextRunnable
13.11.2. DelegatingSecurityContextExecutor
13.11.3. Spring Security Concurrency Classes

#### 13.12. Spring MVC Integration

13.12.1. @EnableWebMvcSecurity
13.12.2. MvcRequestMatcher
13.12.3. @AuthenticationPrincipal
13.12.4. Spring MVC Async Integration
13.12.5. Spring MVC and CSRF Integration
Automatic Token Inclusion
Resolving the CsrfToken

### 14. Spring Data Integration

#### 14.1. Spring Data & Spring Security Configuration

#### 14.2. Security Expressions within @Query

### 15. Appendix

#### 15.1. Security Database Schema

15.1.1. User Schema
For Oracle database
Group Authorities
15.1.2. Persistent Login (Remember-Me) Schema
15.1.3. ACL Schema
HyperSQL
PostgreSQL
MySQL and MariaDB
Microsoft SQL Server
Oracle Database

#### 15.2. The Security Namespace

15.2.1. Web Application Security

这些XML配置项目在 代码中一般都有对应的接口

<debug>
<http>
<access-denied-handler>
<cors>
<headers>
<cache-control>
<hsts>
<hpkp>
<pins>
<pin>
<content-security-policy>
<referrer-policy>
<feature-policy>
<frame-options>
<xss-protection>
<content-type-options>
<header>
<anonymous>
<csrf>
<custom-filter>
<expression-handler>
<form-login>
<http-basic>
<http-firewall> Element
<intercept-url>
<jee>

<logout>

+ delete-cookies

    对应HttpSecurity `http.logout().deleteCookies()`。
    
+ invalidate-sesion
+ logout-success-url
+ logout-url
+ success-handler-ref
    
<openid-login>
<attribute-exchange>
<openid-attribute>
<port-mappings>
<port-mapping>
<remember-me>
<request-cache> Element
<session-management>
<concurrency-control>
<x509>
<filter-chain-map>
<filter-chain>
<filter-security-metadata-source>
15.2.2. WebSocket Security
<websocket-message-broker>
<intercept-message>
15.2.3. Authentication Services
<authentication-manager>
<authentication-provider>
<jdbc-user-service>
<password-encoder>
<user-service>
<user>
15.2.4. Method Security
<global-method-security>
<after-invocation-provider>
<pre-post-annotation-handling>
<invocation-attribute-factory>
<post-invocation-advice>
<pre-invocation-advice>
Securing Methods using
<intercept-methods>
<method-security-metadata-source>
<protect>
15.2.5. LDAP Namespace Options
Defining the LDAP Server using the
<ldap-authentication-provider>
<password-compare>
<ldap-user-service>
#### 15.3. Spring Security Dependencies
15.3.1. spring-security-core
15.3.2. spring-security-remoting
15.3.3. spring-security-web
15.3.4. spring-security-ldap
15.3.5. spring-security-config
15.3.6. spring-security-acl
15.3.7. spring-security-cas
15.3.8. spring-security-openid
15.3.9. spring-security-taglibs

#### 15.4. Proxy Server Configuration

#### 15.5. Spring Security FAQ

15.5.1. General Questions
Will Spring Security take care of all my application security requirements?
Why not just use web.xml security?
What Java and Spring Framework versions are required?
I’m new to Spring Security and I need to build an application that supports CAS single sign-on over HTTPS, while allowing Basic authentication locally for certain URLs, authenticating against multiple back end user information sources (LDAP and JDBC). I’ve copied some configuration files I found but it doesn’t work.
15.5.2. Common Problems
When I try to log in, I get an error message that says "Bad Credentials". What’s wrong?
My application goes into an "endless loop" when I try to login, what’s going on?
I get an exception with the message "Access is denied (user is anonymous);". What’s wrong?
Why can I still see a secured page even after I’ve logged out of my application?
I get an exception with the message "An Authentication object was not found in the SecurityContext". What’s wrong?
I can’t get LDAP authentication to work.
Session Management
I’m using Spring Security’s concurrent session control to prevent users from logging in more than once at a time.
Why does the session Id change when I authenticate through Spring Security?
I’m using Tomcat (or some other servlet container) and have enabled HTTPS for my login page, switching back to HTTP afterwards.
I’m not switching between HTTP and HTTPS but my session is still getting lost
I’m trying to use the concurrent session-control support but it won’t let me log back in, even if I’m sure I’ve logged out and haven’t exceeded the allowed sessions.
Spring Security is creating a session somewhere, even though I’ve configured it not to, by setting the create-session attribute to never.
I get a 403 Forbidden when performing a POST
I’m forwarding a request to another URL using the RequestDispatcher, but my security constraints aren’t being applied.
I have added Spring Security’s <global-method-security> element to my application context but if I add security annotations to my Spring MVC controller beans (Struts actions etc.) then they don’t seem to have an effect.
I have a user who has definitely been authenticated, but when I try to access the SecurityContextHolder during some requests, the Authentication is null.
The authorize JSP Tag doesn’t respect my method security annotations when using the URL attribute.
15.5.3. Spring Security Architecture Questions
How do I know which package class X is in?
How do the namespace elements map to conventional bean configurations?
What does "ROLE_" mean and why do I need it on my role names?
How do I know which dependencies to add to my application to work with Spring Security?
What dependencies are needed to run an embedded ApacheDS LDAP server?
What is a UserDetailsService and do I need one?
15.5.4. Common "Howto" Requests
I need to login in with more information than just the username.
How do I apply different intercept-url constraints where only the fragment value of the requested URLs differs (e.g./foo#bar and /foo#blah?
How do I access the user’s IP Address (or other web-request data) in a UserDetailsService?
How do I access the HttpSession from a UserDetailsService?
How do I access the user’s password in a UserDetailsService?
How do I define the secured URLs within an application dynamically?
How do I authenticate against LDAP but load user roles from a database?
I want to modify the property of a bean that is created by the namespace, but there is nothing in the schema to support it.