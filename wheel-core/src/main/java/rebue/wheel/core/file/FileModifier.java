package rebue.wheel.core.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文件修改器
 *
 * @since JDK1.8
 */
public final class FileModifier {

    private boolean mIsModified = false;

    private final Path mFilePath;

    private final List<Adder>    mAdders    = new LinkedList<>();
    private final List<Modifier> mModifiers = new LinkedList<>();
    private final List<String>   mDeleters  = new LinkedList<>();

    public FileModifier(String fileName) {
        mFilePath = Paths.get(fileName);
    }

    /**
     * 添加一行
     *
     * @param patten      判断是否匹配的正则表达式
     * @param adder       要添加的字符串
     * @param exister     判断是否存在的正则表达式(如果存在，则不用添加)
     * @param addPosition 要添加的位置
     */
    public void addLine(String patten, String adder, String exister, AddPosition addPosition) {
        mAdders.add(Adder.builder()
                .patten(Pattern.compile(patten))
                .adder(adder)
                .exister(exister)
                .addPosition(addPosition)
                .build());
    }

    /**
     * 修改一行
     *
     * @param patten 判断是否匹配的正则表达式
     * @param newer  新替换的内容
     */
    public void modifyLine(String patten, String newer) {
        modifyLine(patten, patten, newer);
    }

    /**
     * 修改一行
     *
     * @param patten 判断是否匹配的正则表达式
     * @param older  要被替换的内容的正则表达式
     * @param newer  新替换的内容
     */
    public void modifyLine(String patten, String older, String newer) {
        mModifiers.add(Modifier.builder()
                .patten(Pattern.compile(patten))
                .older(older)
                .newer(newer)
                .build());
    }

    /**
     * 删除行内的部分内容
     *
     * @param deleter 要删除内容的正则表达式
     */
    public void delete(String deleter) {
        mDeleters.add(deleter);
    }

    public void process() throws IOException {
        List<String> lines;
        // 从文件中读取内容并在修改后放入列表
        try (BufferedReader reader = Files.newBufferedReader(mFilePath)) {
            lines = reader.lines().peek(line -> {
                // 线程安全的移除已存在项，不用添加
                mAdders.removeIf(adder -> line.matches(adder.getExister()));
            }).collect(Collectors.toList());
        }

        lines = lines.stream().map(line -> {
            for (String deleter : mDeleters) {
                String tempLine = line;
                line = line.replaceAll(deleter, "");
                if (!line.equals(tempLine))
                    mIsModified = true;
            }
            for (Modifier modifier : mModifiers) {
                Matcher matcher = modifier.getPatten().matcher(line);
                if (matcher.find()) {
                    mIsModified = true;
                    line = line.replaceAll(modifier.getOlder(), modifier.getNewer());
                    break;
                }
            }
            for (Adder adder : mAdders) {
                Matcher matcher = adder.getPatten().matcher(line);
                if (matcher.find()) {
                    mIsModified = true;
                    if (adder.getAddPosition() == AddPosition.AFTER)
                        return line + System.getProperty("line.separator") + adder.getAdder();
                    else if (adder.getAddPosition() == AddPosition.BEFORE)
                        return adder.getAdder() + System.getProperty("line.separator") + line;
                }
            }
            return line;
        }).collect(Collectors.toList());

        // 如果修改了原文件的内容，那么写入原文件
        if (mIsModified) {
            // 将修改的内容写入文件
            try (BufferedWriter writer = Files.newBufferedWriter(mFilePath)) {
                for (String line : lines) {
                    writer.write(line + System.getProperty("line.separator"));
                }
            }
        }
    }

    /**
     * 修改详情
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class Modifier {
        /**
         * 判断是否匹配的正则表达式
         */
        private Pattern patten;
        /**
         * 要被替换的内容的正则表达式
         */
        private String  older;
        /**
         * 新替换的内容
         */
        private String  newer;
    }

    /**
     * 要添加的位置
     */
    public enum AddPosition {
        /**
         * 在匹配的内容前添加一行
         */
        BEFORE,
        /**
         * 在匹配的内容后添加一行
         */
        AFTER
    }

    /**
     * 添加详情
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class Adder {
        /**
         * 判断是否匹配的正则表达式
         */
        private Pattern     patten;
        /**
         * 要添加的字符串
         */
        private String      adder;
        /**
         * 判断是否存在的正则表达式(如果存在，则不用添加)
         */
        private String      exister;
        /**
         * 要添加的位置
         */
        private AddPosition addPosition;
    }
}
