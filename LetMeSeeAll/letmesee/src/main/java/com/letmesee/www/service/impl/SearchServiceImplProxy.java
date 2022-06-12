package com.letmesee.www.service.impl;

import com.letmesee.LetMeSeeApplication;
import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.pojo.SearchPreLog;
import com.letmesee.www.pojo.TextInfoPacking;
import com.letmesee.www.service.SearchService;
import com.letmesee.www.util.Jackson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SearchServiceImplProxy implements SearchService {


    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private SearchService searchServiceImpl;

    @Autowired
    public void setSearchServiceImpl(SearchService searchServiceImpl) {
        this.searchServiceImpl = searchServiceImpl;
    }


    private Jackson jackson;

    @Autowired
    public void setJackson(Jackson jackson) {
        this.jackson = jackson;
    }

    @Override
    public ResultVO searchTextByText(String text, int pageCount, int limitCount, String[] fwords) {

        //调用被代理方法
        ResultVO res = searchServiceImpl.searchTextByText(text, pageCount, limitCount, fwords);
        if(pageCount==1) {
            ArrayList<TextInfoPacking> temp = (ArrayList) res.getData();
            LetMeSeeApplication.T_POOL.execute(new SearchPreLogger(stringRedisTemplate,temp,text,jackson));
        }

        return res;
    }

    @Override
    public ResultVO getText(String textId) {
        return searchServiceImpl.getText(textId);
    }

}



class SearchPreLogger implements Runnable{

    private StringRedisTemplate stringRedisTemplate;

    private ArrayList<TextInfoPacking> arr;

    private String text;

    private Jackson jackson;

    public SearchPreLogger(StringRedisTemplate stringRedisTemplate,ArrayList<TextInfoPacking> arr,String text,Jackson jackson){
        this.stringRedisTemplate = stringRedisTemplate;
        this.arr = arr;
        this.text = text;
        this.jackson = jackson;
    }

    @Override
    public void run() {
        ArrayList<String> cur = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            cur.add(arr.get(i).get_id());
        }
        SearchPreLog sl = new SearchPreLog(text, System.currentTimeMillis(), cur, "*");
        try {
            String jsonStr = jackson.getJsonStr(sl);
            stringRedisTemplate.opsForList().leftPush("log:" + System.currentTimeMillis() + ":", jsonStr);
        } catch (Throwable t){
            t.printStackTrace();
        }
    }
}
