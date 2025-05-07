package com.xigua.client.controller;

import com.xigua.domain.result.R;
import com.xigua.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName EmailController
 * @Description
 * @Author wangjinfei
 * @Date 2025/5/7 14:52
 */
@Tag(name = "邮箱接口")
@RequiredArgsConstructor
@RequestMapping("/email")
@RestController
public class EmailController {
    @DubboReference
    private EmailService emailService;

    @Operation(summary = "发送邮箱")
    @PostMapping("/send")
    public R send(@RequestParam("email") String email){
        Boolean flag = emailService.send(email, "", "");
        if(flag){
            return R.ok("发送成功");
        }
        return R.fail("发送失败");
    }
}
