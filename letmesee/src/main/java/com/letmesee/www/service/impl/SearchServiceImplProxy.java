package com.letmesee.www.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.pojo.SearchLog;
import com.letmesee.www.pojo.TextInfoPacking;
import com.letmesee.www.service.SearchService;
import com.letmesee.www.util.Jackson;
import com.letmesee.www.util.SomeUtils;
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
            if(temp.size()>0){
                ArrayList<Long> cur = new ArrayList<>();
                for (int i = 0; i < temp.size(); i++) {
                    cur.add(temp.get(i).get_id());
                }
                SearchLog sl = new SearchLog(text, System.currentTimeMillis(), cur, "*");
                try {
                    String jsonStr = jackson.getObjectMapper().writeValueAsString(sl);
                    stringRedisTemplate.opsForList().leftPush("log:" + System.currentTimeMillis() + ":", jsonStr);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }catch (Throwable t){
                    t.printStackTrace();
                }
            }
        }
        return res;
    }

}
