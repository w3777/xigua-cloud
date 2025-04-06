package com.xigua.common.sequence.sequence.impl;

import com.xigua.common.sequence.exception.SeqException;
import com.xigua.common.sequence.sequence.Sequence;

/**
 * 使用雪花算法 一个long类型的数据，64位。以下是每位的具体含义。 <br>
 * snowflake的结构如下(每部分用-分开): <br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * （1）最高位是符号位：第一位为未使用 （0 表示正，1 表示负，固定为 0，如果是 1 就是负数了）
 * （2）毫秒级时间戳：41位为毫秒级时间(41位的长度可以使用69年)
 * （3）数据中心id（机房id）：5位datacenterId
 * （4）工作机器id（应用机器id）：5位workerId
 * （5）序列号：最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
 * 一共加起来刚好64位，为一个Long型。(转换成字符串长度为18)
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
	 * 开始时间截 (北京时间 2025-01-01)
	 */
	private final long twepoch = 1735660800L;

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
		return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift)
				| (workerId << workerIdShift) | sequence;
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
}
