package com.xigua.client.controller;

import com.xigua.domain.dto.RegisterUserDTO;
import com.xigua.domain.result.R;
import com.xigua.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName UserController
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/27 11:48
 */
@Tag(name = "用户相关接口")
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
}
