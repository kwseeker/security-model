## 使用 Redis 统一管理 Session

#### Window 下安装 Redis 服务
啰嗦一句：做开发尤其是后端开发还是用Linux好。

Redis本身对Windows的支持不友好，虽然微软团队和其他人有专门做Redis对Windows的支持，但是版本老旧2.6.x左右，
现在稳定版已经4.0.10了，而且Spring Boot貌似已经不支持这么老的Redis了（报各种问题）。

所以还是选用最新版本，通过docker容器部署的方法。

**使用Redis统一管理Session的特点**  
常用于服务器内部集群  
+ 可以使多个子服务共享一个Session, 避免同一用户同一浏览器访问不同服务器上不同子服务时重复认证的问题；  
+ 子服务器出现问题或宕机不会影响Session（不会删除Session的数据）
+ 不断使用登录的token访问页面，会自动更新超时时间（以分钟为单位），超时之后会删除 Session。

TODO: Spring Boot 对 session redis 这种存储方式，Spring 内部是怎样实现上面的处理逻辑的？  

**Redis服务搭建步骤**：  
1. docker 需要 Linux内核， 所以要么在Window下装个 VirtualBox虚拟机，
   要么使用 Docker for Windows(懒得装虚拟机了，就用了Docker for Window)。  
2. 启动 Docker for Windows, 拉取 Redis镜像   
   具体使用,参考 https://hub.docker.com/_/redis/   
   （简单方式打开，持久化存储方式打开，连接方式，使用自定义的redis.conf配置文件等）
    ```
    $ docker pull redis:4.0.10-alpine
    ```
3. 启动 Docker 中的 Redis镜像  
    查看默认启动配置：
    https://github.com/docker-library/redis/blob/87e80558fb828de53399e341af4c8a05c3e2d631/4.0/alpine/Dockerfile
   
    ```
    # 比较重要的默认配置：
    # RUN addgroup -S redis && adduser -S -G redis redis
    # RUN mkdir /data && chown redis:redis /data
    # VOLUME /data
    # WORKDIR /data
    # EXPOSE 6379       # 将docker内部6379端口暴露出去
    
    # 下面提供两种启动 Redis 容器方法：
    $ docker run --name session-redis -d -p 6379:6379 redis:4.0.10-alpine      # 不会持久化存储
    $ docker run --name session-redis -d -p 6379:6379 redis:4.0.10-alpine redis-server --appendonly yes   # 会持久化存储
    ```

4. docker for window 外部连接与测试 Redis 容器状态
    + 使用工具连接测试(127.0.0.1:6379)  
    老版本的客户端还是可以用的。  
    Redis Client  
    RedisDesktopManager  
5. 项目代码添加 Redis支持和配合 重新启动 security-model
    Redis Client查看Redis所有key-value
    ``` 
    redis 127.0.0.1:6379> keys *
    (empty list or set)
    ```
    执行会创建 Session 的操作如登录，重新查看
    ```
    redis 127.0.0.1:6379> keys *
    1) "spring:session:sessions:expires:a20773af-a8f9-422c-a347-f00d50da9c82"
    2) "spring:session:sessions:6da3e7bc-c056-4df8-b704-a74a58e7fd27"
    3) "spring:session:expirations:1531206000000"
    4) "spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:Arvin"
    5) "spring:session:expirations:1531206300000"
    6) "spring:session:sessions:a20773af-a8f9-422c-a347-f00d50da9c82"
    ```
    