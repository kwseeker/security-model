## Spring Security

**常见问题与框架实现：**  
1) 认证流程及不同认证方式认证流程的差异
2) 指定认证信息的存储方式
3) 添加自定义认证方式
4) 认证成功之后是如何传入session
5) 认证成功后后续的请求如何获取认证信息
6) 自定义登录界面代替Security默认的登录界面
7) 拓展认证成功或失败处理器功能（返回更详细的信息）
8) RememberMe的实现原理
9) 图形验证码的实现（数字图片、拼图等）

#### 问题记录
+ **security-browser模块中的Security配置无效**  
    经过一天的排查发现是因为security-demo模块中Main类路径太多的问题，
    模块化的项目好像只能包含在一级域名反写的目录下面（top.kwseeker），
    如果后面还有目录如 top.kwseeker.demo ， 则会出现Security配置无效的问题，
    报告 "Application context not configured for this file" 这个警告，尚不知为何会这样。
    
#### Spring Boot 基础补充

+ **@JsonView隐藏敏感数据**  
    没有被@JsonView修饰的字段默认为隐藏。

+ **使用Hibernate Validator校验数据以及自定义约束**  
    校验数据是否符合约束条件，如果不符合约束条件并不会报错，只会打印提示信息。

+ **请求异步处理的三种方法**  
    https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/web.html#mvc-ann-async
    
    - **Callable<>**  
        Callable异步请求的处理流程：
        1) 控制器先返回一个Callable对象
        2) Spring MVC开始进行异步处理，并把该Callable对象提交给另一个独立线程的执行器TaskExecutor处理
        3) DispatcherServlet和所有过滤器都退出Servlet容器线程，但此时方法的响应对象仍未返回
        4) Callable对象最终产生一个返回结果，此时Spring MVC会重新把请求分派回Servlet容器，恢复处理
        5) DispatcherServlet再次被调用，恢复对Callable异步处理所返回结果的处理
        
    - **DeferredResult<>**   
        https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/context/request/async/DeferredResult.html
        DeferredResult异步请求的处理流程：
        1) 控制器先返回一个DeferredResult对象，并把它存取在内存（队列或列表等）中以便存取
        2) Spring MVC开始进行异步处理
        3) DispatcherServlet和所有过滤器都退出Servlet容器线程，但此时方法的响应对象仍未返回
        4) 由处理该请求的线程对 DeferredResult进行设值，然后Spring MVC会重新把请求分派回Servlet容器，恢复处理
        5) DispatcherServlet再次被调用，恢复对该异步返回结果的处理
        
    - **@EnableAsync @Async**
    
    前两者可以返回最终处理的结果返回给前端；最后一种方式使用最方便，但是无法返回后台处理结果。
    
+ **ApplicationListener<>**  
    https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#context-functionality-events
    
    - 内置事件
    ContextRefreshedEvent
    ContextStartedEvent
    ContextStoppedEvent
    ContextClosedEvent
    RequestHandledEvent
    
    - 自定义事件
    - 基于注解的事件处理
    - 异步监听器 @Async
    - 有先后顺序的监听器 @Order
    - 通用事件
    
+ **添加自定义配置项**

#### Spring Security 认证流程调试
![image](https://img-blog.csdn.net/2018042723003377?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3MTQyMzQ2/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

Filter画的并不准确，各种过滤器其实是并列的关系，每种认证对应一种Filter，实际认证中由AuthenticationManager进行匹配需要执行哪个过滤器；  
程序员也可以根据业务需要定义自己的过滤器以及认证方式。

#### RememberMe 实现原理与应用
![](https://img-blog.csdn.net/20180427214640450?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3MTQyMzQ2/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

