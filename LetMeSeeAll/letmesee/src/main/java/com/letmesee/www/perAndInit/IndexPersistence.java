package com.letmesee.www.perAndInit;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.letmesee.LetMeSeeApplication;
import com.letmesee.www.pojo.ForwardPacking;
import com.letmesee.www.pojo.InvertedPacking;
import com.letmesee.www.pojo.TextInfoPacking;
import com.letmesee.www.pojo.WordPacking;
import com.letmesee.www.util.LogUtil;
import com.letmesee.www.util.SomeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * 持久化计划类
 */
@Component
@SuppressWarnings("all")
class IndexPersistence {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Scheduled(cron = "*/30 * * * * ?")
    public void run() {

        if(!SomeUtils.judgeStringRedisTemplateOk(stringRedisTemplate)||!SomeUtils.judgeMongoTemplateOk(mongoTemplate)){
            LogUtil.logMessage("redis或者数据库连接失败",LogUtil.ERROR);
            return;
        }

        CountDownLatch countDownLatch = new CountDownLatch(3);


        LogUtil.logMessage("开始索引持久化...",LogUtil.INFO);

        LetMeSeeApplication.T_POOL.execute(new InvIndexPersistence(stringRedisTemplate,mongoTemplate,countDownLatch));

        LetMeSeeApplication.T_POOL.execute(new ForvIndexPersistence(stringRedisTemplate,mongoTemplate,countDownLatch));

        LetMeSeeApplication.T_POOL.execute(new TextInfoPersistence(stringRedisTemplate,mongoTemplate,countDownLatch));

        try {
            countDownLatch.await();
            LogUtil.logMessage("索引持久化完成...",LogUtil.INFO);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}


class InvIndexPersistence implements Runnable {

    private StringRedisTemplate stringRedisTemplate;

    private MongoTemplate mongoTemplate;

    private CountDownLatch countDownLatch;

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    InvIndexPersistence(StringRedisTemplate stringRedisTemplate, MongoTemplate mongoTemplate, CountDownLatch countDownLatch){
        this.stringRedisTemplate = stringRedisTemplate;
        this.mongoTemplate = mongoTemplate;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        LogUtil.logMessage("开始倒排索引持久化...",LogUtil.INFO);
        //从redis中取出前缀为w:的key
        Set<String> set = stringRedisTemplate.keys("w:".concat("*"));

        //遍历key
        for(String word:set){
            if(word==null||word.length()==0){
                continue;
            }

            //取出key对应的list中当前元素的个数
            long count = stringRedisTemplate.opsForList().size(word);
            if(count==0){
                continue;
            }

            //用于存储弹出的元素
            List<String> list = new ArrayList<>();
            //弹出list中的元素
            for(int i=0;i<count;i++){
                //弹出list中的元素（textId）
                String idStr = stringRedisTemplate.opsForList().rightPop(word);
                list.add(idStr);
            }

            //将信息封装成实体类
            String keyword = word.substring(word.indexOf(":")+1);
            InvertedPacking inv = new InvertedPacking(keyword,list);
            try{
                mongoTemplate.insert(inv,"inv_1");
            }catch (Exception e){
                Criteria criteria = Criteria.where("_id").is(keyword);
                Query query = new Query(criteria);
                for(int i=0;i<list.size();i++){
                    mongoTemplate.updateFirst(query,new Update().push("tids",list.get(i)),"inv_1");
                }
            }
        }
        countDownLatch.countDown();
    }
}


class ForvIndexPersistence implements Runnable {

    private StringRedisTemplate stringRedisTemplate;

    private MongoTemplate mongoTemplate;

    private CountDownLatch countDownLatch;

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    ForvIndexPersistence(StringRedisTemplate stringRedisTemplate,MongoTemplate mongoTemplate,CountDownLatch countDownLatch){
        this.stringRedisTemplate = stringRedisTemplate;
        this.mongoTemplate = mongoTemplate;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        LogUtil.logMessage("开始正排索引持久化...",LogUtil.INFO);
        //取出前缀为tid的key
        Set<String> set = stringRedisTemplate.keys("tid:".concat("*"));

        //遍历key
        for(String idStr:set){
            JsonIterator parse = JsonIterator.parse(stringRedisTemplate.opsForValue().get(idStr));
            try {
                Any any = parse.readAny();
                Map<String, WordPacking> as = any.as(Map.class);
                String textId = idStr.substring(idStr.indexOf(":")+1);
                ForwardPacking f = new ForwardPacking(textId,as);
                mongoTemplate.insert(f,"for_1");
            } catch (IOException e) {
            } catch (Exception e){}
            stringRedisTemplate.delete(idStr);
        }
        countDownLatch.countDown();
    }
}


class TextInfoPersistence implements Runnable {

    private StringRedisTemplate stringRedisTemplate;

    private MongoTemplate mongoTemplate;

    private CountDownLatch countDownLatch;

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    TextInfoPersistence(StringRedisTemplate stringRedisTemplate,MongoTemplate mongoTemplate,CountDownLatch countDownLatch){
        this.stringRedisTemplate = stringRedisTemplate;
        this.mongoTemplate = mongoTemplate;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        LogUtil.logMessage("开始文本持久化...",LogUtil.INFO);
        Set<String> set = stringRedisTemplate.keys("fo:".concat("*"));

        //遍历key
        for(String idStr:set){
            String jsonStr = stringRedisTemplate.opsForValue().get(idStr);
            String textId = idStr.substring(idStr.indexOf(":")+1);
            JsonIterator parse = JsonIterator.parse(jsonStr);
            try {
                Any any = parse.readAny();
                TextInfoPacking textInfoPacking = any.as(TextInfoPacking.class);
                textInfoPacking.set_id(textId);
                mongoTemplate.insert(textInfoPacking,"fo_1");
            } catch (IOException e) {
            } catch (Exception e){}
            stringRedisTemplate.delete(idStr);
        }
        countDownLatch.countDown();
    }
}