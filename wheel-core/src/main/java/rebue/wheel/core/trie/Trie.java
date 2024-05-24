package rebue.wheel.core.trie;

import java.util.List;

public interface Trie<V> {

    /**
     * 加入元素
     *
     * @param key   键
     * @param value 值
     */
    void put(String key, V value);

    /**
     * 获取元素
     *
     * @param key 键
     * @return 获取到的元素，如果没有找到则返回null
     */
    V get(String key);

    /**
     * 获取元素列表
     *
     * @param key 键
     * @return 获取到的元素列表，如果没有找到则返回null
     */
    List<V> list(String key);
}
