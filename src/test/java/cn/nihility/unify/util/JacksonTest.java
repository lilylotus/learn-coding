package cn.nihility.unify.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class JacksonTest {

    @Test
    public void testParseEnum() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        String parseValue = objectMapper.writeValueAsString(new TestEntity());
        System.out.println(parseValue);

    }


    static class TestEntity {
        TestEnum note = TestEnum.TEST;

        public TestEnum getNote() {
            return note;
        }

        public void setNote(TestEnum note) {
            this.note = note;
        }
    }

    enum TestEnum {
        TEST(1, "test note");

        private int value;
        private String note;

        TestEnum(int value, String note) {
            this.value = value;
            this.note = note;
        }
    }
}
