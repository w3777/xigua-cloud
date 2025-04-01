package com.xigua.common.sequence.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zcf
 * <p>
 * Snowflake 发号器属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "xigua.sequence.snowflake")
public class SequenceSnowflakeProperties extends BaseSequenceProperties {

	/**
	 * 数据中心ID，值的范围在[0,31]之间，一般可以设置机房的IDC[必选]
	 */
	private long datacenterId;

	/**
	 * 工作机器ID，值的范围在[0,31]之间，一般可以设置机器编号[必选]
	 */
	private long workerId;

}
