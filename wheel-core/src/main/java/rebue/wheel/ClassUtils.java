package rebue.wheel;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import rebue.wheel.exception.NoSuchIntfException;

public class ClassUtils {
	/**
	 * 得到不包含package的类名
	 * 
	 * @param sClassName
	 * @return
	 */
	public static String getSimpleName(String sClassName) {
		return sClassName.substring(sClassName.lastIndexOf(".") + 1); // strip the package name
	}

	/**
	 * 通过Get方法得到属性的field字段名
	 * 
	 * @param methodName
	 * @return
	 */
	public static String getFieldNameByGetMethod(String methodName) {
		return StrUtils.uncapitalize(methodName.substring(3));
	}

	public static String getBeanName(String sClassName) {
		return StrUtils.uncapitalize(getSimpleName(sClassName));
	}

	public static String getBeanName(Class<?> clazz) {
		return getBeanName(clazz.getName());
	}

	/**
	 * 得到本包的上级包全名
	 */
	public static String getParentPackageName(Package pkg) {
		int iLastIndexOf = pkg.getName().lastIndexOf('.');
		return pkg.getName().substring(0, iLastIndexOf);
	}

	/**
	 * 替换包末尾最后那个包的名字，然后返回整个包名
	 * 
	 * @param packageName
	 *            要修改的包的全名
	 * @param suffix
	 *            末尾要改成的后缀
	 */
	public static String replacePackageSuffix(String packageName, String suffix) {
		return packageName.replaceAll("\\.\\w+$", "." + suffix);
	}

	/**
	 * TODO 测试性能<br>
	 * 得到当前方法的名称
	 */
	public static String getCurrentMethodName() {
		// return Thread.currentThread().getStackTrace()[2].getMethodName();
		return new Throwable().getStackTrace()[1].getMethodName();
	}

	public static List<Class<?>> getClassList(String pkgName, boolean isRecursive, Class<? extends Annotation> annotation) {
		List<Class<?>> classList = new ArrayList<>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			// 按文件的形式去查找
			String strFile = pkgName.replaceAll("\\.", File.separator);
			Enumeration<URL> urls = loader.getResources(strFile);
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				if (url != null) {
					String protocol = url.getProtocol();
					String pkgPath = url.getPath();
					System.out.println("protocol:" + protocol + " path:" + pkgPath);
					if ("file".equals(protocol)) {
						// 本地自己可见的代码
						findClassName(classList, pkgName, pkgPath, isRecursive, annotation);
					} else if ("jar".equals(protocol)) {
						// 引用第三方jar的代码
						findClassName(classList, pkgName, url, isRecursive, annotation);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return classList;
	}

	/**
	 * 查找指定包下的类文件
	 * 
	 * @param clazzList
	 * @param pkgName
	 * @param pkgPath
	 * @param isRecursive
	 * @param annotation
	 */
	public static void findClassName(List<Class<?>> clazzList, String pkgName, String pkgPath, boolean isRecursive, Class<? extends Annotation> annotation) {
		if (clazzList == null) {
			return;
		}
		File[] files = filterClassFiles(pkgPath);// 过滤出.class文件及文件夹
		System.out.println("files:" + ((files == null) ? "null" : "length=" + files.length));
		if (files != null) {
			for (File f : files) {
				String fileName = f.getName();
				if (f.isFile()) {
					// .class 文件的情况
					String clazzName = getClassName(pkgName, fileName);
					addClassName(clazzList, clazzName, annotation);
				} else {
					// 文件夹的情况
					if (isRecursive) {
						// 需要继续查找该文件夹/包名下的类
						String subPkgName = pkgName + "." + fileName;
						String subPkgPath = pkgPath + File.separator + fileName;
						findClassName(clazzList, subPkgName, subPkgPath, true, annotation);
					}
				}
			}
		}
	}

	/**
	 * 查找指定包下的类文件(在jar包中 )<br/>
	 * 
	 * @throws IOException
	 */
	public static void findClassName(List<Class<?>> clazzList, String pkgName, URL url, boolean isRecursive, Class<? extends Annotation> annotation) throws IOException {
		JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
		JarFile jarFile = jarURLConnection.getJarFile();
		System.out.println("jarFile:" + jarFile.getName());
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		while (jarEntries.hasMoreElements()) {
			JarEntry jarEntry = jarEntries.nextElement();
			String jarEntryName = jarEntry.getName(); // 类似：sun/security/internal/interfaces/TlsMasterSecret.class
			String clazzName = jarEntryName.replace(File.separator, ".");
			int endIndex = clazzName.lastIndexOf(".");
			String prefix = null;
			if (endIndex > 0) {
				clazzName = clazzName.substring(0, endIndex);
				endIndex = clazzName.lastIndexOf(".");
				if (endIndex > 0) {
					prefix = clazzName.substring(0, endIndex);
				}
			}
			if (prefix != null && jarEntryName.endsWith(".class")) {
//              System.out.println("prefix:" + prefix +" pkgName:" + pkgName);  
				if (prefix.equals(pkgName)) {
					System.out.println("jar entryName:" + jarEntryName);
					addClassName(clazzList, clazzName, annotation);
				} else if (isRecursive && prefix.startsWith(pkgName)) {
					// 遍历子包名：子类
					System.out.println("jar entryName:" + jarEntryName + " isRecursive:" + isRecursive);
					addClassName(clazzList, clazzName, annotation);
				}
			}
		}
	}

	private static File[] filterClassFiles(String pkgPath) {
		if (pkgPath == null) {
			return null;
		}
		// 接收 .class 文件 或 类文件夹
		return new File(pkgPath).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
			}
		});
	}

	private static String getClassName(String pkgName, String fileName) {
		int endIndex = fileName.lastIndexOf(".");
		String clazz = null;
		if (endIndex >= 0) {
			clazz = fileName.substring(0, endIndex);
		}
		String clazzName = null;
		if (clazz != null) {
			clazzName = pkgName + "." + clazz;
		}
		return clazzName;
	}

	private static void addClassName(List<Class<?>> clazzList, String clazzName, Class<? extends Annotation> annotation) {
		if (clazzList != null && clazzName != null) {
			Class<?> clazz = null;
			try {
				clazz = Class.forName(clazzName);
			} catch (ClassNotFoundException e) {
				System.out.println("class name:" + clazzName);
				e.printStackTrace();
			}
//          System.out.println("isAnnotation=" + clazz.isAnnotation() +" author:" + clazz.isAnnotationPresent(author.class));  

			if (clazz != null) {
				if (annotation == null) {
					clazzList.add(clazz);
					System.out.println("find:" + clazz);
				} else if (clazz.isAnnotationPresent(annotation)) {
					clazzList.add(clazz);
					System.out.println("find annotation:" + clazz);
				}
			}
		}
	}

	/**
	 * 得到对象的含有指定注解的接口的名称
	 * 
	 * @throws NoSuchIntfException
	 *             没有这样的接口
	 */
	public static Class<?> getIntfNameWithAnnotationOfObject(Class<? extends Annotation> annotationType, Object obj) throws NoSuchIntfException {
		for (Class<?> intf : obj.getClass().getInterfaces()) {
			if (intf.isAnnotationPresent(annotationType)) {
				return intf;
			}
		}
		throw new NoSuchIntfException();
	}

}
