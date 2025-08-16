package com.xigua.common.mybatis.menthod;

import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;


/**
 * @ClassName SaveBatchPlus
 * @Description TODO
 * 继承InsertBatchSomeColumn（它的injectMappedStatement方法已经实现批量插入）
 * @Author wangjinfei
 * @Date 2023/12/2 11:24
 */
public class SaveBatchPlus extends InsertBatchSomeColumn {
    public SaveBatchPlus() {
        // 调用父类构造方法 是因为InsertBatchSomeColumn里面的是‘insertBatchSomeColumn’
        // 可以理解在这重写只是改个名字  不然自定义Mapper映射不上
        super("saveBatchPlus", null);
    }
}
