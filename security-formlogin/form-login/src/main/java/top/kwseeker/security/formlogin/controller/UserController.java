package top.kwseeker.security.formlogin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kwseeker.security.formlogin.domain.vo.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/login")
    public String login() {
        return "This is a login page!";
    }

    //用户主页
    @GetMapping("/main-page")
    public String mainPage() {
        return "This is personal main page";
    }

    //查看用户列表
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public List<User> queryUserList() {
        //实际业务中用户列表应该是从缓存或者数据库中读取的
        List<User> users = new ArrayList<>();
        users.add(new User().setName("Arvin").setRoles(new String[]{"ADMIN", "STAFF"}));
        users.add(new User().setName("Bob").setRoles(new String[]{"MANAGER"}));
        users.add(new User().setName("Cindy").setRoles(new String[]{"STAFF"}));
        return users;
    }


}