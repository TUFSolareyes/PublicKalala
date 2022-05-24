package com.letmesee.www.service.impl;

import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.pojo.user.StarCollectionPacking;
import com.letmesee.www.pojo.user.User;
import com.letmesee.www.util.SomeUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl {

    private static final String keySign = "U2FsdGVkX19G9ftMBq6bKgeDFQ3yeFg4T0auyZX34mSO2inHebEMDcw6";

    @Autowired
    private StringRedisTemplate stringRedisTemplatel;

    @Autowired
    private MongoTemplate mongoTemplate;


    public ResultVO register(String userName,String pass){
        if(SomeUtils.isNullorEmpty(userName,pass)){
            return new ResultVO(4002,"no",null);
        }
        Criteria criteria = Criteria.where("userName").is(userName);
        Query query = new Query(criteria);
        User user = mongoTemplate.findOne(query, User.class, "user_1");
        if(user==null){
            synchronized (this){
                user = mongoTemplate.findOne(query, User.class, "user_1");
                if(user==null){
                    String uid = SomeUtils.getRandomStr();
                    User u = new User(uid,userName,pass);
                    mongoTemplate.insert(u,"user_1");
                }else{
                    return new ResultVO(4002,"已经存在此用户",null);
                }
            }
            return new ResultVO(4000,"ok",null);
        }else{
            return new ResultVO(4002,"已经存在此用户",null);
        }
    }



    public ResultVO login(String userName,String pass) throws IOException {

        if(SomeUtils.isNullorEmpty(userName,pass)){
            return new ResultVO(4002,"请输入参数",null);
        }

        Criteria criteria = Criteria.where("userName").is(userName);
        Query query = new Query(criteria);
        User user = mongoTemplate.findOne(query, User.class, "user_1");
        if(user==null){
            return new ResultVO(4002,"没有该账号",null);
        }
        if(pass.equals(user.getPass())){
            String keysign = SomeUtils.getUrlFromProFile("keysign");
            Map<String,Object> map = new HashMap<>(1);
            map.put("uid",user.get_id());
            String re = Jwts.builder().setClaims(map).setExpiration(new Date(System.currentTimeMillis()+1000*60*60)).signWith(SignatureAlgorithm.HS256,keysign).compact();
            String ac = Jwts.builder().setClaims(map).setExpiration(new Date(System.currentTimeMillis()+1000*10)).signWith(SignatureAlgorithm.HS256,keysign).compact();
            String[] ans = new String[2];
            ans[0] = re;
            ans[1] = ac;
            return new ResultVO(4000,"ok",ans);
        }
        return new ResultVO(4002,"密码或账号错误",null);
    }


    public ResultVO exitMe(String token){
        stringRedisTemplatel.opsForSet().remove("login_token",token);
        return new ResultVO(4000,"注销成功",null);
    }


    public ResultVO createStarCollection(String uid,String cname){
        if(SomeUtils.isNullorEmpty(cname)){
            return new ResultVO(4002,"请输入名称",null);
        }
        String cid = SomeUtils.getRandomStr();
        ArrayList<Long> tids = new ArrayList<>();
        StarCollectionPacking s = new StarCollectionPacking(cid,uid,cname,System.currentTimeMillis(),tids);
        try{
            mongoTemplate.insert(s,"userSc_1");
            return new ResultVO(4000,"ok",null);
        }catch (Exception e){
        }
        return new ResultVO(4002,"no",null);
    }


    public ResultVO addStarToCollection(String uid,Long tid,String cid){
        Criteria criteria = Criteria.where("_id").is(cid);
        Query query = new Query(criteria);
        StarCollectionPacking userSc_1 = mongoTemplate.findOne(query, StarCollectionPacking.class, "userSc_1");
        if(userSc_1==null){
            return new ResultVO(4002,"没有找到指定的收藏夹",null);
        }
        if(!uid.equals(userSc_1.getUid())){
            return new ResultVO(4002,"用户信息错误",null);
        }
        mongoTemplate.updateFirst(query,new Update().addToSet("tids",tid),"userSc_1");
        return new ResultVO(4000,"ok",null);
    }


    public ResultVO delStarFromCollection(String uid,Long tid,String cid){
        Criteria criteria = Criteria.where("_id").is(cid);
        Query query = new Query(criteria);
        StarCollectionPacking s = mongoTemplate.findOne(query,StarCollectionPacking.class,"userSc_1");
        if(s==null){

        }

        if(!uid.equals(s.getUid())){

        }

        mongoTemplate.updateFirst(query,new Update().pull("tids",tid),"userSc_1");
        return null;
    }


    public ResultVO getStarFromCollection(String cid,String uid,int pageCount,int limitCount){
        return null;
    }


    public ResultVO getStarCollections(String uid){
        Criteria criteria = Criteria.where("uid").is(uid);
        Query query = new Query(criteria);
        List<StarCollectionPacking> list = mongoTemplate.find(query, StarCollectionPacking.class,"userSc_1");
        return new ResultVO(4000,"ok",list);
    }

}
