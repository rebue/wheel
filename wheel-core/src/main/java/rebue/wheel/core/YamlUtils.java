package rebue.wheel.core;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class YamlUtils {

    /**
     * 从YAML格式的字符串中查找匹配的key所在行的索引
     * 
     * @param lines YAML格式按行分割后的字符串数组
     * @param key   要查找的key，可以用“.”分割
     * 
     * @return 行索引(从0开始为第一个元素)，如果没有找到则返回-1
     */
    public static int getLineIndex(final String[] lines, final String key) {
        if (lines.length == 0) {
            throw new IllegalArgumentException("文本行不能为空");
        }
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key不能为空");
        }

        final String[] keys = getKeys(key);

        // 查询key根据“.”分割后数组中的索引
        int keyIndex                = 0;
        // 上一行的左边空格长度
        int lastLineLeftSpaceLength = 0;
        // 当前行
        int lineIndex               = -1;
        for (String line : lines) {
            lineIndex++;
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (line.trim().charAt(0) == '#') {
                continue;
            }
            // 左边空格长度
            final int leftSpaceLength = getLeftSpaceLength(line);

            // 去空格
            line = line.trim();

            if (keyIndex != 0 && leftSpaceLength <= lastLineLeftSpaceLength) {
                return -1;
            }

            if (line.startsWith(keys[keyIndex] + ":")) {
                if (keyIndex == keys.length - 1) {
                    return lineIndex;
                }
                lastLineLeftSpaceLength = leftSpaceLength;
                keyIndex++;
                continue;
            }
        }
        return -1;
    }

    /**
     * 从YAML格式的字符串中查找匹配的key所在行的索引，如果不存在，则创建该行
     * 
     * @param lines YAML格式按行分割后的字符串数组
     * @param key   要查找的key，可以用“.”分割
     * 
     * @return 行索引(从0开始为第一个元素)，如果没有则创建该行并返回该行索引
     */
    public static int getOrCreateLineIndex(final List<String> lines, final String key) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("文本行不能为空");
        }
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key不能为空");
        }

        final String[] keys = getKeys(key);

        // 查询key根据“.”分割后数组中的索引
        int keyIndex                = 0;
        // 上一行的左边空格长度
        int lastLineLeftSpaceLength = 0;
        // 当前行
        int lineIndex               = -1;
        for (String line : lines) {
            lineIndex++;
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (line.trim().charAt(0) == '#') {
                continue;
            }
            // 左边空格长度
            final int leftSpaceLength = getLeftSpaceLength(line);

            // 去空格
            line = line.trim();

            if (keyIndex != 0 && leftSpaceLength <= lastLineLeftSpaceLength) {
                final int spaces = leftSpaceLength / keyIndex;
                return insertKey(lines, lineIndex, keys, keyIndex, spaces);
            }

            if (line.startsWith(keys[keyIndex] + ":")) {
                if (keyIndex == keys.length - 1) {
                    return lineIndex;
                }
                lastLineLeftSpaceLength = leftSpaceLength;
                keyIndex++;
                continue;
            }
        }

        return insertKey(lines, lineIndex, keys, keyIndex, 2);
    }

    /**
     * 往YAML格式的字符串中插入Key行
     * 
     * @param lines     YAML格式的字符串按换行符分割后的List
     * @param lineIndex 要插入行的索引
     * @param keys      要插入的key数组
     * @param keyIndex  要插入的key数组的开始的索引
     * @param spaces    每级的空格数
     */
    public static int insertKey(final List<String> lines, int lineIndex, final String[] keys, int keyIndex, final int spaces) {
        for (; keyIndex < keys.length; keyIndex++, lineIndex++) {
            lines.add(lineIndex + 1, StringUtils.repeat(' ', spaces * keyIndex) + keys[keyIndex] + ": ");
        }
        return lineIndex;
    }

    /**
     * 从YAML格式的字符串中查找匹配的key的值
     * 
     * @param content YAML格式的字符串
     * @param key     要查找的key，可以用“.”分割
     * 
     * @return 对应Key的值，如果没有找到则返回null
     */
    public static String getAsString(final String content, final String key) {
        final String[] lines        = yaml2StringArray(content);
        final int      keyLineIndex = getLineIndex(lines, key);
        if (keyLineIndex == -1) {
            return null;
        }
        final String line = lines[keyLineIndex];
        return line.substring(line.indexOf(':') + 1).trim();
    }

    /**
     * 从YAML格式的字符串中查找匹配的key的值
     * 
     * @param content YAML格式的字符串
     * @param key     要查找的key，可以用“.”分割
     * 
     * @return 对应Key的值，如果没有找到则返回null
     */
    public static List<String> getAsStringList(final String content, final String key) {
        final String[] lines        = yaml2StringArray(content);
        final int      keyLineIndex = getLineIndex(lines, key);
        if (keyLineIndex == -1) {
            return null;
        }
        // 上一行左边空格长度
        final int          lastLineLeftSpaceLength = getLeftSpaceLength(lines[keyLineIndex]);
        final List<String> result                  = new LinkedList<>();
        for (int curLineIndex = keyLineIndex + 1; curLineIndex < lines.length; curLineIndex++) {
            String    line            = lines[curLineIndex];
            // 左边空格长度
            final int leftSpaceLength = getLeftSpaceLength(line);
            if (leftSpaceLength < lastLineLeftSpaceLength) {
                break;
            }
            line = line.trim();
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (line.startsWith("#")) {
                continue;
            }
            if (!line.startsWith("- ")) {
                log.warn("{}行格式不正确: {}，不是集合的格式", curLineIndex + 1, line);
                break;
            }

            result.add(line.substring(1).trim());
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    /**
     * 从YAML格式的字符串中查找匹配的key的值
     * 
     * @param content YAML格式的字符串
     * @param key     要查找的key，可以用“.”分割
     * 
     * @return 对应Key的值，如果没有找到则返回null
     */
    public static List<Map<String, String>> getAsMapList(final String content, final String key) {
        final String[] lines        = yaml2StringArray(content);
        final int      keyLineIndex = getLineIndex(lines, key);
        if (keyLineIndex == -1) {
            return null;
        }
        // Key行左边空格长度
        final int                       keyLineLeftSpaceLength = getLeftSpaceLength(lines[keyLineIndex]);
        final List<Map<String, String>> result                 = new LinkedList<>();
        for (int curLineIndex = keyLineIndex + 1; curLineIndex < lines.length; curLineIndex++) {
            String    line                   = lines[curLineIndex];
            // Map行左边空格长度
            final int mapLineLeftSpaceLength = getLeftSpaceLength(line);
            if (mapLineLeftSpaceLength < keyLineLeftSpaceLength) {
                break;
            }
            line = line.trim();
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (line.startsWith("#")) {
                continue;
            }
            if (!line.startsWith("- ")) {
                log.warn("{}行格式不正确: {}", curLineIndex + 1, line);
                break;
            }

            final Map<String, String> value = new LinkedHashMap<>();
            // 去掉开头的“-”
            line = line.substring(1).trim();
            String left  = StringUtils.left(line, line.indexOf(':')).trim();
            String right = line.substring(line.indexOf(':') + 1).trim();
            value.put(left, right);
            while (true) {
                curLineIndex++;
                if (curLineIndex == lines.length) {
                    break;
                }
                line = lines[curLineIndex];
                // Item行左边空格长度
                final int itemLeftSpaceLength = getLeftSpaceLength(line);
                if (itemLeftSpaceLength <= mapLineLeftSpaceLength) {
                    break;
                }
                line = line.trim();
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }

                left  = StringUtils.left(line, line.indexOf(':')).trim();
                right = line.substring(line.indexOf(':') + 1).trim();
                value.put(left, right);
            }
            result.add(value);
            curLineIndex--;
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    /**
     * 从YAML格式的字符串中设置匹配的key的值
     * 
     * @param content YAML格式的字符串
     * @param key     要设置的key，可以用“.”分割
     * 
     * @return 设置后的全文本内容
     */
    public static String setAsString(final String content, final String key, final String value) {
        final List<String> lines = yaml2StringList(content);
        setAsString(lines, key, value);
        return StringList2Yaml(lines);
    }

    /**
     * 从YAML格式的字符串中设置匹配的key的值
     * 
     * @param lines YAML格式的字符串按换行符分割后的List
     * @param key   要设置的key，可以用“.”分割
     * @param value 要设置的值
     * 
     * @return 对应Key的值，如果没有找到则返回null
     */
    public static void setAsString(final List<String> lines, final String key, final String value) {
        final int    keyLineIndex = getOrCreateLineIndex(lines, key);
        final String oldValue     = lines.get(keyLineIndex);
        final String newValue     = StringUtils.left(oldValue, oldValue.indexOf(':') + 1) + " " + value;
        lines.set(keyLineIndex, newValue);
    }

    /**
     * 从YAML格式的字符串中设置匹配的key的值
     * 
     * @param content YAML格式的字符串
     * @param key     要查找的key，可以用“.”分割
     * @param value   要设置的值
     * 
     * @return 设置后的全文本内容
     */
    public static String setAsStringList(final String content, final String key, final List<String> value) {
        final List<String> lines                  = yaml2StringList(content);
        final int          keyLineIndex           = getOrCreateLineIndex(lines, key);
        final String       keyLine                = lines.get(keyLineIndex);
        final int          keyLineLeftSpaceLength = getLeftSpaceLength(keyLine);
        final int          keysCount              = getKeysCount(key);
        final int          spaces                 = keyLineLeftSpaceLength / (keysCount - 1);

        // 删除旧的行
        for (int lineIndex = keyLineIndex + 1, length = lines.size(); lineIndex < length; lineIndex++) {
            String    line            = lines.get(lineIndex);
            final int leftSpaceLength = getLeftSpaceLength(line);
            line = line.trim();
            if (leftSpaceLength > keyLineLeftSpaceLength || ((leftSpaceLength == keyLineLeftSpaceLength) && !line.startsWith("- "))) {
                lines.remove(lineIndex);
                length--;
                lineIndex--;
            }
            else {
                break;
            }
        }

        // 添加新行
        for (int valueIndex = 0; valueIndex < value.size(); valueIndex++) {
            lines.add(keyLineIndex + valueIndex + 1, StringUtils.repeat(' ', keysCount * spaces) + "- " + value.get(valueIndex));
        }

        return StringList2Yaml(lines);
    }

    /**
     * 从YAML格式的字符串中设置匹配的key的值
     * 
     * @param content YAML格式的字符串
     * @param key     要查找的key，可以用“.”分割
     * @param value   要设置的值
     * 
     * @return 设置后的全文本内容
     */
    public static String setAsMapList(final String content, final String key, final List<Map<String, String>> value) {
        final List<String> lines                  = yaml2StringList(content);
        final int          keyLineIndex           = getOrCreateLineIndex(lines, key);
        final String       keyLine                = lines.get(keyLineIndex);
        final int          keyLineLeftSpaceLength = getLeftSpaceLength(keyLine);
        final int          keysCount              = getKeysCount(key);
        final int          spaces                 = keyLineLeftSpaceLength / (keysCount - 1);

        // 删除旧的行
        for (int lineIndex = keyLineIndex + 1, length = lines.size(); lineIndex < length; lineIndex++) {
            String    line            = lines.get(lineIndex);
            final int leftSpaceLength = getLeftSpaceLength(line);
            line = line.trim();
            if (leftSpaceLength > keyLineLeftSpaceLength || ((leftSpaceLength == keyLineLeftSpaceLength) && !line.startsWith("- "))) {
                lines.remove(lineIndex);
                length--;
                lineIndex--;
            }
            else {
                break;
            }
        }

        int index = keyLineIndex + 1;
        // 添加新行
        for (int valueIndex = 0; valueIndex < value.size(); valueIndex++) {
            final Map<String, String>                 map     = value.get(valueIndex);

            int                                       i       = 0;
            final Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
            while (entries.hasNext()) {
                final String                    prefix = i == 0 ? "- " : "  ";
                final Map.Entry<String, String> entry  = entries.next();
                lines.add(index, StringUtils.repeat(' ', keysCount * spaces) + prefix + entry.getKey() + ": " + entry.getValue());
                i++;
                index++;
            }
        }

        return StringList2Yaml(lines);
    }

    private static List<String> yaml2StringList(final String content) {
        return Stream.of(yaml2StringArray(content)).collect(Collectors.toList());
    }

    private static String[] yaml2StringArray(final String content) {
        return content.split("\\n");
    }

    private static String StringList2Yaml(final List<String> lines) {
        return lines.stream().collect(Collectors.joining("\n"));
    }

    /**
     * 获取左边空格长度
     * 
     * @param line 行内容
     * 
     * @return 左边空格长度
     */
    private static int getLeftSpaceLength(final String line) {
        return line.replaceAll("([ ]*).*", "$1").length();
    }

    private static String[] getKeys(final String key) {
        return key.split("\\.");
    }

    private static int getKeysCount(final String key) {
        return getKeys(key).length;
    }
}
