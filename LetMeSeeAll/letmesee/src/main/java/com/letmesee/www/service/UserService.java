package com.letmesee.www.service;

import com.letmesee.www.pojo.ResultVO;

import java.io.IOException;

public interface UserService {

    /**
     * 注册
     * @param userName
     * @param pass
     * @return
     */
    ResultVO register(String userName, String pass);

    /**
     * 登录
     * @param userName
     * @param pass
     * @return
     * @throws IOException
     */
    ResultVO login(String userName,String pass) throws IOException;

    /**
     * 注销
     * @param token
     * @return
     */
    ResultVO exitMe(String token);

    /**
     * 新建收藏夹
     * @param uid
     * @param cname
     * @return
     */
    ResultVO createStarCollection(String uid,String cname);

    /**
     * 添加新的收藏
     * @param uid
     * @param tid
     * @param cid
     * @return
     */
    ResultVO addStarToCollection(String uid, String tid, String cid);

    /**
     * 从收藏夹中删除某个收藏
     * @param uid
     * @param tid
     * @param cid
     * @return
     */
    ResultVO delStarFromCollection(String uid,String tid,String cid);


    /**
     * 删除某个集合
     * @param uid
     * @param cid
     * @return
     */
    ResultVO delCollection(String uid,String cid);

    /**
     * 获取收藏
     * @param cid
     * @param uid
     * @param pageCount
     * @param limitCount
     * @return
     */
    ResultVO getStarFromCollection(String cid,String uid,int pageCount,int limitCount);

    /**
     * 获取所有收藏夹
     * @param uid
     * @return
     */
    ResultVO getStarCollections(String uid);

    /**
     * 重命名收藏夹
     * @param uid
     * @param name
     * @param cid
     * @return
     */
    ResultVO renameCollection(String uid,String name,String cid);
}
