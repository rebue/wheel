package rebue.wheel.core.file;

import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public class FileUtils {

    /**
     * 判断是否是绝对路径(不是绝对路径就是相对路径)
     *
     * @param path 路径
     * @return 是否是绝对路径
     */
    public static boolean isAbsPath(String path) {
        return path.startsWith("/") || path.indexOf(":") > 0;
    }

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
    public static String getClassesPath(Class<?> clazz) {
        URL resource = clazz.getResource(File.separator);
        if (resource == null) throw new RuntimeException("获取类资源为null");
        String result = resource.getPath();
        // windows系统中取得的路径如/D:/workspace/abc/， 去掉第一个字母'/'
        if (result.charAt(0) == '/' && result.charAt(2) == ':') {
            result = result.substring(1);
        }
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
        String[] dirList = dir.list();
        if (dir.isDirectory() && dirList != null) {
            // 递归删除目录中的子目录下
            for (String children : dirList) {
                boolean success = deleteDir(new File(dir, children));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 读取文件内容到字符串
     *
     * @param filePath 文件路径
     * @return 文件内容的字符串
     * @throws IOException IO异常
     */
    public static String readToString(String filePath) throws IOException {
        StringJoiner contentStringJoiner = new StringJoiner(System.lineSeparator());
        try (BufferedReader in = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = in.readLine()) != null) {
                contentStringJoiner.add(line);
            }
        }
        return contentStringJoiner.toString();
    }

    /**
     * 读取资源文件内容到字符串
     *
     * @param resourceFilePath 文件路径
     * @return 文件内容的字符串
     * @throws IOException IO异常
     */
    public static List<String> readResourceFileToList(String resourceFilePath, Class<?> clazz) throws IOException {
        List<String> list        = new LinkedList<>();
        InputStream  inputStream = clazz.getResourceAsStream(resourceFilePath);
        if (inputStream == null) throw new RuntimeException("获取类资源为null");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = in.readLine()) != null) {
                list.add(line);
            }
        }
        return list;
    }

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
