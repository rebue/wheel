package rebue.wheel.core.trie;

import org.junit.jupiter.api.Test;

public class PatriciaTrieTests {
    @Test
    public void test01() {
        // Create a new PatriciaTrie object
        PatriciaTrie<String> trie = new PatriciaTrie<>();

        // Insert key-value pairs into the trie
        trie.put("a", "A");
        trie.put("aa", "AA");
        trie.put("aaa", "AAA");
        trie.put("ab", "AB");
        trie.put("abc", "ABC");
        trie.put("b", "B");
        trie.put("c", "C");
        trie.put("d", "d");

        // Retrieve values from the trie
        System.out.println(trie.get("/"));              // Output: null
        System.out.println(trie.get("a"));              // Output: A
        System.out.println(trie.get("b"));              // Output: B
        System.out.println(trie.get("c"));              // Output: C
        System.out.println(trie.get("ab"));             // Output: A
        System.out.println(trie.list("ab"));            // Output: A,AB
        System.out.println(trie.list("abc"));           // Output: A,AB,ABC
        System.out.println(trie.list("abcdef"));        // Output: A,AB,ABC
    }

}
