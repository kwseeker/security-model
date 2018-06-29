package top.kwseeker.controller;


import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import top.kwseeker.dto.User;
import top.kwseeker.dto.UserQueryCondition;
import top.kwseeker.webmod.exception.UserNotExistException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController //如果方法返回是一个对象或对象的容器，会自动转换成JSON的格式
@RequestMapping("/user")
public class UserController {

    //注册用户
    @PostMapping("/register")
    public void register(User user, HttpServletRequest request) {
        //以用户名作为用户唯一标识
        String userId = user.getUsername();
    }

    //创建用户
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        //TODO: Id号自增
        System.out.println(user.getId() + "\n"
                + user.getUsername() + "\n"
                + user.getPassword() + "\n"
                + user.getBirthday()
        );
        // To do store operation
        return user;
    }

    //查询用户列表
    @GetMapping
    @JsonView(User.UserSimpleView.class)    //只显示UserSimpleView注释的接口获取的数据
    //public List<User> query(@RequestParam String username, @PageableDefault(page = 2, size = 5, sort = "username, desc") Pageable pageable) {
    //public List<User> query(@RequestParam(name = "username", required = false, defaultValue = "kwseeker") String un,
    //                        @PageableDefault(page = 2, size = 5, sort = "username, desc") Pageable pageable) {
    public List<User> query(UserQueryCondition condition, @PageableDefault(page = 2, size = 2, sort = "username, asc") Pageable pageable) {

        System.out.println(ReflectionToStringBuilder.toString(condition, ToStringStyle.MULTI_LINE_STYLE));  //对象转化为String, 以多行显示
        System.out.println("page number: " + pageable.getPageSize() + "\n"
                + "page size: " + pageable.getPageNumber() + "\n"
                + "page sort: " + pageable.getSort());

        List<User> userList = new ArrayList<>();
        userList.add(new User("Arvin", "123456"));
        userList.add(new User("Bob", "123456"));
        userList.add(new User("Candy", "123456"));
        userList.add(new User("Danny", "123456"));

        return userList;
    }

    //查询当前用户

    //绑定用户

    //获取指定用户的信息（通过用户id）
    @GetMapping(value = "/{id:\\d+}")   //正则表达式约束id值为多位整数
    @JsonView(User.UserSimpleView.class)
    public User getInfo(@PathVariable String id) {

        if( true ) {     //TODO: 查数据库判断id是否存在
            throw new UserNotExistException(id);
        }
        return null;

//        User user = new User();
//        user.setId("1");
//        user.setUsername("Arvin");
//        user.setPassword("123458");
//        user.setBirthday(new Date());
//        return user;
    }

    //更新用户信息数据
    @PutMapping("/{id:\\d+}")
    public User updateInfo(@Valid @RequestBody User user, BindingResult errors) {   //BindingResult获取Validator错误消息
        if(errors.hasErrors()) {
            errors.getAllErrors().stream().forEach(error -> {
                FieldError fieldError = (FieldError) error;
                String message = fieldError.getField() + " " + fieldError.getDefaultMessage();
                System.out.println(message);
            });
        }

        System.out.println(ReflectionToStringBuilder.toString(user, ToStringStyle.MULTI_LINE_STYLE));
        //更新用户数据的操作
        //...

        return user;
    }

    //删除用户
    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable String id) {
        System.out.println("User id to delete: " + id);
        //删除用户操作
        //...
    }

}
