package rebue.wheel.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author ZBZ
 * 
 * 反射帮助类
 * 
 */
public class ReflectEx {

	protected static Logger _logger = LoggerFactory.getLogger(ReflectEx.class);

	/**
	 * 得到对象指定字段的值
	 * 
	 * @param obj
	 * @param field
	 * @return
	 */
	public static Object getFieldValue(Object obj, Field field) {
		_logger.debug("loading JpaSupport method id..");
		field.setAccessible(true);
		try {
			return field.get(obj);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("不应该的异常，类的字段定义不符合求规范", e);
		}
	}

	/**
	 * 设置字段的值
	 * 
	 * @param obj
	 * @param field
	 */
	public static void setFieldValue(Object obj, Field field, Object value) {
		field.setAccessible(true);
		try {
			field.set(obj, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("不应该的异常，类的字段定义不符合求规范", e);
		}
	}

	/**
	 * 将src中属性值复制到dst中，src中为null的属性不复制
	 */
	public static void copy(Object src, Object dst) {
		Map<String, Method> mapMethodDst = new HashMap<>();
		Map<String, Object> mapMethodSrc = new HashMap<>();

		Method[] methodDst = dst.getClass().getMethods();
		for (Method methodGet : methodDst) {
			try {
				String propertyName;
				if (methodGet.getName().startsWith("get"))
					propertyName = methodGet.getName().substring(3);
				else
					continue;
				if (methodGet.getReturnType().isAssignableFrom(Set.class))
					continue;
				Method methodSet = dst.getClass().getMethod("set" + propertyName, methodGet.getReturnType());
				mapMethodDst.put(propertyName, methodSet);
			} catch (Exception e) {
				_logger.debug("复制对象出问题，可能是有get的方法但没有Set的方法", e.fillInStackTrace());
			}
		}

		Method[] methodSrc = src.getClass().getMethods();
		for (Method methodGet : methodSrc) {
			try {
				String propertyName;
				if (methodGet.getName().startsWith("get"))
					propertyName = methodGet.getName().substring(3);
				else
					continue;
				// 注释掉下面两行是因为集合应该也需要复制
				// if (methodGet.getReturnType().isAssignableFrom(Set.class))
				// continue;
				Object valueSrc = methodGet.invoke(src);
				// 原值为null时不复制
				if (valueSrc == null)
					continue;
				// 注释掉下面5行是因为字符串为""时不为null，也应该要复制
				// if (methodGet.getReturnType().isAssignableFrom(String.class))
				// {
				// if (((String) valueSrc).trim().equals(""))
				// continue;
				// }

				// TODO 奇怪的需求，如果是实体类，id为空，意思是删除此对象
				// Class<?> clazz = methodGet.getReturnType();
				// if (clazz.isAnnotationPresent(Entity.class)) {
				// // 如果id为空
				// Object idValue = JpaUtil.getIdValue(valueSrc);
				// if (idValue == null || idValue.toString().isEmpty()) {
				// mapMethodSrc.put(propertyName, null);
				// continue;
				// }
				// }
				mapMethodSrc.put(propertyName, valueSrc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (Entry<String, Object> entry : mapMethodSrc.entrySet()) {
			try {
				Method method = mapMethodDst.get(entry.getKey());
				if (method != null) {
					method.invoke(dst, entry.getValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 以dst为基础，将自己值为null的属性从src中复制过来
	 */
	public static void merge(Object src, Object dst) {
		Map<String, Method> mapMethodDst = new HashMap<>();
		Map<String, Object> mapMethodSrc = new HashMap<>();

		Method[] methodDst = dst.getClass().getMethods();
		for (Method methodGet : methodDst) {
			try {
				String propertyName;
				if (methodGet.getName().startsWith("get"))
					propertyName = methodGet.getName().substring(3);
				else if (methodGet.getName().startsWith("is"))
					propertyName = methodGet.getName().substring(2);
				else
					continue;
				if (methodGet.getReturnType().isAssignableFrom(Set.class))
					continue;
				Method methodSet = dst.getClass().getMethod("set" + propertyName, methodGet.getReturnType());
				Object valueDst;
				valueDst = methodGet.invoke(dst);
				if (valueDst != null) {
					if (methodGet.getReturnType().isAssignableFrom(String.class)) {
						if (!((String) valueDst).trim().equals(""))
							continue;
					} else
						continue;
				}
				mapMethodDst.put(propertyName, methodSet);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Method[] methodSrc = src.getClass().getMethods();
		for (Method methodGet : methodSrc) {
			try {
				String propertyName;
				if (methodGet.getName().startsWith("get"))
					propertyName = methodGet.getName().substring(3);
				else if (methodGet.getName().startsWith("is"))
					propertyName = methodGet.getName().substring(2);
				else
					continue;
				if (methodGet.getReturnType().isAssignableFrom(Set.class))
					continue;
				Object valueSrc = methodGet.invoke(src);
				if (valueSrc == null)
					continue;
				if (methodGet.getReturnType().isAssignableFrom(String.class)) {
					if (((String) valueSrc).trim().equals(""))
						continue;
				}
				mapMethodSrc.put(propertyName, valueSrc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (Entry<String, Object> entry : mapMethodSrc.entrySet()) {
			try {
				Method method = mapMethodDst.get(entry.getKey());
				if (method != null) {
					method.invoke(dst, entry.getValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
