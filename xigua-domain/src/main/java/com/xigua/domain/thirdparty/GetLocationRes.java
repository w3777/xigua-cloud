package com.xigua.domain.thirdparty;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName GetLocationRes
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/11 14:22
 */
@Data
public class GetLocationRes implements Serializable {

    private static final long serialVersionUID = 1L;
    // 国家/地区
    private String Country;
    // 省份区域，部分可能为空
    private String Province;
    // 	城市，部分可能为空
    private String City;
    // 运营商，部分可能为空
    private String Isp;
}
