package top.kwseeker.security.formlogin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员访问接口
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    /**
     * 为系统添加新的权限分组
     * @return
     */
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/add-permission-group")
    public String addPermissionGroup() {
        log.info("添加新的权限分组");
        //实际业务中这里添加权限分组
        return "success";
    }
}
