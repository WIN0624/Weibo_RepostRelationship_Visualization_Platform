package com.weibo.weibo.controller;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.weibo.weibo.util.loadJsonFile;
import java.io.File;

@Slf4j
@SpringBootTest()
public class WeiboControllerTest {

    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;
    @Value("classpath:static/testFiles/getWeibo01.json")
    private Resource testWeibo01;
    @Value("classpath:static/testFiles/getRelationship01.json")
    private Resource testRelationship01;

    @BeforeEach
    public void setup(){
        log.info("测试开始...");
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void query() throws Exception{
        // 使用mockmvc来模拟请求并接收结果
        MvcResult mvcResult = null;
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/getWeibo/新冠/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        // 提取结果中的字符串
        String result = mvcResult.getResponse().getContentAsString();
        // 测试希望匹配的答案（暂时不知道答案是什么就先放着，先保证代码跑通）
        File file = testWeibo01.getFile();
        String answer = loadJsonFile.parseJson(file);
        // 由于json生成字符串每次同级的顺序都不一样，因此需要用这个JSONAssert，需要导入特定依赖
        JSONAssert.assertEquals(answer, result, false);
    }

    @Test
    public void relationship() throws Exception{
        // 使用mockmvc来模拟请求并接收结果
        MvcResult mvcResult = null;
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/getRelationship/4529066247461463")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        // 提取结果中的字符串
        String result = mvcResult.getResponse().getContentAsString();
        // 测试希望匹配的答案（暂时不知道答案是什么就先放着，先保证代码跑通）
        File file = testRelationship01.getFile();
        String answer = loadJsonFile.parseJson(file);
        // 由于json生成字符串每次同级的顺序都不一样，因此需要用这个JSONAssert，需要导入特定依赖
        JSONAssert.assertEquals(answer, result, false);
    }




}
