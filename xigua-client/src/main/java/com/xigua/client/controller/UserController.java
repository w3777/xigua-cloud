package com.xigua.client.controller;

import com.xigua.domain.dto.LoginDTO;
import com.xigua.domain.dto.RegisterUserDTO;
import com.xigua.domain.entity.User;
import com.xigua.domain.result.R;
import com.xigua.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName UserController
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/27 11:48
 */
@Tag(name = "用户接口")
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    @DubboReference
    private UserService userService;

    /**
     * 注册
     * @author wangjinfei
     * @date 2025/4/27 9:53
     * @param dto
     * @return Boolean
     */
    @ApiResponses({@ApiResponse(responseCode = "200", description = "注册成功", content =
            { @Content(mediaType = "application/json") })})
    @Operation(summary = "注册用户")
    @PostMapping("/register")
    public R register(@Validated @RequestBody RegisterUserDTO dto){
        Boolean register = userService.register(dto);
        if(!register){
            return R.fail("注册失败");
        }
        return R.ok(null,"注册成功");
    }

    /**
     * 登录
     * @author wangjinfei
     * @date 2025/5/7 13:40
     * @param loginDTO
     * @return Boolean
     */
    @ApiResponses({@ApiResponse(responseCode = "200", description = "登录成功", content =
            { @Content(mediaType = "application/json") })})
    @Operation(summary = "登录")
    @PostMapping("/login")
    public R login(@Validated @RequestBody LoginDTO loginDTO){
        String token = userService.login(loginDTO);
        if(StringUtils.isEmpty(token)){
            return R.fail("登录失败");
        }
        return R.ok(token,"登录成功");
    }

    /**
     * 获取当前登录用户信息
     * @author wangjinfei
     * @date 2025/5/10 12:52
     * @return User
     */
    @ApiResponses({@ApiResponse(responseCode = "200", description = "获取成功", content =
            { @Content(mediaType = "application/json") })})
    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/getUserInfo")
    public R<User> getUserInfo(){
        User userInfo = userService.getUserInfo();
        return R.ok(userInfo,"获取成功");
    }
}
