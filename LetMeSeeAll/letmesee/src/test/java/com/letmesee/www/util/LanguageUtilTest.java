package com.letmesee.www.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LanguageUtilTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Jackson jackson;

    @Test
    public void getTranslate(){
        String src = "你好";
        try {
            String fanyi = SomeUtils.getValueFromProFile("fanyi", "some/urls.properties");
            String jsonStr = restTemplate.getForObject(fanyi + src, String.class);
            JsonNode jsonNode = jackson.getJsonNode(jsonStr);
            String s = jsonNode.get("translateResult").get(0).get("tgt").asText();
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(src);
    }
}