package com.letmesee;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.letmesee.www.pojo.InvertedPacking;
import com.letmesee.www.pojo.PicPacking;
import com.letmesee.www.util.JiebaUtil;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

@SpringBootTest
class LetMeSeeApplicationTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void contextLoads() {
        int i = 0;
        MongoCursor<Document> run = mongoTemplate.getCollection("pic_1").find().noCursorTimeout(true).batchSize(2000).cursor();
        while(run.hasNext()){

            Document document = run.next();
            String jsonStr = document.toJson();
            JsonIterator parse = JsonIterator.parse(jsonStr);
            try {
                Any any = parse.readAny();
                PicPacking p = any.as(PicPacking.class);
                Set<String> set = JiebaUtil.jiebaAn2(p.getContent());
                System.out.println(++i);
                for(String word:set){
                    ArrayList<Long> arr = new ArrayList<>();
                    arr.add(p.get_id());
                    InvertedPacking in = new InvertedPacking(word,arr);
                    try{
                        mongoTemplate.insert(in,"picInv_1");
                    }catch (Exception e){
                        Criteria criteria = Criteria.where("_id").is(word);
                        Query query = new Query(criteria);
                        mongoTemplate.updateFirst(query,new Update().push("tids",p.get_id()),"picInv_1");
                    }
                }
            } catch (Exception e){
            }
        }

    }

}
