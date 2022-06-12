package com.letmesee.www.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;


@Component
public class LanguageUtil {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Jackson jackson;

    public String getTranslate(String src){
        try {
            String fanyi = SomeUtils.getValueFromProFile("fanyi", "some/urls.properties");
            String jsonStr = restTemplate.getForObject(fanyi + src, String.class);
            JsonNode jsonNode = jackson.getJsonNode(jsonStr);
            return jsonNode.path("translateResult").get(0).get(0).path("tgt").asText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return src;
    }
}
