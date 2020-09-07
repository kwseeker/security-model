package top.kwseeker.security.oauth2.controller;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping("/server")
@Controller
public class AuthController {

    String clientId = null;
    String clientSecret = null;
    String accessTokenUrl = null;
    String userInfoUrl = null;
    String redirectUrl = null;
    String response_type = null;
    String code= null;

    //1) 用户点击第三方登录，应用向认证服务器请求获取授权码
    @RequestMapping("/requestServerCode")
    public String requestServerFirst(HttpServletRequest request, HttpServletResponse response, RedirectAttributes attr)
            throws OAuthSystemException {

        clientId = "clientId";
        clientSecret = "clientSecret";
        accessTokenUrl = "responseCode";
        redirectUrl = "http://localhost:8081/oauthclient01/server/callbackCode";    //用于接收访问令牌的回调接口
        response_type = "code";

        //OAuthClient oAuthClient =new OAuthClient(new URLConnectionClient());
        String requestUrl = null;

        //构建oauth的请求。设置请求服务地址（accessTokenUrl）、clientId、response_type、redirectUrl
        OAuthClientRequest accessTokenRequest = OAuthClientRequest
                .authorizationLocation(accessTokenUrl)
                .setResponseType(response_type)     //???
                .setClientId(clientId)
                .setRedirectURI(redirectUrl)
                .buildQueryMessage();
        requestUrl = accessTokenRequest.getLocationUri();   //用于获取授权码
        System.out.println(requestUrl);

        return "redirect:http://localhost:8082/oauthserver/"+requestUrl ;   //重定向到认证服务器,获取授权码
    }

    //通过此回调接口获取token,然后再重定向到平台用户名密码认证页并附带自身信息
    @RequestMapping("/callbackCode")
    public Object toLogin(HttpServletRequest request) throws OAuthProblemException, OAuthSystemException {

        clientId = "clientId";
        clientSecret = "clientSecret";
        accessTokenUrl="http://localhost:8082/oauthserver/responseAccessToken";
        userInfoUrl = "userInfoUrl";
        redirectUrl = "http://localhost:8081/oauthclient01/server/accessToken";

        HttpServletRequest httpRequest = (HttpServletRequest)request;
        code = httpRequest.getParameter("code");
        System.out.println(code);
        OAuthClient oAuthClient =new OAuthClient(new URLConnectionClient());

        OAuthClientRequest accessTokenRequest = OAuthClientRequest
                .tokenLocation(accessTokenUrl)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setCode(code)
                .setRedirectURI(redirectUrl)
                .buildQueryMessage();
        //去服务端请求access token，并返回响应
        OAuthAccessTokenResponse oAuthResponse =oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.POST);
        //获取服务端返回过来的access token
        String accessToken = oAuthResponse.getAccessToken();
        //查看access token是否过期
        Long expiresIn =oAuthResponse.getExpiresIn();
        System.out.println("客户端/callbackCode方法的token：：："+accessToken);
        System.out.println("-----------客户端/callbackCode--------------------------------------------------------------------------------");
        return "redirect:http://localhost:8081/oauthclient01/server/accessToken?accessToken="+accessToken;
    }

    //通过此回调接口获取accessToken
    @RequestMapping("/accessToken")
    public ModelAndView accessToken(String accessToken) throws OAuthSystemException, OAuthProblemException {
        System.out.println("---------客户端/accessToken----------------------------------------------------------------------------------");
        userInfoUrl = "http://localhost:8082/oauthserver/userInfo";
        System.out.println("accessToken");
        OAuthClient oAuthClient =new OAuthClient(new URLConnectionClient());

        OAuthClientRequest userInfoRequest =new OAuthBearerClientRequest(userInfoUrl)
                .setAccessToken(accessToken).buildQueryMessage();
        OAuthResourceResponse resourceResponse =oAuthClient.resource(userInfoRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
        String username = resourceResponse.getBody();
        System.out.println(username);
        ModelAndView modelAndView =new ModelAndView("usernamePage");
        modelAndView.addObject("username",username);
        System.out.println("---------客户端/accessToken----------------------------------------------------------------------------------");
        return modelAndView;
    }
}
