package com.letmesee.www.perAndInit;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.letmesee.LetMeSeeApplication;
import com.letmesee.www.pojo.ForwardPacking;
import com.letmesee.www.pojo.InvertedPacking;
import com.letmesee.www.pojo.TextInfoPacking;
import com.letmesee.www.util.LogUtil;
import com.letmesee.www.util.SomeUtils;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 初始化任务类
 */
@Component
@SuppressWarnings("all")
public class Init implements Runnable{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run() {

        if(!SomeUtils.judgeMongoTemplateOk(mongoTemplate)){
            LogUtil.logMessage("数据库连接失败",LogUtil.ERROR);
            return;
        }

        CountDownLatch countDownLatch = new CountDownLatch(3);

        LetMeSeeApplication.T_POOL.execute(new InvIndexInit(mongoTemplate,countDownLatch));

        LetMeSeeApplication.T_POOL.execute(new ForvIndexInit(mongoTemplate,countDownLatch));

        LetMeSeeApplication.T_POOL.execute(new TextInfoInit(mongoTemplate,countDownLatch));

        try {
            countDownLatch.await();
            if(SomeUtils.isMemoryToolittle()){
                LogUtil.logMessage("注意，剩余内存过少！请及时进行优化",LogUtil.WARN);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }


        LogUtil.logMessage("当前倒排索引链数量: "+LetMeSeeApplication.INVERTED.size(),LogUtil.INFO);
        LogUtil.logMessage("当前正排索引链数量: "+LetMeSeeApplication.FORWARD.size(),LogUtil.INFO);
    }

}

/**
 * 倒排索引初始化任务
 */
class InvIndexInit implements Runnable{

    private MongoTemplate mongoTemplate;

    private CountDownLatch countDownLatch;

    InvIndexInit(MongoTemplate mongoTemplate,CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run() {
        //载入倒排索引
        MongoCursor<Document> inv = mongoTemplate.getCollection("inv_1").find().noCursorTimeout(true).batchSize(2000).cursor();
        while(inv.hasNext()){
            Document document = inv.next();
            String jsonStr = document.toJson();
            JsonIterator parse = JsonIterator.parse(jsonStr);
            try {
                Any any = parse.readAny();
                InvertedPacking invertedPacking = any.as(InvertedPacking.class);
                LetMeSeeApplication.INVERTED.put(invertedPacking.get_id(),invertedPacking.getTids());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        countDownLatch.countDown();
    }
}


/**
 * 正排索引初始化任务
 */
class ForvIndexInit implements Runnable{

    private MongoTemplate mongoTemplate;

    private CountDownLatch countDownLatch;

    ForvIndexInit(MongoTemplate mongoTemplate,CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run() {
        //载入正排索引
        MongoCursor<Document> for_1 = mongoTemplate.getCollection("for_1").find().noCursorTimeout(true).batchSize(2000).cursor();
        while(for_1.hasNext()){
            Document document = for_1.next();
            String jsonStr = document.toJson();
            JsonIterator parse = JsonIterator.parse(jsonStr);
            try {
                Any any = parse.readAny();
                ForwardPacking forwardPacking = any.as(ForwardPacking.class);
                LetMeSeeApplication.FORWARD.put(forwardPacking.get_id(),forwardPacking.getMap());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        countDownLatch.countDown();
    }
}


/**
 * 文本摘要初始化任务
 */

class TextInfoInit implements Runnable {

    private MongoTemplate mongoTemplate;

    private CountDownLatch countDownLatch;

    TextInfoInit(MongoTemplate mongoTemplate,CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run() {
        MongoCursor<Document> fo_1 = mongoTemplate.getCollection("fo_1").find().noCursorTimeout(true).batchSize(2000).cursor();
        while(fo_1.hasNext()){
            Document document = fo_1.next();
            String jsonStr = document.toJson();
            JsonIterator parse = JsonIterator.parse(jsonStr);
            try {
                Any any = parse.readAny();
                TextInfoPacking textInfoPacking = any.as(TextInfoPacking.class);
                LetMeSeeApplication.TEXTINFO.put(textInfoPacking.get_id(),textInfoPacking);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        countDownLatch.countDown();
    }
}