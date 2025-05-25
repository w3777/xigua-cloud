package com.xigua.center.controller;

import com.xigua.domain.dto.GetFriendLastMesDTO;
import com.xigua.domain.dto.GetHistoryMes;
import com.xigua.domain.result.BasePageVO;
import com.xigua.domain.result.R;
import com.xigua.domain.vo.ChatMessageVO;
import com.xigua.domain.vo.LastChatVO;
import com.xigua.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

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
     * 获取好友最后一条消息
     * @author wangjinfei
     * @date 2025/5/17 19:25
     * @param dto
     * @return List<LastChatVO>
     */
    @Operation(summary = "获取好友最后一条消息")
    @PostMapping("/getFriendLastMes")
    public R<BasePageVO<LastChatVO>> getFriendLastMes(@RequestBody GetFriendLastMesDTO dto){
        BasePageVO<LastChatVO> lastChat = chatMessageService.getFriendLastMes(dto);
        return R.ok(lastChat, "获取成功");
    }

    /**
     * 分页获取好友历史消息
     * @author wangjinfei
     * @date 2025/5/25 10:16
     * @param dto
     * @return BasePageVO<ChatMessageVO>
     */
    @Operation(summary = "分页获取好友历史消息")
    @PostMapping("/getHistoryMes")
    public R<BasePageVO<ChatMessageVO>> getHistoryMes(@RequestBody GetHistoryMes dto){
        return R.ok(chatMessageService.getHistoryMes(dto), "查询成功");
    }
}
