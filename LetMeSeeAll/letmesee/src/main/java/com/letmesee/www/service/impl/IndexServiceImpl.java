package com.letmesee.www.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.letmesee.LetMeSeeApplication;
import com.letmesee.www.pojo.*;
import com.letmesee.www.util.Jackson;
import com.letmesee.www.util.JiebaUtil;
import com.letmesee.www.util.LogUtil;
import com.letmesee.www.util.SomeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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

    @Autowired
    private RestTemplate restTemplate;

    Map<String, List<String>> inverted = LetMeSeeApplication.INVERTED;

    Map<String, Map<String, WordPacking>> forward = LetMeSeeApplication.FORWARD;

    Map<String, TextInfoPacking> textInfo = LetMeSeeApplication.TEXTINFO;

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
        String textId = String.valueOf(someUtils.getNextId());

        //生成文本信息类
        TextInfoPacking textInfoPacking = new TextInfoPacking(textId,title,SomeUtils.getSummary(content),dataJsonStr);
        //生成文本本体类
        TextPacking textPacking = new TextPacking(textId,title,content);

        //先将文本本体直接刷到数据库
        mongoTemplate.insert(textPacking,"text_1");

        //先将文本信息加入内存和增量缓存
        String jsonStr = jackson.getJsonStr(textInfoPacking);
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
                        List<String> list = Collections.synchronizedList(new LinkedList<>());
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
            stringRedisTemplate.opsForList().leftPush("w:"+word,textId);
        }

        //将正排索引加入redis中
        String jsonStr2 = jackson.getJsonStr(map);
        stringRedisTemplate.opsForValue().set("tid:"+textId,jsonStr2);

        System.out.println("已经成功添加索引: ");
        LogUtil.logMessage("当前倒排索引链数量: "+inverted.size(),LogUtil.INFO);
        LogUtil.logMessage("当前正排索引链数量: "+forward.size(),LogUtil.INFO);
        LogUtil.logMessage("当前文本简介信息数量: "+textInfo.size(),LogUtil.INFO);
        LogUtil.logMessage("当前剩余内存空间: "+Runtime.getRuntime().freeMemory(),LogUtil.INFO);
        LogUtil.logMessage("文本id:"+textId,LogUtil.INFO);
        return new ResultVO(ResultVO.OK,"ok",null);
    }


    public ResultVO addPicIndex(String url,String content) throws IOException {
        String s = restTemplate.getForObject("http://localhost:8082/index/addIndex.go?url="+url+"&content="+content,String.class);
        JsonIterator parse = JsonIterator.parse(s);
        Any any = parse.readAny();
        ResultVO as = any.as(ResultVO.class);
        return as;
    }


    /**
     * 删除某个文档（会删除文档对应的所有索引！！！）
     * @param textId
     * @return
     */
    public ResultVO delText(String textId){

        //删除内存中的索引
        Map<String, WordPacking> map = forward.get(textId);
        Iterator<String> iterator = map.keySet().iterator();
        while(iterator.hasNext()){
            String keyword = iterator.next();
            List<String> list = inverted.get(keyword);
            list.remove(textId);
            Criteria criteria = Criteria.where("_id").is(keyword);
            Query query = new Query(criteria);
            mongoTemplate.updateFirst(query,new Update().pull("tids",textId),"inv_1");
        }
        forward.remove(textId);
        textInfo.remove(textId);
        Criteria criteria = Criteria.where("_id").is(textId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query,"for_1");
        mongoTemplate.remove(query,"fo_1");
        mongoTemplate.remove(query,"text_1");
        return new ResultVO(ResultVO.OK,"ok",null);
    }


    /**
     * 获取当前文本索引信息
     * @return
     */
    public ResultVO getInvertedIndexInfo(){

        boolean redisOk = true;

        boolean databaseOk = true;

        //获取索引数量
        int invSize = inverted.size();
        int forcSize = forward.size();
        int textInfoSize = textInfo.size();

        //获取CPU参数
        int cores = Runtime.getRuntime().availableProcessors();

        //获取剩余内存容量
        long freeMemory = Runtime.getRuntime().freeMemory();

        //已使用的内存容量
        long hasUsedMemory = Runtime.getRuntime().maxMemory()-freeMemory;

        //获取可用内存容量
        long abledMemory = Runtime.getRuntime().maxMemory();

        try {
            stringRedisTemplate.hasKey("-");
        }catch (Throwable t){
            redisOk = false;
        }

        try{
            mongoTemplate.collectionExists("-");
        }catch (Exception e){
            databaseOk = false;
        }

        SystemInfoPacking s = new SystemInfoPacking(cores,freeMemory,abledMemory,hasUsedMemory,invSize,forcSize,textInfoSize,redisOk,databaseOk);

        return new ResultVO(ResultVO.OK,"ok",s);
    }



}
