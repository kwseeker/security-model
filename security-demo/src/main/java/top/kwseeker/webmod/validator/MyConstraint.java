package top.kwseeker.webmod.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//自定义校验约束注解
//参考官方校验约束注解写法
// For more detail info: https://docs.jboss.org/hibernate/validator/4.2/reference/zh-CN/html/validator-customconstraints.html#validator-customconstraints-validator
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MyConstraintValidator.class)
public @interface MyConstraint {

    //校验失败时的消息
    String message() default "To show the error message ..";
    //groups 属性, 用于指定这个约束条件属于哪(些)个校验组
    Class<?>[] groups() default {};
    //Bean Validation API 的使用者可以通过此属性来给约束条件指定严重级别
    Class<? extends Payload>[] payload() default {};

}
