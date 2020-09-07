# Web应用鉴授权框架

参考：  
[Spring Security 与 OAuth2](https://www.jianshu.com/p/68f22f9a00ee)  
[OAuth 2.0](https://oauth.net/2/)  
[The OAuth 2.0 Authorization Framework
](https://tools.ietf.org/html/rfc6749) (76 pages)

## <font color="blue">1 OAuth2</font>

OAuth（开放授权）是一个`开放标准`，允许用户(比如QQ用户)授权第三方移动应用(比如某个网课应用)访问他们存储在另外的服务提供者（腾讯QQ用户服务）上的信息，
而不需要将用户名和密码提供给第三方移动应用或分享他们数据的所有内容，OAuth2.0是OAuth协议的延续版本，但不向后兼容OAuth 1.0。

OAuth2 是做第三方（这里第三方指第三方企业或者服务集群中鉴授权服务器）授权的。

根据上面的介绍 OAuth2 应该是适合作为SSO实现方案的。


名词概念：

资源服务器（resource-server）：提供用户信息资源(姓名、年龄等信息)的服务  
资源所有者（resource-owner）：用户  
认证服务器（authorization-server）：对用户认证的服务  
第三方应用：  
用户代理（user-agent）：浏览器


### 1.1 OAuth2 的工作流程

![OAuth2第三方登录](https://upload-images.jianshu.io/upload_images/9434708-ccd365e0a1ae9d7e.png?imageMogr2/auto-orient/)

![OAuth2第三方登录](https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=2005963405,1223119627&fm=173&app=49&f=JPEG?w=640&h=446&s=449A4C3211DE61C8547140DE0200C0B2)

### 1.2 OAuth2 的四种授权模式

以QQ用户登录新浪微博为例：

#### 1.2.1 授权码模式（authorization code）

![](https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=3360499339,3703041730&fm=173&app=49&f=JPEG?w=640&h=443&s=E6F1E27E110A454B4E5454CE0000D0B3)

这里client应该是新浪微博服务器，user-agent 是浏览器。

A）用户使用浏览器打开新浪微博登录页面，用户点击QQ登录图标，新浪微博向 QQ认证服务器 发送登录请求，
    QQ认证服务器返回QQ网页登录重定向地址；  
B）新浪微博将显示QQ网页登录页，用户输入用户名密码，点击授权登录，前往QQ认证服务器认证用户名密码；  
C）认证成功返回一个授权码新浪微博获取授权码；  
D）使用授权码和重定向地址请求QQ认证服务器；  
E）QQ认证服务器返回访问token和更新token（用于在访问token过期后获取新的访问token）；  
F）新浪微博使用访问token请求QQ资源服务器获取用户信息资源；  
G）新浪微博使用QQ用户信息刷新页面，显示用户头像，曾现登录状态。   
     
++授权码为何不能作为访问token？为何还要去获取访问token？++   
因为这种模式中AccessToken不会经过浏览器或移动端的App，而是新浪微博的后台服务器直接从QQ认证服务端去交换，
这样就最大限度的减小了AccessToken泄漏的风险。

除了授权码模式，其他都有使用范围限制。  
安全级别: 授权码最高、客户端模式最低、简化模式和密码模式居中；  
简化模式危险在浏览器或APP，密码模式风险在第三方服务器。  

#### 1.2.2 简化模式（implicit）

![](https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=1094957589,2534791990&fm=173&app=49&f=JPEG?w=640&h=547&s=BD0A777E190EC44D1C75F5CE0000C0B3)

好像就是上面提的那个问题说的不安全的模型，但是由添加了个脚本解密AccessToken的过程；
用于提升安全性（虽然浏览器可以拿到包含AccessToken的Fragment,但是没法解密出来，
还要再请求一次获取解密脚本）。

一般简化模式用于没有服务器端的第三方单页面应用，
因为第三方单页面应用没有服务器端（新浪微博后台）无法使用授权码模式。

#### 1.2.3 密码模式（resource owner password credentials）

![](https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=890485039,2872359714&fm=173&app=49&f=JPEG?w=640&h=325&s=B55A657F3D1A4C4D18DD89DB0000C0B2)

授权码模式是第三方服务器无法获取用户名和密码，而密码模式是直接将用户名密码给到第三方服务器，
让服务器自行去获取AccessToken。
但是这样会把用户名密码泄漏给第三方服务供应商。

所以密码模式应该适合企业内部不同服务之间的授权。

#### 1.2.4 客户端模式（client credentials）

![](https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=2824142023,4194952714&fm=173&app=49&f=JPEG?w=640&h=146&s=E6F3E27ECFE64D2010FDA1DA000080B1)

client向授权服务器发送自己的身份信息，并请求AccessToken
确认client信息无误后，将AccessToken发送给client。

这种模式是最方便但最不安全的模式。因此这就要求我们对client完全的信任，而client本身也是安全的。因此这种模式一般用来提供给我们完全信任的服务器端服务。在这个过程中不需要用户的参与。

### 1.3 具体实现

关于Spring Security使用OAuth2参考：
[Spring Security Reference](https://docs.spring.io/spring-security/site/docs/5.1.6.RELEASE/reference/htmlsingle)

#### 1.3.1 创建 Spring Security Java Configuration

创建 Spring


## <font color="blue">2 Shiro</font>
