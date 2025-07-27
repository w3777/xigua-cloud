package com.xigua.client.controller;

import com.xigua.api.service.ContactService;
import com.xigua.domain.result.R;
import com.xigua.domain.vo.ContactCountVO;
import com.xigua.domain.vo.FriendVO;
import com.xigua.domain.vo.GroupVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName ContactController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/27 9:58
 */
@Tag(name = "联系人接口")
@RequestMapping("/contact")
@RestController
public class ContactController {
    @Autowired
    private ContactService contactService;


    /**
     * 获取联系人数量
     * @author wangjinfei
     * @date 2025/7/27 9:59
     * @return R<ContactCountVO>
    */
    @Operation(summary = "获取联系人数量")
    @GetMapping("/getContactCount")
    public R<ContactCountVO> getContactCount(){
        ContactCountVO contactCount = contactService.getContactCount();
        return R.ok(contactCount);
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
        return R.ok(contactService.getFriendList());
    }



    /**
     * 获取群列表
     * @author wangjinfei
     * @date 2025/7/27 11:44
     * @return List<GroupVO>
     */
    @Operation(summary = "获取群列表")
    @GetMapping("/getGroupList")
    public R<List<GroupVO>> getGroupList(){
        return R.ok(contactService.getGroupList());
    }

}
