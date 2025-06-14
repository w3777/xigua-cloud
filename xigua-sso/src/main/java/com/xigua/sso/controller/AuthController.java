package com.xigua.sso.controller;

import com.xigua.domain.dto.LoginDTO;
import com.xigua.domain.dto.RegisterUserDTO;
import com.xigua.domain.result.R;
import com.xigua.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName AuthController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/12 21:20
 */
@Tag(name = "认证接口")
@RequestMapping("/auth")
@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

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
        Boolean register = authService.register(dto);
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
        String token = authService.login(loginDTO);
        if(StringUtils.isEmpty(token)){
            return R.fail("登录失败");
        }
        return R.ok(token,"登录成功");
    }
}
