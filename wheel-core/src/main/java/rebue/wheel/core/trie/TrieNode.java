package rebue.wheel.core.trie;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
class TrieNode<V> {
    /**
     * 是否为叶子节点
     */
    private Boolean           isLeaf   = false;
    /**
     * 键
     */
    private String            key;
    /**
     * 值
     */
    private V                 value;
    /**
     * 子节点
     */
    private List<TrieNode<V>> children = new LinkedList<>();

    public static <V> TrieNode<V> of(String key) {
        TrieNode<V> node = new TrieNode<>();
        node.key = key;
        return node;
    }

    public static <V> TrieNode<V> of(String key, V value) {
        TrieNode<V> node = new TrieNode<>();
        node.key   = key;
        node.value = value;
        return node;
    }

    public static <V> TrieNode<V> of(boolean isLeaf, String key, V value) {
        TrieNode<V> node = new TrieNode<>();
        node.isLeaf = isLeaf;
        node.key    = key;
        node.value  = value;
        return node;
    }
}
