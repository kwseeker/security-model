# Spring Security 与 Apache Shiro 对比

总结：

Spring Security 适合小数据量（角色少、需要权限控制的方法少）的 Spring 项目，
提供了很多用户认证功能，封装了很多接口方法，易于集成到Spring项目中。  
但是配置文件较多，即使是基于注解角色被编码到配置文件和源文件中，RBAC不明显；
对于系统中用户、角色、权限之间的关系没有可操作的界面，不适合后台权限管理。
如果权限管理复杂，角色很多，需要权限控制的方法很多，则Spring Security几乎是不可用的。

Shiro 则更灵活、简单易懂，可以解决上述Spring Security 的缺陷；
但是 Spring Boot 暂时没有集成 Shiro， 使用Apache Shiro 除了需要自己实现RBAC外，
操作界面也需要自己实现。

Java官方更推荐 Apache Shiro。

针对上述 Spring Security 的问题，能否对 Spring Security 自定义定制解决那些问题？ 