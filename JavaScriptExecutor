package com.huoli.js;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JavaScript的运行器,对之前的运行器进行修改,能够保持引擎不重新创建 <br>
 * 传入参数时,整个 paramMap 直接传入,输出对象也挂到这个 map 上,便于做清理<br>
 * 版权: Copyright (c) 2011-2017<br>
 * 公司: 北京活力天汇<br>
 * 
 * @author: 童凡<br>
 * @date: 2018年6月24日<br>
 */
public class JavaScriptExecutor {
	private static Logger logger = LoggerFactory.getLogger(JavaScriptExecutor.class);
	/** 返回需要忽略的变量列表 */
	private List<String> ignoreList = new ArrayList<String>();
	/** 脚本引擎 */
	private ScriptEngine engine;

	public JavaScriptExecutor() {
		// 忽略列表初始化
		ignoreList.add("print");
		ignoreList.add("println");
		ignoreList.add("context");
		createEngine();
	}

	/** 创建引擎 */
	public void createEngine() {
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		engine = scriptEngineManager.getEngineByName("nashorn");
	}

	/** 使用引擎执行工具脚本,本方法执行一次,后续不需要再次执行 */
	public void init(List<String> toolScripts) {
		try {
			// 加载工具脚本
			if (toolScripts != null) {
				for (String toolScript : toolScripts) {
					engine.eval(toolScript);
				}
			}
		} catch (ScriptException e) {
			logger.error("Exception", e);
		}
	}

	/**
	 * 运行脚本
	 * 
	 * @param script
	 *            脚本文本
	 * @param param
	 *            参数列表
	 * @param variableList
	 *            需要返回的参数名
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> execute(String script, Map<String, Object> param) {
		// 先将参数置空,避免因为没有传入参数导致使用上一次的参数
		engine.put("param", null);
		// 放入输入参数
		if (param != null) {
			engine.put("param", param);
		}
		// 运行脚本
		try {
			engine.eval(script);
		} catch (ScriptException e) {
			logger.error("Exception", e);
		}
		ScriptContext scriptContext = engine.getContext();
		Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);

		// 处理返回的值
		param = (Map<String, Object>) bindings.get("param");

		return param;
	}

	/** 清理传入参数 */
	public void clearParam() {
		ScriptContext scriptContext = engine.getContext();
		Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
		// 清理传入参数
		bindings.remove("param");
	}

	public List<String> getIgnoreList() {
		return ignoreList;
	}

	public void setIgnoreList(List<String> ignoreList) {
		this.ignoreList = ignoreList;
	}

	public ScriptEngine getEngine() {
		return engine;
	}

	public void setEngine(ScriptEngine engine) {
		this.engine = engine;
	}

}
