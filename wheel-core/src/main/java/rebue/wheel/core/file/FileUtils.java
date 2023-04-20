package rebue.wheel.core.file;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
public class FileUtils {

    /**
     * 得到项目的绝对路径
     */
    public static String getProjectPath() {
        return System.getProperty("user.dir");
    }

    /**
     * 得到类路径
     */
    public static String getClassesPath() {
        return getClassesPath(FileUtils.class);
    }

    /**
     * 得到类路径
     */
    public static String getClassesPath(Class clazz) {
        String result;
        result = clazz.getResource(File.separator).getPath();
        // windows系统中取得的路径如/D:/workspace/abc/， 去掉第一个字母'/'
        if (result.charAt(0) == '/' && result.charAt(2) == ':') {
            result = result.substring(1);
        }

//        result = Thread.currentThread().getContextClassLoader().getResource(File.separator).getPath();
//        result = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();

        log.info("得到类路径:" + result);
        return result;
    }

    public static String getWebRootPath() {
        return getClassesPath() + "../..";
    }

    public static String getWebLibPath() {
        return getClassesPath() + "../lib";
    }

    /**
     * 得到计算后的路径
     */
    public static String getCanonicalPath(String path) {
        try {
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("错误的路径：" + path);
            throw new RuntimeException(e);
        }
    }

    /**
     * 得到项目源代码的绝对路径（/src/main/java）
     */
    public static String getSrcPath() {
        return getProjectPath() + "/src/main/java/";
    }

    /**
     * 替换目录末尾最后那个目录的名字，然后返回整个路径
     *
     * @param dirPath 要改的目录的全路径名
     * @param suffix  末尾要改成的后缀
     */
    public static String replaceDirSuffix(String dirPath, String suffix) {
        return dirPath.replaceAll("/\\w+$", File.separator + suffix);
    }

    public static String replaceFileSuffix(String fileName, String suffix) {
        return fileName.replaceAll("\\.\\w+$", "." + suffix);
    }

    public static boolean deleteDir(String sDir) {
        return deleteDir(new File(sDir));
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            // 递归删除目录中的子目录下
            for (String children : dir.list()) {
                boolean success = deleteDir(new File(dir, children));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    // /**
    // * 查找文件的匹配器
    // */
    // public interface FileMatcher {
    // void matched(File file, Matcher matcher);
    // }

    // /**
    // * 递归查找文件
    // *
    // * @param sSearchDir
    // * 查找的文件夹路径
    // * @param fileComparer
    // * 文件比较接口
    // * @param patten
    // * 匹配文件名的正则表达式
    // *
    // * XXX 与new File(dir).listFiles(new
    // * FileFilter(){....有区别，这个是递归查找文件的
    // */
    // public static void searchFiles(String sSearchDir, Pattern pattern, FileMatcher fileComparer) {
    // File searchDir = new File(sSearchDir);
    // if (!searchDir.exists())
    // _logger.error("文件查找失败：不存在" + sSearchDir + "这个路径");
    // else if (!searchDir.isDirectory())
    // _logger.error("文件查找失败：" + sSearchDir + "不是一个目录！");
    // else
    // searchFiles(new File(sSearchDir), pattern, fileComparer);
    // }
    //
    // private static void searchFiles(File searchDir, final Pattern pattern, final FileMatcher fileMatcher) {
    // searchDir.listFiles(new FileFilter() {
    // @Override
    // public boolean accept(File file) {
    // if (file.isDirectory())
    // searchFiles(file, pattern, fileMatcher);
    // else {
    // try {
    // Matcher matcher = pattern.matcher(file.getCanonicalPath());
    // if (matcher.find())
    // fileMatcher.matched(file, matcher);
    // } catch (IOException e) {
    // _logger.error("不应该的异常：是在查找到文件后再取的路径信息", e);
    // }
    // }
    // return false;
    // }
    // });
    // }

    public static byte[] getBytesFromFile(String filePath) throws IOException {
        File            file            = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[]          data            = new byte[(int) file.length()];
        try (DataInputStream dataInputStream = new DataInputStream(fileInputStream)) {
            dataInputStream.readFully(data);
        }
        return data;
    }

}
