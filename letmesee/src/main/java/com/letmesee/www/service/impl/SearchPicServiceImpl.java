package com.letmesee.www.service.impl;


import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.letmesee.www.pojo.PicPacking;
import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.util.Jackson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class SearchPicServiceImpl {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Jackson jackson;

    public ResultVO searchPic(String text,String pageCountStr,String limitCountStr) throws IOException {
        System.out.println("http://localhost:8082/search/searchPic?text="+text+"&pageCountStr="+pageCountStr+"&limitCountStr="+limitCountStr);
        String s = restTemplate.getForObject("http://localhost:8082/search/searchPic?text="+text+"&pageCountStr="+pageCountStr+"&limitCountStr="+limitCountStr, String.class);
        JsonIterator parse = JsonIterator.parse(s);
        Any any = parse.readAny();
        ResultVO as = any.as(ResultVO.class);
        return as;
    }
}
