package com.letmesee.www.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.letmesee.LetMeSeeApplication;
import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.service.SearchService;
import com.letmesee.www.service.impl.IndexServiceImpl;
import com.letmesee.www.service.impl.ProcessingRecord;
import com.letmesee.www.service.impl.SearchPicServiceImpl;
import com.letmesee.www.service.impl.UserServiceImpl;
import com.letmesee.www.util.Jackson;

import com.letmesee.www.util.SomeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Controller
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private IndexServiceImpl indexService;

    @Resource
    private SearchService searchServiceImplProxy;

    @Autowired
    private SearchPicServiceImpl searchPicService;

    @Autowired
    private ProcessingRecord processingRecord;

    @Autowired
    private Jackson jackson;

    @RequestMapping("/addIndex.go")
    @ResponseBody
    public String addIndex(String title,String content,String dataJsonStr){
        try {
            return jackson.getJsonStr(indexService.addTextIndex(title, content, dataJsonStr));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jackson.getJsonStr(new ResultVO(4002,"no",null));
    }


    @RequestMapping("/search.do")
    @ResponseBody
    public String search(HttpServletRequest request) {
        String text = request.getParameter("text");
        String fwords = request.getParameter("fwords");
        String pageCountStr = request.getParameter("pageCountStr");
        String limitCountStr = request.getParameter("limitCountStr");
        if(SomeUtils.isNullorEmpty(text,pageCountStr,limitCountStr)){
            return jackson.getJsonStr(new ResultVO(4002,"请输入完整参数",null));
        }
        try {
            Integer pageCount = Integer.parseInt(pageCountStr);
            Integer limitCount = Integer.parseInt(limitCountStr);
            return jackson.getJsonStr(searchServiceImplProxy.searchTextByText(text, pageCount, limitCount, fwords.split(" ")));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return jackson.getJsonStr(new ResultVO(4002,"no",null));
    }


    @RequestMapping("/getRelatedSearch.do")
    @ResponseBody
    public String getRelatedSearch(String text){
        return jackson.getJsonStr(processingRecord.getRelatedSearch(text));
    }



    @RequestMapping("/searchPic.do")
    @ResponseBody
    public String searchPic(String text,String pageCountStr,String limitCountStr){
        try {
            return jackson.getJsonStr(searchPicService.searchPic(text, pageCountStr, limitCountStr));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jackson.getJsonStr(new ResultVO(4002,"no",null));
    }


}

