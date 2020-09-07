package top.kwseeker.security.oauth2.controller;

import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
public class AuthorizeController{

    @Autowired
    private UserService userService;

    //返回授权码给ISV
    @RequestMapping("/responseCode")
    public Object toShowUser(Model model, HttpServletRequest request)
            throws URISyntaxException, OAuthSystemException, OAuthProblemException {
        System.out.println("----------服务端/responseCode--------------------------------------------------------------");
        //构建OAuth授权请求
        OAuthAuthzRequest oauthRequest =new OAuthAuthzRequest(request);
         /*oauthRequest.getClientId();
         oauthRequest.getResponseType();
         oauthRequest.getRedirectURI();
         System.out.println(oauthRequest.getClientId());
         System.out.println(oauthRequest.getResponseType());
         System.out.println(oauthRequest.getRedirectURI());*/

        if(oauthRequest.getClientId()!=null && oauthRequest.getClientId()!="") {
            //设置授权码
            String authorizationCode ="authorizationCode";
            //利用oauth授权请求设置responseType，目前仅支持CODE，另外还有TOKEN
            String responseType =oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
            //进行OAuth响应构建
            OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
                    OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND);
            //设置授权码
            builder.setCode(authorizationCode);
            //得到到客户端重定向地址
            String redirectURI =oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);        //这个回调地址ISV获取授权码的地址

            //构建响应
            final OAuthResponse response =builder.location(redirectURI).buildQueryMessage();
            System.out.println("服务端/responseCode内，返回的回调路径："+response.getLocationUri());
            System.out.println("----------服务端/responseCode--------------------------------------------------------------");
            String responseUri =response.getLocationUri();

            //根据OAuthResponse返回ResponseEntity响应
            HttpHeaders headers =new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));

            return "redirect:" + responseUri;
        }
    }
}
