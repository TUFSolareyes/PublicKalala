package com.letmesee.www.service.impl;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.pojo.TextInfoPacking;
import com.letmesee.www.pojo.TextPacking;
import com.letmesee.www.util.Jackson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 缓存服务层
 */
@Service
public class CacheServiceImpl {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private Jackson jackson;

    public ResultVO putSearchResultCache(String word,int pageCount,int maxPageCount,int maxCount,List<TextInfoPacking> data){
        String jsonStr = jackson.getJsonStr(data);
        stringRedisTemplate.opsForHash().put("searchResultCache:"+word,String.valueOf(pageCount),jsonStr);
        stringRedisTemplate.opsForHash().put("searchResultCache:"+word,"maxPageCount",String.valueOf(maxPageCount));
        stringRedisTemplate.opsForHash().put("searchResultCache:"+word,"maxCount",String.valueOf(maxCount));
        if(pageCount<=3){
            stringRedisTemplate.expire("searchResultCache:"+word,1000*10,TimeUnit.MILLISECONDS);
        }else{
            stringRedisTemplate.expire("searchResultCache:"+word,1000*2,TimeUnit.MILLISECONDS);
        }

        return new ResultVO(ResultVO.OK,"ok",null);
    }


    public ResultVO putTextCache(String textId, TextPacking textPacking){
        String jsonStr = jackson.getJsonStr(textPacking);
        stringRedisTemplate.opsForValue().set("textResultCache:"+textId,jsonStr,1000*10, TimeUnit.MILLISECONDS);
        return new ResultVO(ResultVO.OK,"ok",null);
    }


    public ResultVO selectSearchResultCache(String word,int pageCount){
        long start = System.currentTimeMillis();
        if(!stringRedisTemplate.opsForHash().hasKey("searchResultCache:"+word,String.valueOf(pageCount))){
            return new ResultVO(ResultVO.EXCEPTION,"empty",null);
        }
        String jsonStr = (String) stringRedisTemplate.opsForHash().get("searchResultCache:" + word, String.valueOf(pageCount));
        String maxPageCountStr = (String) stringRedisTemplate.opsForHash().get("searchResultCache:" + word,"maxPageCount");
        String maxCountStr = (String) stringRedisTemplate.opsForHash().get("searchResultCache:" + word,"maxCount");
        stringRedisTemplate.expire("searchResultCache:" + word,1000*10,TimeUnit.MILLISECONDS);
        JsonIterator jsonIterator = JsonIterator.parse(jsonStr);
        try {
            Any any = jsonIterator.readAny();
            List<TextInfoPacking> as = any.as(List.class);
            long end = System.currentTimeMillis();
            return new ResultVO(ResultVO.OK,(end-start)+","+maxCountStr+","+maxPageCountStr,as);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResultVO(ResultVO.EXCEPTION,"no",null);
    }


    public ResultVO selectTextCache(String textId){
        if(!stringRedisTemplate.hasKey("textResultCache:"+textId)){
            return new ResultVO(ResultVO.EXCEPTION,"empty",null);
        }
        String jsonStr = stringRedisTemplate.opsForValue().get("textResultCache:"+textId);
        stringRedisTemplate.expire("textResultCache:"+textId,1000*10,TimeUnit.MILLISECONDS);
        JsonIterator jsonIterator = JsonIterator.parse(jsonStr);
        try {
            Any any = jsonIterator.readAny();
            TextPacking as = any.as(TextPacking.class);
            return new ResultVO(ResultVO.OK,"ok",as);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResultVO(ResultVO.EXCEPTION,"no",null);
    }

}
