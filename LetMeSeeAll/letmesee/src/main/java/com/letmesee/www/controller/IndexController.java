package com.letmesee.www.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.pojo.TextInfoPacking;
import com.letmesee.www.pojo.TextPacking;
import com.letmesee.www.service.SearchService;
import com.letmesee.www.service.impl.CacheServiceImpl;
import com.letmesee.www.service.impl.IndexServiceImpl;
import com.letmesee.www.service.impl.ProcessingRecord;
import com.letmesee.www.service.impl.SearchPicServiceImpl;
import com.letmesee.www.util.Jackson;

import com.letmesee.www.util.SomeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;


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
    private CacheServiceImpl cacheService;

    @Autowired
    private Jackson jackson;

    @PostMapping("/addIndex.go")
    @ResponseBody
    public String addIndex(String title,String content,String dataJsonStr){
        try {
            return jackson.getJsonStr(indexService.addTextIndex(title, content, dataJsonStr));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jackson.getJsonStr(new ResultVO(ResultVO.EXCEPTION,"no",null));
    }


    @RequestMapping("/addPic.go")
    @ResponseBody
    public String addPic(String url,String content){
        try {
            return jackson.getJsonStr(indexService.addPicIndex(url, content));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jackson.getJsonStr(new ResultVO(ResultVO.EXCEPTION,"no",null));
    }


    @RequestMapping("/search.do")
    @ResponseBody
    public String search(HttpServletRequest request) {
        String text = request.getParameter("text");
        String fwords = request.getParameter("fwords");
        String pageCountStr = request.getParameter("pageCountStr");
        String limitCountStr = request.getParameter("limitCountStr");
        if(SomeUtils.isNullorEmpty(text,pageCountStr,limitCountStr)){
            return jackson.getJsonStr(new ResultVO(ResultVO.EXCEPTION,"请输入完整参数",null));
        }
        try {
            Integer pageCount = Integer.parseInt(pageCountStr);
            Integer limitCount = Integer.parseInt(limitCountStr);

            if(SomeUtils.isNullorEmpty(fwords)){
                ResultVO rv01 = cacheService.selectSearchResultCache(text, pageCount);
                if(rv01!=null&&rv01.getCode()==ResultVO.OK){
                    return jackson.getJsonStr(rv01);
                }
            }
            ResultVO rv02 = searchServiceImplProxy.searchTextByText(text, pageCount, limitCount, fwords.split(" "));
            if(SomeUtils.isNullorEmpty(fwords)){
                if(rv02.getCode()==ResultVO.OK){
                    String[] temp = rv02.getMsg().split(",");
                    Integer maxCount = Integer.parseInt(temp[1]);
                    Integer maxPageCount = Integer.parseInt(temp[2]);
                    cacheService.putSearchResultCache(text,pageCount,maxPageCount,maxCount,(List<TextInfoPacking>) rv02.getData());
                }
            }
            return jackson.getJsonStr(rv02);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return jackson.getJsonStr(new ResultVO(ResultVO.EXCEPTION,"no",null));
    }


    @RequestMapping("/getRelatedSearch.do")
    @ResponseBody
    public String getRelatedSearch(String text){
        return jackson.getJsonStr(processingRecord.getRelatedSearch(text));
    }


    @GetMapping("/getText.do")
    @ResponseBody
    public String getText(String textId){
        try{
            ResultVO rv01 = cacheService.selectTextCache(textId);
            if(rv01!=null&&rv01.getCode()==ResultVO.OK){
                return jackson.getJsonStr(rv01);
            }
            ResultVO rv02 = searchServiceImplProxy.getText(textId);
            if(rv02.getCode()==ResultVO.OK){
                cacheService.putTextCache(textId, (TextPacking) rv02.getData());
            }
            return jackson.getJsonStr(rv02);
        }catch (NumberFormatException e){
        }
        return jackson.getJsonStr(new ResultVO(ResultVO.EXCEPTION,"no",null));
    }



    @RequestMapping("/searchPic.do")
    @ResponseBody
    public String searchPic(String text,String pageCountStr,String limitCountStr){
        try {
            return jackson.getJsonStr(searchPicService.searchPic(text, pageCountStr, limitCountStr));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jackson.getJsonStr(new ResultVO(ResultVO.EXCEPTION,"no",null));
    }


    @RequestMapping("delText.de")
    @ResponseBody
    public String delText(String tid){
        return jackson.getJsonStr(indexService.delText(tid));
    }

}

