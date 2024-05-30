package rebue.wheel.core.trie;

import java.util.List;

public interface Trie<V> {
    /**
     * 获取叶子数量
     * 
     * @return 叶子数量
     */
    int size();

    /**
     * 是否没有叶子
     *
     * @return 是否没有叶子
     */
    default boolean isEmpty() {
        return size() == 0;
    }

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
