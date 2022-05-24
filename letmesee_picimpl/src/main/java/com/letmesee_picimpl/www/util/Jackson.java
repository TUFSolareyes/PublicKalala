package com.letmesee_picimpl.www.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class Jackson {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public ObjectMapper getObjectMapper(){
        return objectMapper;
    }
}
