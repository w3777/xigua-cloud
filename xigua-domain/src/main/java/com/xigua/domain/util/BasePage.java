package com.xigua.domain.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xigua.domain.result.BasePageVO;

import java.util.List;

/**
 * @ClassName BasePage
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/19 21:30
 */
public class BasePage<R> {
    /**
     * 获取分页结果
     * @author wangjinfei
     * @date 2025/5/19 21:37
     * @param page
     * @param list
     * @return BasePageVO<R>
    */
    public static <R> BasePageVO<R> getResult(Page<R> page, List<R> list){
        BasePageVO<R> result = new BasePageVO<>();
        result.setPageNum((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setRows(list);
        result.setTotalPage(page.getPages());
        result.setTotalCount(page.getTotal());
        return result;
    }
}
