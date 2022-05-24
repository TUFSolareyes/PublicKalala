package com.letmesee_picimpl.www.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.letmesee_picimpl.www.pojo.ResultVO;
import com.letmesee_picimpl.www.service.SearchServiceImpl;
import com.letmesee_picimpl.www.util.Jackson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/search")
public class SearchPicController {

    @Autowired
    private SearchServiceImpl searchService;

    @Autowired
    private Jackson jackson;

    @RequestMapping("/searchPic")
    @ResponseBody
    public String searchPic(String text,String pageCountStr,String limitCountStr){
        try{
            Integer pageCount = Integer.parseInt(pageCountStr);
            Integer limitCount = Integer.parseInt(limitCountStr);
            ResultVO rv = searchService.searchPic(text, pageCount, limitCount);
            return jackson.getObjectMapper().writeValueAsString(rv);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "no";
    }
}
