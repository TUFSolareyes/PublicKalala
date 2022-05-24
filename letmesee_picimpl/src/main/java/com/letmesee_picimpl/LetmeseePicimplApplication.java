package com.letmesee_picimpl;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.letmesee_picimpl.www.SpringUtils;
import com.letmesee_picimpl.www.pojo.ForPacking;
import com.letmesee_picimpl.www.pojo.InvPacking;
import com.letmesee_picimpl.www.pojo.PicPacking;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class LetmeseePicimplApplication {

    public static final Map<String, List<Long>> INVCACHE = new ConcurrentHashMap<>();

    public static final Map<Long, PicPacking> FORCACHE = new ConcurrentHashMap<>();

    public static ThreadPoolExecutor tPool = new ThreadPoolExecutor(3,10,60, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(20), new ThreadPoolExecutor.DiscardOldestPolicy());


    public static void main(String[] args) {
        SpringApplication.run(LetmeseePicimplApplication.class, args);
        System.out.println("开始初始化...");
        Thread t = new Thread((Runnable) SpringUtils.getBean("init"));
        t.start();
    }


}


@Component
class Init implements Runnable{

    @Autowired
    private MongoTemplate mongoTemplate;

    private Map<String,List<Long>> INV = LetmeseePicimplApplication.INVCACHE;

    private Map<Long,PicPacking> FOR = LetmeseePicimplApplication.FORCACHE;

    @Override
    public void run() {

        System.out.println("开始加载索引信息...");
        CountDownLatch countDownLatch = new CountDownLatch(2);

        //初始化倒排索引
        LetmeseePicimplApplication.tPool.execute(new Runnable() {
            @Override
            public void run() {
                MongoCursor<Document> run = mongoTemplate.getCollection("picInv_1").find().noCursorTimeout(true).batchSize(2000).cursor();
                while(run.hasNext()){
                    Document document = run.next();
                    String jsonStr = document.toJson();
                    JsonIterator parse = JsonIterator.parse(jsonStr);
                    try {
                        Any any = parse.readAny();
                        InvPacking as = any.as(InvPacking.class);
                        INV.put(as.get_id(),as.getTids());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (Exception e){

                    }
                }
                countDownLatch.countDown();
            }
        });


        //初始化正排索引
        LetmeseePicimplApplication.tPool.execute(new Runnable() {
            @Override
            public void run() {
                MongoCursor<Document> run = mongoTemplate.getCollection("pic_1").find().noCursorTimeout(true).batchSize(2000).cursor();
                while(run.hasNext()){
                    Document document = run.next();
                    String jsonStr = document.toJson();
                    JsonIterator parse = JsonIterator.parse(jsonStr);
                    try {
                        Any any = parse.readAny();
                        ForPacking as = any.as(ForPacking.class);
                        PicPacking p = new PicPacking(as.get_id(),as.getUrl(),as.getContent());
                        FOR.put(as.get_id(),p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (Exception e){

                    }
                }
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
            System.out.println("索引加载完成...");
            System.out.println("当前倒排索引链数量:"+INV.size());
            System.out.println("当前正排索引数量:"+FOR.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
