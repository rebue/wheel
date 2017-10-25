package com.zboss.wheel.spring;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {
	private static ConfigurableApplicationContext	_applicationContext;

	public synchronized static void start() {
		if (_applicationContext == null)
			new ClassPathXmlApplicationContext("classpath*:**/spring-*.xml").start();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (_applicationContext == null) {
			_applicationContext = (ConfigurableApplicationContext) applicationContext;
			_applicationContext.registerShutdownHook();
		} else
			throw new Error("Shouldn't reach here.");
	}

	public static ConfigurableApplicationContext getContext() {
		return _applicationContext;
	}

	public static <T> T getBean(Class<T> requiredType) {
		return _applicationContext.getBean(requiredType);
	}

	/**
	 * 查找文件的匹配器
	 */
	public interface BeanMatcher {
		void matched(Object bean) throws Exception;
	}

	/**
	 * 遍历容器的bean，并匹配有指定注解类型的bean
	 */
	public static void foreachBeansWithAnnotation(Class<? extends Annotation> annotationType, final BeanMatcher beanMatcher) throws Exception {
		Map<String, Object> beans = _applicationContext.getBeansWithAnnotation(annotationType);
		// 将Spring容器中的服务bean转成Key为接口全名的Map
		Object tempBean;
		for (Object bean : beans.values()) {
			// 如果bean被Spring的AOP包装
			if (AopUtils.isAopProxy(bean))
				tempBean = SpringAopHelper.getTarget(bean);
			else
				tempBean = bean;
			beanMatcher.matched(tempBean);
		}
	}

	public static void foreachBeansOfType(Class<?> type, final BeanMatcher beanMatcher) throws Exception {
		Map<String, ?> beanMap = _applicationContext.getBeansOfType(type);
		// 将Spring容器中的服务bean转成Key为接口全名的Map
		Object tempBean;
		for (Object bean : beanMap.values()) {
			// 如果bean被Spring的AOP包装
			if (AopUtils.isAopProxy(bean))
				tempBean = SpringAopHelper.getTarget(bean);
			else
				tempBean = bean;
			beanMatcher.matched(tempBean);
		}
	}
}
