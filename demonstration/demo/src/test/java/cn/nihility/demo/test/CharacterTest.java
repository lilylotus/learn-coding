package cn.nihility.demo.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CharacterTest {

    @Test
    void testChinese() {
        String ch = "中";
        int length = ch.length();
        System.out.println("len [" + length + "]");
        Assertions.assertEquals(1, length);

        char[] chars = ch.toCharArray();
        int len = chars.length;
        System.out.println("Char Array Len [" + len + "]");
        Assertions.assertEquals(1, len);

        char c = chars[0];
        System.out.println("char [" + c + "]");
        System.out.println(Integer.toHexString(c));
    }

}
