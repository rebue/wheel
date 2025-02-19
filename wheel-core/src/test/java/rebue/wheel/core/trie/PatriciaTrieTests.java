package rebue.wheel.core.trie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class PatriciaTrieTests {
    final static PatriciaTrie<String> trie = new PatriciaTrie<>();

    @BeforeAll
    static void beforeAll() {
        trie.put("a", "A");
        trie.put("aa", "AA");
        trie.put("aaa", "AAA");
        trie.put("ab", "AB");
        trie.put("abc", "ABC");
        trie.put("abcabc", "ABCABC");
        trie.put("abcdef", "ABCDEF");
        trie.put("b", "B");
        trie.put("c", "C");
        trie.put("d", "D");
        trie.put("GET:/abc", "GET ABC");
        trie.put("GET:/def", "GET DEF");
    }

    @Test
    public void test01_simple() {
        Assertions.assertNull(trie.get("/"));
        Assertions.assertEquals("A", trie.get("a"));
        Assertions.assertEquals("B", trie.get("b"));
        Assertions.assertEquals("C", trie.get("c"));
        Assertions.assertEquals("A", trie.get("ab"));
        Assertions.assertEquals("[A, AB]", trie.list("ab").toString());
        Assertions.assertEquals("[A, AB, ABC]", trie.list("abc").toString());
        Assertions.assertEquals("[A, AB, ABC, ABCABC]", trie.list("abcabc").toString());
        Assertions.assertEquals("[A, AB, ABC, ABCDEF]", trie.list("abcdef").toString());
        Assertions.assertEquals("[B]", trie.list("b").toString());
        Assertions.assertEquals("[C]", trie.list("c").toString());
        Assertions.assertEquals("[D]", trie.list("d").toString());
        Assertions.assertNull(trie.get("GET:/"));
        Assertions.assertEquals("GET ABC", trie.get("GET:/abc"));
        Assertions.assertEquals("GET ABC", trie.get("GET:/abc/def"));
        Assertions.assertNull(trie.list("GET:/"));
        Assertions.assertEquals("[GET DEF]", trie.list("GET:/def").toString());
    }

    @RepeatedTest(100000)
    public void test02_concurrent() {
        String node = trie.get("GET:/abc/def");
        System.out.println(Thread.currentThread().getName() + ": " + node);
        Assertions.assertEquals("GET ABC", node);
    }

}
