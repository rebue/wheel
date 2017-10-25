package com.zboss.wheel.file;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件搜索器<br>
 * 根据提供的正则表达式，递归搜索指定的目录下，匹配的文件
 * 
 * @since JDK1.8
 */
public final class FileSearcher {
    private static Logger _logger = LoggerFactory.getLogger(FileSearcher.class);

    /**
     *
     * @param sSearchRootDir
     *            查找的文件夹路径
     * @param patten
     *            匹配文件名的正则表达式
     * @param onMatched
     *            文件匹配事件
     * 
     * @throws IOException
     */
    public static void searchFiles(String sSearchRootDir, String regex, Consumer<File> onMatched) throws IOException {
        File searchRootDir = new File(sSearchRootDir);
        searchFiles(searchRootDir, Pattern.compile(regex), onMatched);
    }

    public static void searchFiles(File searchRootDir, String regex, Consumer<File> onMatched) throws IOException {
        searchFiles(searchRootDir, Pattern.compile(regex), onMatched);
    }

    public static void searchFiles(File searchRootDir, Pattern pattern, Consumer<File> onMatched) throws IOException {
        if (!searchRootDir.exists()) {
            throw new IOException("文件查找失败：不存在" + searchRootDir.getAbsolutePath() + "这个路径");
        }
        if (searchRootDir.isFile()) {
            throw new IOException("文件查找失败：" + searchRootDir.getAbsolutePath() + "不是一个目录！");
        }
        searchFiles0(searchRootDir, pattern, onMatched);
    }

    /**
     * 递归搜索文件的方法，可将所有子目录的文件都查出来
     * 
     * @param searchDir
     *            查找的文件夹路径
     * @param patten
     *            匹配文件名的正则表达式
     * @param onMatched
     *            文件匹配事件
     */
    private static void searchFiles0(File searchDir, Pattern pattern, Consumer<File> onMatched) {
        Stream.of(searchDir.listFiles()).forEach(file -> {
            if (file.isDirectory()) {
                searchFiles0(file, pattern, onMatched);
            } else {
                try {
                    // 注意，这里用的是getCanonicalPath，是经过解析后的路径
                    if (pattern.matcher(file.getCanonicalPath()).find()) {
                        onMatched.accept(file);
                    }
                } catch (IOException e) {
                    _logger.error("不应该的异常：是在查找到文件后再取的路径信息", e);
                }
            }
        });
    }

    /**
     *
     * @param sSearchRootDir
     *            查找的文件夹路径
     * @param patten
     *            匹配文件名的正则表达式
     * @param onMatched
     *            文件匹配事件
     * 
     * @throws IOException
     */
    public static List<File> searchFiles(String sSearchRootDir, Pattern pattern) throws IOException {
        File searchDir = new File(sSearchRootDir);
        if (!searchDir.exists()) {
            throw new IOException("文件查找失败：不存在" + searchDir.getAbsolutePath() + "这个路径");
        }
        if (!searchDir.isDirectory()) {
            throw new IOException("文件查找失败：" + searchDir.getAbsolutePath() + "不是一个目录！");
        }

        List<File> files = new LinkedList<>();
        searchFiles1(searchDir, pattern, files);
        return files;
    }

    private static void searchFiles1(File searchDir, Pattern pattern, List<File> files) throws IOException {
        for (File file : searchDir.listFiles()) {
            if (file.isDirectory()) {
                searchFiles1(file, pattern, files);
            } else {
                if (pattern.matcher(file.getCanonicalPath()).find()) {
                    files.add(file);
                }
            }
        }
    }
}
