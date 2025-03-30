package com.xigua.controller;

import com.xigua.domain.dto.LoginDTO;
import com.xigua.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/3/18 22:08
 */
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    @DubboReference
    private UserService userService;

    @PostMapping("/testDubbo")
    public void testDubbo(){
        userService.testDubbo();
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO loginDTO){
        return userService.login(loginDTO);
    }

    @PostMapping("/testToken")
    public void testToken() {
        userService.testToken();
    }


}
