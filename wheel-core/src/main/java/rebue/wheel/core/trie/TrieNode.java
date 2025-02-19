package rebue.wheel.core.trie;

import java.util.LinkedList;
import java.util.List;

import jakarta.annotation.Nonnull;
import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
final class TrieNode<V> {
    /**
     * 是否为叶子节点
     */
    @Nonnull
    private Boolean           isLeaf;
    /**
     * 键
     */
    @Nonnull
    private String            key;
    /**
     * 值
     */
    private V                 value;
    /**
     * 子节点
     */
    @Builder.Default
    private List<TrieNode<V>> children = new LinkedList<>();

    /**
     * 构造一个非叶子节点，且此节点的值为null(用于构造中间路径的节点)
     * 
     * @param key 键
     * @return 节点
     */
    public static <V> TrieNode<V> of(final String key) {
        return TrieNode.of(false, key);
    }

    /**
     * 构造一个非叶子节点，且此节点的值为null(用于构造中间路径的节点)
     * 
     * @param isLeaf 是否为叶子节点
     * @param key    键
     * @param value  值
     * @return 节点
     */
    public static <V> TrieNode<V> of(boolean isLeaf, String key, V value) {
        return TrieNode.<V>builder()
                .isLeaf(isLeaf)
                .key(key)
                .value(value)
                .build();
    }

}
