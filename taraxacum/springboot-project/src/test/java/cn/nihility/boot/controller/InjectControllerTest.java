package cn.nihility.boot.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author dandelion
 * @date 2020-09-05 16:44
 */
@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(InjectController.class)
class InjectControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void inject() throws Exception {
        String contentAsString = mvc.perform(get("/inject/my").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        System.out.println(contentAsString);
    }
}