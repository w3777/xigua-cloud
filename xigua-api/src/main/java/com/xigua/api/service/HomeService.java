package com.xigua.api.service;

import com.xigua.domain.vo.HomeCountVO;

/**
 * @ClassName HomeService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/9 10:53
 */
public interface HomeService {

    /**
     * 获取首页统计信息
     * @author wangjinfei
     * @date 2025/8/9 10:56
     * @return HomeCountVO
    */
    HomeCountVO getHomeCount();
}
