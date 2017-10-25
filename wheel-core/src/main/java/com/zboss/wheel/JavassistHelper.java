package com.zboss.wheel;

import javassist.CtBehavior;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

public class JavassistHelper {

	/**
	 * 获取一个行为的指定注解（行为可以是方法或构造方法）
	 * 
	 * @param ctBehavior
	 * @param type
	 * @return
	 */
	public static Annotation getAnnotation(CtBehavior ctBehavior, String type) {
		// 获取注解属性
		AnnotationsAttribute attribute = (AnnotationsAttribute) ctBehavior.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
		if (attribute == null)
			return null;
		return attribute.getAnnotation(type);
	}

	/**
	 * 生成一个新的注解对象，可以动态的添加给类
	 * 
	 * @param sAnnotationName
	 * @param constPool
	 * @return
	 */
	public static AnnotationsAttribute newAnnotationAttribute(String sAnnotationName, ConstPool constPool) {
		AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		attribute.setAnnotation(new Annotation(sAnnotationName, constPool));
		return attribute;
	}

}
