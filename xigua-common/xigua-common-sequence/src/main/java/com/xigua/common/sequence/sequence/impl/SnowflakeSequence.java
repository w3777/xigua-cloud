package com.xigua.common.sequence.sequence.impl;

import com.xigua.common.sequence.exception.SeqException;
import com.xigua.common.sequence.sequence.Sequence;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 使用雪花算法 一个long类型的数据，64位。以下是每位的具体含义。
 * snowflake的结构如下(每部分用-分开):
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * （1）最高位是符号位：第一位为未使用 （0 表示正，1 表示负，固定为 0，如果是 1 就是负数了）
 * （2）毫秒级时间戳：41位为毫秒级时间(41位的长度可以使用69年)
 * （3）数据中心id（机房id）：5位datacenterId
 * （4）工作机器id（应用机器id）：5位workerId
 * （5）序列号：最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
 * 特别注意:
 * 雪花id指的是一个 64bit 的 long 型的数值，可以参考to64BitBinaryString方法的打印结果
 * 生成的id长度是不固定的，受twepoch、机器id、数据中心id、序列号等参数的影响
 * 比如
 * 较小的 twepoch 会导致时间差较大，分配给时间戳部分的位数更多，从而生成更长的 ID
 * 较大的 twepoch 会导致时间差较小，分配给时间戳部分的位数较少，从而生成更短的 ID
 *
 * 注意：
 * 1. 标识位（存储机器码）：10bit，上面中的数据中心id（5bit）和工作机器id（5bit）统一叫作“标识位”，两个标识位组合起来最多可以支持部署 1024 个节点（32 * 32）。
 * 2. 如果希望运行更久，增加时间戳的位数；如果需要支持更多节点部署，增加标识位长度；如果并发很高，增加序列号位数
 * 3. 毫秒时间戳在高位，自增序列在低位，生成出来的id才能保证趋势递增
 * 4. 分布式集群部署时，配置数据中心id和工作机器id，保证分布式环境下id不重复
 * 5. 可以根据自身业务特性分配bit位，非常灵活
 *
 * 缺点：
 * 依赖服务器时间，服务器时间回拨时可能会生成重复id。
 * 在获取时间的时候，可能会出现时间回拨的问题，什么是时间回拨问题呢？就是服务器上的时间突然倒退到之前的时间。
 * 人为原因，把系统环境的时间改了；
 * 有时候不同的机器上需要同步时间，可能不同机器之间存在误差，那么可能会出现时间回拨问题。
 * 解决方案：算法中可通过记录最后一个生成id时的时间戳来解决，每次生成id之前比较当前服务器时钟是否被回拨，避免生成重复id
 *
 *
 * @author zcf
 */
public class SnowflakeSequence implements Sequence {

	/**
	 * 开始时间截 (北京时间 2025-01-01 00:00:00.000)
	 * 毫秒级时间戳
	 */
	private final long twepoch = 1735660800000L;

	/**
	 * 机器id所占的位数
	 */
	private final long workerIdBits = 5L;

	/**
	 * 数据标识id所占的位数
	 */
	private final long datacenterIdBits = 5L;

	/**
	 * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
	 */
	private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

	/**
	 * 支持的最大数据标识id，结果是31
	 */
	private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

	/**
	 * 序列在id中占的位数
	 */
	private final long sequenceBits = 12L;

	/**
	 * 机器ID向左移12位
	 */
	private final long workerIdShift = sequenceBits;

	/**
	 * 数据标识id向左移17位(12+5)
	 */
	private final long datacenterIdShift = sequenceBits + workerIdBits;

	/**
	 * 时间截向左移22位(5+5+12)
	 */
	private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

	/**
	 * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
	 */
	private final long sequenceMask = -1L ^ (-1L << sequenceBits);

	/**
	 * 工作机器ID(0~31)
	 */
	private long workerId;

	/**
	 * 数据中心ID(0~31)
	 */
	private long datacenterId;

	/**
	 * 毫秒内序列(0~4095)
	 */
	private long sequence = 0L;

	/**
	 * 上次生成ID的时间截
	 */
	private long lastTimestamp = -1L;

