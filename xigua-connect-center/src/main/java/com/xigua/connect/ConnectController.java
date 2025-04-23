package com.xigua.connect;

import com.xigua.service.ConnectService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @ClassName ConnectController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/23 20:15
 */
@RequiredArgsConstructor
@RequestMapping("/connect/center")
@RestController
public class ConnectController {
    @DubboReference
    private ConnectService connectService;

    /**
     * 获取在线人员id
     * @author wangjinfei
     * @date 2025/4/23 19:52
     * @return List<String>
     */
    @PostMapping("/getOnlineId")
    public Set<String> getOnlineId(){
        return connectService.getOnlineId();
    }
}
