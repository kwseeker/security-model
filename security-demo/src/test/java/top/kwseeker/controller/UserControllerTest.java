package top.kwseeker.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void register() {
    }

    @Test
    public void create() throws Exception {
        Date date = new Date();
        String content = "{\"username\":\"Eric\", \"password\":\"123456\", \"birthday\":"+date.getTime()+"}";
        //System.out.println("new user created: " + content);
        String result = mockMvc.perform(MockMvcRequestBuilders
                .post("/user")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        System.out.println(result);
    }

    @Test
    public void query() throws Exception {
        // TODO: MockMvc 使用详解
        // 模拟发送Get请求
        String result = mockMvc.perform(MockMvcRequestBuilders
                .get("/user")
//                .param("username", "Arvin")
                .param("username", "Arvin")
                .param("ageBegin", "18")
                .param("ageEnd", "35")
                .param("xxx", "yyy")
//                .param("size", "15")    //每页15条数据
//                .param("page", "3")     //查询从第3页开始
//                .param("sort", "age,desc")  //查询结果以age降序排序
                .contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(4))
            .andReturn().getResponse().getContentAsString();  //关于 jsonPath()的使用： https://github.com/json-path/JsonPath

        System.out.println(result);
    }

    @Test
    public void getInfo() throws Exception {
        String result = mockMvc.perform(MockMvcRequestBuilders
                .get("/user/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();    //返回请求内容

        System.out.println(result);
    }

    @Test
    public void whenGetInfoFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/user/a")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateInfo() throws Exception {
        Date date = new Date(LocalDateTime.now().plusYears(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        String content = "{\"id\":\"1\", \"username\":\"ATooLongNameExample\",\"password\":null,\"birthday\":"+date.getTime()+"}";
        mockMvc.perform(MockMvcRequestBuilders
                .put("/user/1").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.username").isString());
    }

    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/user/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }
}