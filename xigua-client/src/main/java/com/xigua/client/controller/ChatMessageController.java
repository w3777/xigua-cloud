package com.xigua.client.controller;

import com.xigua.domain.result.R;
import com.xigua.domain.vo.LastChatVO;
import com.xigua.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName ChatMessageController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/17 19:38
 */
@Tag(name = "消息接口")
@RequiredArgsConstructor
@RequestMapping("/chat/message")
@RestController
public class ChatMessageController {
    @DubboReference
    private ChatMessageService chatMessageService;

    /**
     * 获取最后几条聊天记录
     * @author wangjinfei
     * @date 2025/5/17 19:25
     * @param topUserId 置顶用户聊天记录的用户id
     * @return List<LastChatVO>
     */
    @Operation(summary = "获取最后几条聊天记录")
    @GetMapping("/getLastChat")
    public R<List<LastChatVO>> getLastChat(@RequestParam(value = "topUserId", required = false) String topUserId){
        List<LastChatVO> lastChat = chatMessageService.getLastChat(topUserId);
        return R.ok(lastChat, "获取成功");
    }
}
