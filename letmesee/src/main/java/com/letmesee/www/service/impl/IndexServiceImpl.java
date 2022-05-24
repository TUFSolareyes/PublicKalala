package com.letmesee.www.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.letmesee.LetMeSeeApplication;
import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.pojo.TextInfoPacking;
import com.letmesee.www.pojo.TextPacking;
import com.letmesee.www.pojo.WordPacking;
import com.letmesee.www.util.Jackson;
import com.letmesee.www.util.JiebaUtil;
import com.letmesee.www.util.SomeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IndexServiceImpl {

    @Autowired
    private SomeUtils someUtils;

    @Autowired
    private Jackson jackson;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    Map<String, List<Long>> inverted = LetMeSeeApplication.INVERTED;

    Map<Long,Map<String,WordPacking>> forward = LetMeSeeApplication.FORWARD;

    Map<Long,TextInfoPacking> textInfo = LetMeSeeApplication.TEXTINFO;

    /**
     * 添加索引信息
     * @param title 标题
     * @param content 内容
     * @param dataJsonStr 附带数据的json字符串格式
     * @return
     */
    public ResultVO addTextIndex(String title, String content, String dataJsonStr) throws JsonProcessingException {

        //分词处理
        Map<String, WordPacking> map = JiebaUtil.jiebaWebText(title,content);

        //生成文本唯一id
        long textId = someUtils.getNextId();

        //生成文本信息类
        TextInfoPacking textInfoPacking = new TextInfoPacking(textId,title,SomeUtils.getSummary(content),dataJsonStr);
        //生成文本本体类
        TextPacking textPacking = new TextPacking(textId,title,content);

        //先将文本本体直接刷到数据库
        mongoTemplate.insert(textPacking,"text_1");

        //先将文本信息加入内存和增量缓存
        String jsonStr = jackson.getObjectMapper().writeValueAsString(textInfoPacking);
        stringRedisTemplate.opsForValue().set("fo:"+textId,jsonStr);
        textInfo.put(textId,textInfoPacking);

        //将增量数据加入jvm内存中
        //增量倒排索引
        Set<String> words = map.keySet();
        Iterator<String> iterator = words.iterator();
        while(iterator.hasNext()){
            String word = iterator.next();
            if(!inverted.containsKey(word)){
                synchronized (this){
                    if(!inverted.containsKey(word)){
                        //创建新的键值对
                        List<Long> list = Collections.synchronizedList(new LinkedList<>());
                        list.add(textId);
                        inverted.put(word,list);
                    }else{
                        inverted.get(word).add(textId);
                    }
                }
            }else{
                inverted.get(word).add(textId);
            }
        }

        //增量正排索引
        forward.put(textId,map);


        //将增量数据加入redis中，等待持久化
        Iterator<String> iterator2 = words.iterator();

        //将倒排数据加入redis中
        while(iterator2.hasNext()){
            String word = iterator2.next();
            stringRedisTemplate.opsForList().leftPush("w:"+word,String.valueOf(textId));
        }

        //将正排索引加入redis中
        String jsonStr2 = jackson.getObjectMapper().writeValueAsString(map);
        stringRedisTemplate.opsForValue().set("tid:"+textId,jsonStr2);

        System.out.println("已经成功添加索引: ");
        System.out.println("当前倒排索引链数量: "+inverted.size());
        System.out.println("当前正排索引链数量: "+forward.size());
        System.out.println("当前文本简介信息数量: "+textInfo.size());
        System.out.println("当前剩余内存空间: "+Runtime.getRuntime().freeMemory());
        return new ResultVO(4000,"ok",null);
    }


    public ResultVO addPicIndex(String url,String content){


        return null;
    }


    /**
     * 删除索引（会删除索引对应的所有文章！！！）
     * @param keyword
     * @return
     */
    public ResultVO delIndex(String keyword){
        return null;
    }


    /**
     * 获取当前倒排索引列表
     * @param pageCount
     * @param limitCount
     * @return
     */
    public ResultVO getAllInvertedIndex(int pageCount,int limitCount){
        return null;
    }



}
