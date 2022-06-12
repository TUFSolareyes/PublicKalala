package com.letmesee_picimpl.www.controller;


import com.letmesee_picimpl.www.pojo.ResultVO;
import com.letmesee_picimpl.www.service.IndexServiceImpl;
import com.letmesee_picimpl.www.util.Jackson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private IndexServiceImpl indexService;

    @Autowired
    private Jackson jackson;

    @RequestMapping("/addIndex.go")
    @ResponseBody
    public String addIndex(String url,String content){
        ResultVO resultVO = indexService.addIndex(url, content);
        return jackson.getJsonStr(resultVO);
    }

}
