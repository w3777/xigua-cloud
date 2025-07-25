package com.xigua.center.controller;

import com.xigua.domain.result.R;
import com.xigua.api.service.CenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @ClassName CenterController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/23 20:15
 */
@Tag(name = "长连接中心接口")
@RequiredArgsConstructor
@RequestMapping("/center")
@RestController
public class CenterController {
    @DubboReference
    private CenterService centerService;

    /**
     * 获取在线人员id
     * @author wangjinfei
     * @date 2025/4/23 19:52
     * @return List<String>
     */
    @ApiResponses({@ApiResponse(responseCode = "200", description = "查询成功", content =
            { @Content(mediaType = "application/json") })})
    @Operation(summary = "获取在线人员id")
    @PostMapping("/getOnlineId")
    public R<Set<String>> getOnlineId(){
        return R.ok(centerService.getOnlineId(),"查询成功");
    }
}