	// 加锁保证线程安全
	@Override
	public synchronized long nextValue() throws SeqException {
		long timestamp = timeGen();

		// 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
		if (timestamp < lastTimestamp) {
			throw new SeqException("[SnowflakeSequence-nextValue] 当前时间小于上次生成序列号的时间，时间被回退了，请确认服务器时间的设置.");
		}

		// 如果是同一时间生成的，则进行毫秒内序列
		if (lastTimestamp == timestamp) {
			// sequence进行递增，同时通过sequenceMask进行取模运算，以确保sequence始终保持在sequenceMask允许的范围内
			// sequence在[0, sequenceMask]之间循环，而不会无限递增
			sequence = (sequence + 1) & sequenceMask;
			// 毫秒内序列溢出 （说明当前毫秒的sequence已满，已到达4095，表明业务的毫秒并发达到4095，并发非常高!）
			if (sequence == 0) {
				// 阻塞到下一个毫秒,获得新的时间戳
				timestamp = tilNextMillis(lastTimestamp);
			}
		}
		else { // 不在同一时间生成的，即时间戳改变了，毫秒内的sequence重置
			// 时间戳改变，毫秒内序列重置
			sequence = 0L;
		}

		// 上次生成ID的时间截
		lastTimestamp = timestamp;

		// 移位并通过或运算拼到一起组成64位的ID
		long snowflakeId = ((timestamp - twepoch) << timestampLeftShift)
				           | (datacenterId << datacenterIdShift)
				           | (workerId << workerIdShift)
				           | sequence;

		/**
		 * timestamp - twepoch为什么这里要减去twepoch？其他都是按照位运算左移
		 * 1. 如果只用当前时间戳timestamp，会随着时间的推移而不断增长，导致生成的ID长度不断增加，可能会超过64位的限制
		 * 2. 可以将当前时间戳减去一个固定的起始时间twepoch，这样可以将时间戳映射到一个相对较小的范围内，保证不会超过64位的限制
		 * 3. 如果只用twepoch，会导致有人通过id反向推出生成规律，因为后12bit是递增的，可以分析出订单量、用户量、成交量等安全隐患
		 * 雪花id要保证：唯一、趋势递增、趋势递增不能轻易被发现
		*/


		// 解析雪花id
//		parseSnowflakeId(snowflakeId);

		return snowflakeId;
	}

	/**
	 * 阻塞到下一个毫秒，直到获得新的时间戳
	 * @param lastTimestamp 上次生成ID的时间截
	 * @return 当前时间戳
	 */
	private long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	/**
	 * 返回以毫秒为单位的当前时间
	 * @return 当前时间(毫秒)
	 */
	private long timeGen() {
		return System.currentTimeMillis();
	}

	public void setWorkerId(long workerId) {
		if (workerId > maxWorkerId) {
			throw new SeqException("[SnowflakeSequence-setWorkerId] workerId 不能大于31.");
		}

		this.workerId = workerId;
	}

	public void setDatacenterId(long datacenterId) {
		if (datacenterId > maxDatacenterId) {
			throw new SeqException("[SnowflakeSequence-setDatacenterId] datacenterId 不能大于31.");
		}

		this.datacenterId = datacenterId;
	}

	/**
	 * 生成下一个序列号 （string）
	 * @author wangjinfei
	 * @date 2025/4/6 10:29
	 * @return String
	 */
	@Override
	public String nextNo() {
		return String.valueOf(nextValue());
	}

	/**
	 * 解析雪花id
	 * @author wangjinfei
	 * @date 2025/6/8 16:45
	 * @param snowflakeId
	*/
	public void parseSnowflakeId(long snowflakeId) {
		long sequence = snowflakeId & sequenceMask;
		long workerId = (snowflakeId >> workerIdShift) & maxWorkerId;
		long datacenterId = (snowflakeId >> datacenterIdShift) & maxDatacenterId;
		long timestampDelta = snowflakeId >> timestampLeftShift;
		long timestamp = timestampDelta + twepoch;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String formattedTime = sdf.format(new Date(timestamp));

		// 按位宽格式化二进制字符串，左边补0
		String binaryTimestamp = String.format("%41s", Long.toBinaryString(timestampDelta)).replace(' ', '0');
		String binaryDatacenterId = String.format("%5s", Long.toBinaryString(datacenterId)).replace(' ', '0');
		String binaryWorkerId = String.format("%5s", Long.toBinaryString(workerId)).replace(' ', '0');
		String binarySequence = String.format("%12s", Long.toBinaryString(sequence)).replace(' ', '0');

		// 拼接成完整64位二进制字符串，最高位符号位默认为0
		String fullBinary = "0" + binaryTimestamp + binaryDatacenterId + binaryWorkerId + binarySequence;

		System.out.println("原始雪花ID: " + snowflakeId);
		System.out.println("二进制: " + String.format("%64s", Long.toBinaryString(snowflakeId)).replace(' ', '0'));
		System.out.println("二进制: " + fullBinary);
		System.out.println("时间戳: " + timestamp + " (" + formattedTime + ")");
		System.out.println("数据中心ID: " + datacenterId);
		System.out.println("机器ID: " + workerId);
		System.out.println("序列号: " + sequence);
	}

	public void matchSnowflakeId(long snowflakeId, long timestmap) {
		long sequence = snowflakeId & sequenceMask;
		long workerId = (snowflakeId >> workerIdShift) & maxWorkerId;
		long datacenterId = (snowflakeId >> datacenterIdShift) & maxDatacenterId;
	}
}
