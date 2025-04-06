package com.xigua.common.sequence.sequence;

import com.xigua.common.sequence.exception.SeqException;

/**
 * 序列号生成器接口
 *
 * @author zcf
 */
public interface Sequence {

	/**
	 * 生成下一个序列号
	 * @return 序列号
	 * @throws SeqException 序列号异常
	 */
	long nextValue() throws SeqException;

	/**
	 * 生成下一个序列号 （string）
	 * @author wangjinfei
	 * @date 2025/4/6 10:29
	 * @return String
	*/
	String nextNo();
}
