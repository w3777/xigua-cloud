package com.xigua.client.controller;

import com.xigua.domain.dto.FriendVerifyDTO;
import com.xigua.domain.dto.sendFriendRequestDTO;
import com.xigua.domain.result.R;
import com.xigua.domain.vo.FriendDetailVO;
import com.xigua.domain.vo.FriendVO;
import com.xigua.domain.vo.FriendRequestVO;
import com.xigua.service.FriendRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName FriendRelationController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/13 23:07
 */
@Tag(name = "好友关系接口")
@RequiredArgsConstructor
@RequestMapping("/friend/relation")
@RestController
public class FriendRelationController {
    @DubboReference
    private FriendRelationService friendRelationService;

    /**
     * 发送好友请求
     * @author wangjinfei
     * @date 2025/5/13 21:26
     * @param dto
     * @return Boolean
     */
    @Operation(summary = "发送好友请求")
    @PostMapping("/sendFriendRequest")
    public R<Boolean> sendFriendRequest(@RequestBody sendFriendRequestDTO dto){
        Boolean sendFriendRequest = friendRelationService.sendFriendRequest(dto);
        if(!sendFriendRequest){
            return R.fail("发送失败");
        }
        return R.ok(null,"发送成功");
    }

    /**
     * 好友请求(包含已发送和已接收)
     * @author wangjinfei
     * @date 2025/5/13 23:10
     * @return ReceiveFriendRequestVO
     */
    @Operation(summary = "好友请求(包含已发送和已接收)")
    @GetMapping("/friendRequest")
    public R<List<FriendRequestVO>> friendRequest(){
        List<FriendRequestVO> receiveFriendRequest = friendRelationService.friendRequest();
        return R.ok(receiveFriendRequest);
    }

    /**
     * 获取好友列表
     * @author wangjinfei
     * @date 2025/5/14 20:56
     * @return List<FriendVO>
     */
    @Operation(summary = "获取好友列表")
    @GetMapping("/getFriendList")
    public R<List<FriendVO>> getFriendList(){
        return R.ok(friendRelationService.getFriendList());
    }

    /**
     * 好友验证
     * @author wangjinfei
     * @date 2025/5/14 22:00
     * @param dto
     * @return Boolean
     */
    @Operation(summary = "好友验证")
    @PostMapping("/friendVerify")
    public R<Boolean> friendVerify(@RequestBody FriendVerifyDTO dto){
        Boolean b = friendRelationService.friendVerify(dto);
        if(!b){
            return R.fail("验证失败");
        }
        return R.ok(null,"验证成功");
    }

    /**
     * 好友详情
     * @author wangjinfei
     * @date 2025/5/17 12:06
     * @param friendId
     * @return FriendDetailVO
     */
    @Operation(summary = "好友详情")
    @GetMapping("/getFriendDetail")
    public R<FriendDetailVO> getFriendDetail(@RequestParam("friendId") String friendId){
        return R.ok(friendRelationService.getFriendDetail(friendId), "查询成功");
    }

    /**
     * 所有好友关系存入redis
     * @author wangjinfei
     * @date 2025/6/2 20:58
     */
    @Operation(summary = "所有好友关系存入redis")
    @GetMapping("/allFriendRelationToRedis")
    public R<Boolean> allFriendRelationToRedis(){
        friendRelationService.allFriendRelationToRedis();
        return R.ok(true, "缓存成功");
    }
}
