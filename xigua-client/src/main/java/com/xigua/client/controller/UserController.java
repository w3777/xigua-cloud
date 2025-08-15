package com.xigua.client.controller;

import com.xigua.domain.entity.User;
import com.xigua.domain.result.R;
import com.xigua.domain.vo.UserSearchVO;
import com.xigua.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName UserController
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/27 11:48
 */
@Tag(name = "用户接口")
@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

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

    /**
     * 上传头像
     * @author wangjinfei
     * @date 2025/5/10 20:37
     * @param avatar
     * @return String
     */
    @Operation(summary = "上传头像")
    @PostMapping("/uploadAvatar")
    public R<String> uploadAvatar(@RequestParam("avatar") String avatar){
        userService.uploadAvatar(avatar);
        return R.ok("上传成功");
    }

    /**
     * 更新用户信息
     * @author wangjinfei
     * @date 2025/5/11 20:34
     * @param user
     * @return Boolean
     */
    @Operation(summary = "更新用户信息")
    @PostMapping("/updateUserInfo")
    public R<Boolean> updateUserInfo(@RequestBody User user){
        Boolean updateUserInfo = userService.updateUserInfo(user);
        if(!updateUserInfo){
            return R.fail("更新失败");
        }
        return R.ok(null,"更新成功");
    }

    /**
     * 根据用户名查询用户列表
     * @author wangjinfei
     * @date 2025/5/12 21:18
     * @param username
     * @return List<User>
     */
    @Operation(summary = "根据用户名查询用户列表")
    @PostMapping("/getListByName")
    public R<List<UserSearchVO>> getListByName(@RequestParam("username") String username){
        return R.ok(userService.getListByName(username),"查询成功");
    }
}
