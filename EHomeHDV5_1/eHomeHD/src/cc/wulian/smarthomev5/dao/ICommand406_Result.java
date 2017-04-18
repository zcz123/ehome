package cc.wulian.smarthomev5.dao;

import java.util.List;

import cc.wulian.smarthomev5.entity.Command406Result;

public interface ICommand406_Result {
	/**
	 * 处理获取单条数据
	 */
	public void Reply406Result(Command406Result result);
	/**
	 * 处理获取的多条数据
	 */
	public void Reply406Result(List<Command406Result> results);
}
