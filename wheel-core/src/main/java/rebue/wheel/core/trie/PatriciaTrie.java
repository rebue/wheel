package rebue.wheel.core.trie;

import java.util.LinkedList;
import java.util.List;

public class PatriciaTrie<V> implements Trie<V> {
    private final TrieNode<V> root = new TrieNode<>();

    /**
     * 叶子数量
     */
    private int               size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(String key, V value) {
        if (key == null || key.length() == 0) {
            return;
        }
        size++;
        put(root, key.trim(), value);
    }

    @Override
    public V get(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        return get(root, key);
    }

    @Override
    public List<V> list(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        List<V> list = new LinkedList<>();
        list(root, key, list);
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

    private void put(TrieNode<V> node, String key, V value) {
        // 循环遍历子节点
        LOOP_CHILDREN: for (int i = 0; i < node.getChildren().size(); i++) {
            TrieNode<V> child     = node.getChildren().get(i);
            String      childKey  = child.getKey();
            int         minLength = Math.min(childKey.length(), key.length());
            // 循环遍历子节点的key的字符
            for (int j = 0; j < minLength; j++) {
                // 比较字符
                int compared = key.charAt(j) - childKey.charAt(j);

                // 如果key的当前字符小于childKey的当前字符
                if (compared < 0) {
                    // 如果第一个字符就小了，直接在最前面插入一个新的子节点
                    if (j == 0) {
                        node.getChildren().add(j, TrieNode.of(true, key, value));
                        return;
                    }
                    // 如果之前有相同的字符，那么在此处插入一个新的叶子节点，并将当前子节点移动到新的叶子节点下
                    node.getChildren().remove(i);
                    TrieNode<V> newNode = TrieNode.of(childKey.substring(0, j));
                    newNode.getChildren().add(TrieNode.of(true, key.substring(j), value));
                    newNode.getChildren().add(child);
                    child.setKey(childKey.substring(j));
                    node.getChildren().add(i, newNode);
                    return;
                }
                // 如果key的当前字符大于childKey的当前字符
                else if (compared > 0) {
                    // 如果第一个字符就大了，继续查找下一个子节点，直到找到有相同的字符进行插入，如果直到遍历完所有子节点一直第一个字符就大了，那么在循环外再进行逻辑处理
                    if (j == 0) {
                        continue LOOP_CHILDREN;
                    }
                    // 如果之前有相同的字符，那么在此处插入一个新的叶子节点，并将当前子节点移动到新的叶子节点下
                    node.getChildren().remove(i);
                    TrieNode<V> newNode = TrieNode.of(childKey.substring(0, j));
                    child.setKey(childKey.substring(j));
                    newNode.getChildren().add(child);
                    newNode.getChildren().add(TrieNode.of(true, key.substring(j), value));
                    node.getChildren().add(i, newNode);
                    return;
                }
                // 如果是一样的字符，继续查找，直到找到不同的字符，如果遍历完所有字符也一直相等，那么在循环外再进行逻辑处理
            }
            // 如果遍历完所有字符，说明key与childKey前缀相同，做如下判断:
            // 1. key的长度等于childKey的长度，说明key和childKey是完全一样的，将此子节点设置为叶子节点
            // 2. key的长度小于childKey的长度，则分割childKey，添加前面部分为新子节点到父节点中，并设置为叶子节点，并将当前子节点移动到新子节点下
            // 3. key的长度大于childKey的长度，则分割key，递归调用本方法，继续在当前子节点中添加key
            int compared = key.length() - childKey.length();
            if (compared == 0) {
                if (!child.getIsLeaf()) {
                    size--;
                    child.setIsLeaf(true);
                }
                child.setValue(value);
                return;
            } else if (compared < 0) {
                TrieNode<V> newNode = TrieNode.of(true, key, value);
                node.getChildren().remove(i);
                child.setKey(childKey.substring(minLength));
                newNode.getChildren().add(child);
                node.getChildren().add(i, newNode);
                return;
            } else {
                put(child, key.substring(minLength), value);
                return;
            }
        }

        // 如果遍历完所有子节点，说明key的第一个字符大于所有子节点的第一个字符，那么就在父节点下添加一个新的子节点到末尾(也有可能是父节点没有子节点，同样如此处理)
        node.getChildren().add(TrieNode.of(true, key, value));
    }

    private V get(TrieNode<V> node, String key) {
        // 循环遍历子节点
        LOOP_CHILDREN: for (int i = 0; i < node.getChildren().size(); i++) {
            TrieNode<V> child     = node.getChildren().get(i);
            String      childKey  = child.getKey();
            int         minLength = Math.min(childKey.length(), key.length());
            // 循环遍历子节点的key的字符
            for (int j = 0; j < minLength; j++) {
                // 比较字符
                int compared = key.charAt(j) - childKey.charAt(j);
                // 如果key的当前字符小于childKey的当前字符，则说明没有此key，返回null
                if (compared < 0) {
                    return null;
                }
                // 如果key的当前字符大于childKey的当前字符
                else if (compared > 0) {
                    // 如果第一个字符就大了，继续查找下一个子节点，如果直到遍历完所有子节点一直第一个字符就大了，那么在循环外再进行逻辑处理
                    if (j == 0) {
                        continue LOOP_CHILDREN;
                    }
                    // 如果之前有相同的字符，那么说明没有此key，返回null
                    return null;
                }
                // 如果是一样的字符，继续查找，直到找到不同的字符，如果遍历完所有字符也一直相等，那么在循环外再进行逻辑处理
            }
            // 如果遍历完所有字符，说明key与childKey前缀相同，做如下判断:
            // 1. key的长度等于childKey的长度，说明key和childKey是完全一样的，返回此节点
            // 2. key的长度小于childKey的长度，说明没有此key，返回null
            // 3. key的长度大于childKey的长度，如果当前子节点已经是叶子节点了，返回此节点，否则还要继续在下一层中查找，递归调用本方法
            int compared = key.length() - childKey.length();
            if (compared == 0) {
                return child.getIsLeaf() ? child.getValue() : null;
            } else if (compared < 0) {
                return null;
            } else {
                return child.getIsLeaf() ? child.getValue() : get(child, key.substring(minLength));
            }
        }
        // 如果遍历完所有子节点，说明没有此key，返回null(也有可能是父节点没有子节点，同样如此处理)
        return null;
    }

    private void list(TrieNode<V> node, String key, List<V> list) {
        // 循环遍历子节点
        LOOP_CHILDREN: for (int i = 0; i < node.getChildren().size(); i++) {
            TrieNode<V> child     = node.getChildren().get(i);
            String      childKey  = child.getKey();
            int         minLength = Math.min(childKey.length(), key.length());
            // 循环遍历子节点的key的字符
            for (int j = 0; j < minLength; j++) {
                // 比较字符
                int compared = key.charAt(j) - childKey.charAt(j);
                // 如果key的当前字符小于childKey的当前字符，则说明没有此key，返回
                if (compared < 0) {
                    return;
                }
                // 如果key的当前字符大于childKey的当前字符
                else if (compared > 0) {
                    // 如果第一个字符就大了，继续查找下一个子节点，如果直到遍历完所有子节点一直第一个字符就大了，那么在循环外再进行逻辑处理
                    if (j == 0) {
                        continue LOOP_CHILDREN;
                    }
                    // 如果之前有相同的字符，那么说明没有此key，返回
                    return;
                }
                // 如果是一样的字符，继续查找，直到找到不同的字符，如果遍历完所有字符也一直相等，那么在循环外再进行逻辑处理
            }
            // 如果遍历完所有字符，说明key与childKey前缀相同，做如下判断:
            // 1. key的长度等于childKey的长度，说明key和childKey是完全一样的，添加此节点并返回
            // 2. key的长度小于childKey的长度，说明没有此key，返回
            // 3. key的长度大于childKey的长度，如果当前子节点已经是叶子节点了，添加此节点，然后还要继续在下一层中查找，递归调用本方法
            int compared = key.length() - childKey.length();
            if (compared == 0) {
                if (child.getIsLeaf()) {
                    list.add(child.getValue());
                }
                return;
            } else if (compared < 0) {
                return;
            } else {
                if (child.getIsLeaf()) {
                    list.add(child.getValue());
                }
                list(child, key.substring(minLength), list);
            }
        }
        // 如果遍历完所有子节点，说明没有此key，返回(也有可能是父节点没有子节点，同样如此处理)
        // return;
    }
}
