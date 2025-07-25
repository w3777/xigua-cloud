package com.xigua.sso.controller;

import com.xigua.common.core.util.UserContext;
import com.xigua.domain.dto.LoginDTO;
import com.xigua.domain.dto.RegisterUserDTO;
import com.xigua.domain.result.R;
import com.xigua.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName AuthController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/12 21:20
 */
@Tag(name = "认证接口")
@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    @Autowired
    private final AuthService authService;

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
        return R.ok(authService.login(loginDTO),"登录成功");
    }

    /**
     * 获取一次性ticket
     * @author wangjinfei
     * @date 2025/6/14 16:07
     * @return String
     */
    @Operation(summary = "获取一次性ticket")
    @GetMapping("/getTicket")
    public R getTicket(){
        String userId = UserContext.get().getUserId();
        return R.ok(authService.createTicket(userId));
    }

    /**
     * ticket兑换token
     * @author wangjinfei
     * @date 2025/6/14 21:46
     * @param ticket
     * @return String
     */
    @Operation(summary = "ticket兑换token")
    @GetMapping("/redeemToken")
    public R redeemToken(@RequestParam("ticket") String ticket){
        return R.ok(authService.redeemToken(ticket));
    }
}
