package com.letmesee.www.controller;

import com.letmesee.www.Interceptor.Login;
import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.service.impl.UserServiceImpl;
import com.letmesee.www.util.Jackson;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/loginJudge")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private Jackson jackson;


    @RequestMapping("/login.do")
    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response){
        try {
            String userName = request.getParameter("userName");
            String pass = request.getParameter("pass");
            ResultVO rv = userService.login(userName,pass);
            String[] strings = (String[]) rv.getData();
            if(strings==null){
                return jackson.getJsonStr(new ResultVO(ResultVO.EXCEPTION,"no",null));
            }
            response.setHeader("re",strings[0]);
            response.setHeader("ac",strings[1]);
            return jackson.getJsonStr(rv);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jackson.getJsonStr(new ResultVO(ResultVO.EXCEPTION,"no",null));
    }


    @RequestMapping("/register.go")
    @ResponseBody
    public String register(String userName,String pass){
        return jackson.getJsonStr(userService.register(userName, pass));
    }


    @RequestMapping("/isLogin")
    @ResponseBody
    public String isLogin() {
        return jackson.getJsonStr(new ResultVO(ResultVO.OK,"ok",null));
    }


    @RequestMapping("/createStarCollection.go")
    @ResponseBody
    public String createStarCollection(String cname){
        String uid = Login.tl.get();
        return jackson.getJsonStr(userService.createStarCollection(uid,cname));
    }


    @RequestMapping("/addStarToCollection.go")
    @ResponseBody
    public String addStarToCollection(String cid,String tid){
        String uid = Login.tl.get();
        try{
            return jackson.getJsonStr(userService.addStarToCollection(uid,tid,cid));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return jackson.getJsonStr(new ResultVO(ResultVO.OK,"no",null));
    }


    @RequestMapping("/getStarCollections.do")
    @ResponseBody
    public String getStarCollections(){
        String uid = Login.tl.get();
        return jackson.getJsonStr(userService.getStarCollections(uid));
    }


    @RequestMapping("/exitMe.do")
    @ResponseBody
    public String exitMe(){
        String uid = Login.tl.get();
        return jackson.getJsonStr(userService.exitMe(uid));
    }


    @RequestMapping("/getStarFromCollection.do")
    @ResponseBody
    public String getStarFromCollection(String cid,String pageCountStr,String limitCountStr){
        String uid = Login.tl.get();
        try{
            Integer pageCount = Integer.parseInt(pageCountStr);
            Integer limitCount = Integer.parseInt(limitCountStr);
            return jackson.getJsonStr(userService.getStarFromCollection(cid,uid,pageCount,limitCount));
        }catch (NumberFormatException e){

        }
        return jackson.getJsonStr(new ResultVO(ResultVO.EXCEPTION,"no",null));
    }


    @RequestMapping("delStarFromCollection.de")
    @ResponseBody
    public String delStarFromCollection(String tid,String cid){
        String uid = Login.tl.get();
        return jackson.getJsonStr(userService.delStarFromCollection(uid, tid, cid));
    }


    @RequestMapping("delCollection.de")
    @ResponseBody
    public String delCollection(String cid){
        String uid = Login.tl.get();
        return jackson.getJsonStr(userService.delCollection(uid,cid));
    }

    @RequestMapping("/renameCollection.go")
    @ResponseBody
    public String renameCollection(String name,String cid){
        String uid = Login.tl.get();
        return jackson.getJsonStr(userService.renameCollection(uid,name, cid));
    }
}
