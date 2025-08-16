package com.xigua.center.controller;

import com.xigua.api.service.SyncMySqlService;
import com.xigua.domain.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName SyncMySqlController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/16 17:36
 */
@Tag(name = "同步mysql接口")
@RestController
@RequestMapping("/sync/mysql")
public class SyncMySqlController {
    @Autowired
    private SyncMySqlService syncMySqlService;

    /**
     * 同步消息已读
     * @author wangjinfei
     * @date 2025/8/16 16:46
     * @return Boolean
     */
    @Operation(summary = "同步消息已读")
    @PostMapping("/syncMessageRead")
    public R<Boolean> syncMessageRead(){
        return R.ok(syncMySqlService.syncMessageRead());
    }
}
