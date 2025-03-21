package com.xigua.common.sequence.builder;

import com.xigua.common.sequence.sequence.Sequence;

/**
 * 序列号生成器构建者
 *
 * @author zcf
 */
public interface SeqBuilder {

	/**
	 * 构建一个序列号生成器
	 * @return 序列号生成器
	 */
	Sequence build();

}
