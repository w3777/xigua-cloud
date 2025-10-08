package com.xigua.client.controller;

import com.xigua.api.service.BotService;
import com.xigua.domain.dto.BotDTO;
import com.xigua.domain.result.R;
import com.xigua.domain.vo.BotDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName BotController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/1 15:35
 */
@Tag(name = "机器人接口")
@Slf4j
@RequestMapping("/bot")
@RestController
public class BotController {
    @Autowired
    private BotService botService;

    @Operation(summary = "创建机器人")
    @PostMapping("/createBot")
    public R<Boolean> createBot(@RequestBody BotDTO dto) {
        Boolean b = botService.create(dto);
        if (b) {
            return R.ok(b, "创建成功");
        }
        return R.fail("创建失败");
    }

    /**
     * 获取机器人详情
     * @author wangjinfei
     * @date 2025/10/8 18:19
     * @param botId
     * @return BotDetailVO
     */
    @Operation(summary = "获取机器人详情")
    @PostMapping("/getBotDetail")
    public R<BotDetailVO> getBotDetail(@RequestParam("botId") String botId){
        BotDetailVO botDetail = botService.getBotDetail(botId);
        return R.ok(botDetail);
    }
}
