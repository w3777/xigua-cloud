package com.xigua.client.controller;

import com.xigua.api.service.FriendRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName FriendRequestController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/13 21:47
 */
@Tag(name = "好友请求接口")
@RequiredArgsConstructor
@RequestMapping("/friend/request")
@RestController
public class FriendRequestController {
    @DubboReference
    private FriendRequestService friendRequestService;
}
