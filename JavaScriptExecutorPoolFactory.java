package com.huoli.js;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * JavaScript 执行器池的工厂类 <br>
 * <br>
 * 版权: Copyright (c) 2011-2018<br>
 * 公司: 活力天汇<br>
 * 
 * @author: 童凡<br>
 * @date: 2018年6月24日<br>
 */
public class JavaScriptExecutorPoolFactory implements PooledObjectFactory<JavaScriptExecutor> {
	private static Logger logger = LoggerFactory.getLogger(JavaScriptExecutorPoolFactory.class);
	/** 工具脚本的路径 */
	private List<String> toolScriptsPath;

	/** 创建对象 */
	@Override
	public PooledObject<JavaScriptExecutor> makeObject() throws Exception {
		JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor();
		logger.trace("make: {}", javaScriptExecutor);
		// 初始化工具脚本
		ResourceLoader loader = new DefaultResourceLoader();
		List<String> toolScripts = new ArrayList<String>();
		for (String filePath : toolScriptsPath) {
			Resource resource = loader.getResource(filePath);
			File file = resource.getFile();
			String toolScript = FileUtils.readFileToString(file, "utf-8");
			toolScripts.add(toolScript);
		}
		javaScriptExecutor.init(toolScripts);
		PooledObject<JavaScriptExecutor> pooledObject = new DefaultPooledObject<JavaScriptExecutor>(javaScriptExecutor);
		return pooledObject;
	}

	/** 销毁对象 */
	@Override
	public void destroyObject(PooledObject<JavaScriptExecutor> pooledObject) throws Exception {
		JavaScriptExecutor javaScriptExecutor = pooledObject.getObject();
		logger.trace("destroy: {}", javaScriptExecutor);
		// 清理执行器中的引擎
		javaScriptExecutor.setEngine(null);

	}

	/** 验证对象是否可用 */
	@Override
	public boolean validateObject(PooledObject<JavaScriptExecutor> pooledObject) {
		logger.trace("validate: {}", pooledObject.getObject());
		JavaScriptExecutor javaScriptExecutor = pooledObject.getObject();
		if (javaScriptExecutor.getEngine() != null) {
			return true;
		}
		return false;
	}

	/** 不可用对象进行激活 */
	@Override
	public void activateObject(PooledObject<JavaScriptExecutor> pooledObject) throws Exception {
		JavaScriptExecutor javaScriptExecutor = pooledObject.getObject();
		if (javaScriptExecutor.getEngine() == null) {
			javaScriptExecutor.createEngine();
			List<String> toolScripts = new ArrayList<String>();
			for (String filePath : toolScriptsPath) {
				File file = new ClassPathResource(filePath).getFile();
				String toolScript = FileUtils.readFileToString(file, "utf-8");
				toolScripts.add(toolScript);
			}
			javaScriptExecutor.init(toolScripts);
		}
		logger.trace("activate: {}", pooledObject.getObject());
	}

	/** 钝化对象,资源还给池时调用,主要是需要清理上下文中的参数 */
	@Override
	public void passivateObject(PooledObject<JavaScriptExecutor> pooledObject) throws Exception {
		JavaScriptExecutor javaScriptExecutor = pooledObject.getObject();
		javaScriptExecutor.clearParam();
		logger.trace("passivate: {}", pooledObject.getObject());
	}

	public List<String> getToolScriptsPath() {
		return toolScriptsPath;
	}

	public void setToolScriptsPath(List<String> toolScriptsPath) {
		this.toolScriptsPath = toolScriptsPath;
	}

}
