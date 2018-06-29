package top.kwseeker.webmod.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import top.kwseeker.webmod.exception.UserNotExistException;

import java.util.HashMap;
import java.util.Map;

/**
 * 注解@ControllerAdvice是一个@Component，用于定义@ExceptionHandler，@InitBinder和@ModelAttribute方法，适用于所有使用@RequestMapping方法。
 * 在Spring4中， @ControllerAdvice通过annotations(), basePackageClasses(), basePackages()方法定制用于选择控制器子集。
 * 如果单使用@ExceptionHandler，只能在当前Controller中处理异常。但当配合@ControllerAdvice一起使用的时候，就可以摆脱那个限制了。
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    /**
     * UserNotExistException异常处理器
     * @param ex
     * @return
     */
    @ExceptionHandler(UserNotExistException.class)  //指定处理哪个异常，这个处理函数会覆盖默认的处理函数
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   //返回给前端的状态码
    public Map<String, Object> handleUserNotExistException(UserNotExistException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", ex.getId());
        result.put("message", ex.getMessage());
        return  result;                             //将异常信息返回给前端
    }

    /**
     * 还可以指定其他异常的处理器
     */

}
