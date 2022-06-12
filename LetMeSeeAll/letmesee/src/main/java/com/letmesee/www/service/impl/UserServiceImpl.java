package com.letmesee.www.service.impl;

import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.pojo.TextInfoPacking;
import com.letmesee.www.pojo.user.StarCollectionPacking;
import com.letmesee.www.pojo.user.StarPacking;
import com.letmesee.www.pojo.user.User;
import com.letmesee.www.service.UserService;
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
public class UserServiceImpl implements UserService {

    private static final String keySign = "U2FsdGVkX19G9ftMBq6bKgeDFQ3yeFg4T0auyZX34mSO2inHebEMDcw6";

    @Autowired
    private StringRedisTemplate stringRedisTemplatel;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public ResultVO register(String userName, String pass){
        if(SomeUtils.isNullorEmpty(userName,pass)){
            return new ResultVO(ResultVO.EXCEPTION,"no",null);
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
                    return new ResultVO(ResultVO.EXCEPTION,"已经存在此用户",null);
                }
            }
            return new ResultVO(ResultVO.OK,"ok",null);
        }else{
            return new ResultVO(ResultVO.EXCEPTION,"已经存在此用户",null);
        }
    }



    @Override
    public ResultVO login(String userName, String pass) throws IOException {

        if(SomeUtils.isNullorEmpty(userName,pass)){
            return new ResultVO(ResultVO.EXCEPTION,"请输入参数",null);
        }

        Criteria criteria = Criteria.where("userName").is(userName);
        Query query = new Query(criteria);
        User user = mongoTemplate.findOne(query, User.class, "user_1");
        if(SomeUtils.isNullorEmpty(user)){
            return new ResultVO(ResultVO.EXCEPTION,"没有该账号",null);
        }
        if(pass.equals(user.getPass())){
            String keysign = SomeUtils.getValueFromProFile("keysign","some/key.properties");
            Map<String,Object> map = new HashMap<>(1);
            map.put("uid",user.get_id());
            String re = Jwts.builder().setClaims(map).setExpiration(new Date(System.currentTimeMillis()+1000*60*60)).signWith(SignatureAlgorithm.HS256,keysign).compact();
            String ac = Jwts.builder().setClaims(map).setExpiration(new Date(System.currentTimeMillis()+1000*10)).signWith(SignatureAlgorithm.HS256,keysign).compact();
            String[] ans = new String[2];
            ans[0] = re;
            ans[1] = ac;
            stringRedisTemplatel.opsForSet().add("hasLoginUsers",user.get_id());
            return new ResultVO(ResultVO.EXCEPTION,"ok",ans);
        }
        return new ResultVO(ResultVO.EXCEPTION,"密码或账号错误",null);
    }


    @Override
    public ResultVO exitMe(String uid){
        stringRedisTemplatel.opsForSet().remove("hasLoginUsers",uid);
        return new ResultVO(ResultVO.OK,"注销成功",null);
    }


    @Override
    public ResultVO createStarCollection(String uid, String cname){
        if(SomeUtils.isNullorEmpty(cname)){
            return new ResultVO(ResultVO.EXCEPTION,"请输入名称",null);
        }
        String cid = SomeUtils.getRandomStr();
        StarCollectionPacking s = new StarCollectionPacking(cid,uid,cname,System.currentTimeMillis());
        try{
            mongoTemplate.insert(s,"userSc_1");
            return new ResultVO(ResultVO.OK,"ok",null);
        }catch (Exception e){
        }
        return new ResultVO(ResultVO.EXCEPTION,"no",null);

    }


    @Override
    public ResultVO addStarToCollection(String uid, String tid, String cid){
        Criteria criteria = Criteria.where("_id").is(cid);
        Query query = new Query(criteria);
        StarCollectionPacking collection = mongoTemplate.findOne(query, StarCollectionPacking.class, "userSc_1");
        if(collection==null){
            return new ResultVO(ResultVO.EXCEPTION,"没有找到指定的收藏夹",null);
        }
        if(!uid.equals(collection.getUid())){
            return new ResultVO(ResultVO.EXCEPTION,"用户信息错误",null);
        }
        ArrayList<String> arr = new ArrayList<>();
        arr.add(tid);
        StarPacking starPacking = new StarPacking(cid,arr);
        try{
            mongoTemplate.insert(starPacking,"sc_1");
        }catch (Exception e){
            mongoTemplate.updateFirst(query,new Update().addToSet("tids",tid),"sc_1");
        }
        return new ResultVO(ResultVO.OK,"ok",null);
    }


    @Override
    public ResultVO delStarFromCollection(String uid, String tid, String cid){
        Criteria criteria = Criteria.where("_id").is(cid);
        Query query = new Query(criteria);
        StarCollectionPacking s = mongoTemplate.findOne(query,StarCollectionPacking.class,"userSc_1");
        if(SomeUtils.isNullorEmpty(s)){
            return new ResultVO(ResultVO.EXCEPTION,"no",null);
        }
        if(SomeUtils.isNullorEmpty(uid)&&!uid.equals(s.getUid())){
            return new ResultVO(ResultVO.EXCEPTION,"no",null);
        }
        mongoTemplate.updateFirst(query,new Update().pull("tids",tid),"sc_1");
        return new ResultVO(ResultVO.OK,"ok",null);
    }

    @Override
    public ResultVO delCollection(String uid, String cid) {
        if(SomeUtils.isNullorEmpty(uid,cid)){
            return new ResultVO(ResultVO.EXCEPTION,"no",null);
        }
        Criteria criteria = Criteria.where("_id").is(cid);
        Query query = new Query(criteria);
        StarCollectionPacking s = mongoTemplate.findOne(query, StarCollectionPacking.class, "userSc_1");
        if(SomeUtils.isNullorEmpty(s)||!uid.equals(s.getUid())){
            return new ResultVO(ResultVO.EXCEPTION,"no",null);
        }
        mongoTemplate.findAndRemove(query,StarCollectionPacking.class,"userSc_1");
        mongoTemplate.findAndRemove(query,StarPacking.class,"sc_1");
        return new ResultVO(ResultVO.OK,"ok",null);
    }


    @Override
    public ResultVO getStarFromCollection(String cid, String uid, int pageCount, int limitCount){
        Criteria criteria = Criteria.where("_id").is(cid);
        Query query = new Query(criteria);
        StarCollectionPacking sc = mongoTemplate.findOne(query, StarCollectionPacking.class, "userSc_1");
        if(sc==null){
            return new ResultVO(ResultVO.EXCEPTION,"no",null);
        }
        if(SomeUtils.isNullorEmpty(sc.getUid())||!sc.getUid().equals(uid)){
            return new ResultVO(ResultVO.EXCEPTION,"no",null);
        }
        StarPacking one = mongoTemplate.findOne(query, StarPacking.class, "sc_1");
        if(SomeUtils.isNullorEmpty(one)){
            return new ResultVO(ResultVO.EXCEPTION,"no",null);
        }

        int start = (pageCount-1)*limitCount;
        List<TextInfoPacking> arr = new ArrayList<>();
        for(int i=start;one.getTids()!=null&&i<start+limitCount-1&&i<one.getTids().size();i++){
            Criteria criteria2 = Criteria.where("_id").is(one.getTids().get(i));
            Query query2 = new Query(criteria2);
            TextInfoPacking infoPacking = mongoTemplate.findOne(query2, TextInfoPacking.class, "fo_1");
            arr.add(infoPacking);
        }

        int maxCount = one.getTids().size();
        int maxPageCount = maxCount%limitCount==0? maxCount/limitCount:maxCount/limitCount+1;
        return new ResultVO(ResultVO.OK,maxCount+","+maxPageCount,arr);
    }


    @Override
    public ResultVO getStarCollections(String uid){
        Criteria criteria = Criteria.where("uid").is(uid);
        Query query = new Query(criteria);
        List<StarCollectionPacking> list = mongoTemplate.find(query, StarCollectionPacking.class,"userSc_1");
        return new ResultVO(ResultVO.OK,"ok",list);
    }


    @Override
    public ResultVO renameCollection(String uid, String name, String cid){
        if(SomeUtils.isNullorEmpty(uid,name,cid)){
            return new ResultVO(ResultVO.EXCEPTION,"no",null);
        }
        Criteria criteria = Criteria.where("_id").is(cid);
        Query query = new Query(criteria);
        StarCollectionPacking s = mongoTemplate.findOne(query, StarCollectionPacking.class, "userSc_1");
        if(SomeUtils.isNullorEmpty(s)||!uid.equals(s.getUid())){
            return new ResultVO(ResultVO.EXCEPTION,"no",null);
        }
        mongoTemplate.updateFirst(query,new Update().set("cname",name),"userSc_1");
        return new ResultVO(ResultVO.OK,"ok",null);
    }
}
