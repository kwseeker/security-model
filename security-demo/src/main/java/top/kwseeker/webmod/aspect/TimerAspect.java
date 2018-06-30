package top.kwseeker.webmod.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;

import java.util.Date;

@Aspect
@Component
public class TimerAspect {

    @Around("execution(public * top.kwseeker.controller.UserController.*(..))")
    public Object handleControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("TimeAspect start");

        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            System.out.println("arg is "+arg);
        }
        long start = new Date().getTime();

        Object object = pjp.proceed();  //这里执行controller方法

        System.out.println("TimeAspect 耗时:"+ (new Date().getTime() - start));
        System.out.println("TimeAspect end");

        return object;
    }
}
