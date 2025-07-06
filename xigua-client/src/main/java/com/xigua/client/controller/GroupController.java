package com.xigua.client.controller;

import com.xigua.domain.dto.GroupDTO;
import com.xigua.domain.result.R;
import com.xigua.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName GroupController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/6 15:30
 */
@Tag(name = "群组接口")
@RequiredArgsConstructor
@RequestMapping("/group")
@RestController
public class GroupController {
    @DubboReference
    private GroupService groupService;

    /**
     * 创建群组
     * @author wangjinfei
     * @date 2025/7/6 15:32
     * @param dto
     * @return R<GetLocationRes>
    */
    @Operation(summary = "创建群组")
    @PostMapping("/createGroup")
    public R<String> createGroup(@RequestBody GroupDTO dto){
        Boolean b = groupService.createGroup(dto);
        if (!b){
            return R.fail("创建失败");
        }
        return R.ok("创建成功");
    }
}
