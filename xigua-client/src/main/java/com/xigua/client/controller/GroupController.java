package com.xigua.client.controller;

import com.xigua.common.core.exception.BusinessException;
import com.xigua.domain.dto.AddGroup2RedisDTO;
import com.xigua.domain.dto.GroupDTO;
import com.xigua.domain.result.R;
import com.xigua.api.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName GroupController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/6 15:30
 */
@Tag(name = "群组接口")
@Slf4j
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
        Boolean b = null;
        try {
            b = groupService.createGroup(dto);
        } catch (BusinessException e){
            log.error("创建群组失败：{}", e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }catch (Exception e) {
            log.error("创建群组失败：{}", e.getMessage(), e);
            throw new BusinessException("创建群组失败");
        }
        if (!b){
            return R.fail("创建失败");
        }
        return R.ok("创建成功");
    }

    /**
     * 群组添加到缓存
     * @author wangjinfei
     * @date 2025/7/27 11:31
     * @param dto
     * @return R<Boolean>
     */
    @Operation(summary = "群组添加到缓存")
    @PostMapping("/addGroup2Redis")
    public R<Boolean> addGroup2Redis(@RequestBody AddGroup2RedisDTO dto){
        return R.ok(groupService.addGroup2Redis(dto.getGroupIds()));
    }
}
