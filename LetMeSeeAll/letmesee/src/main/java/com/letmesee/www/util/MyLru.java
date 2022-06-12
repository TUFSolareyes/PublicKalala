package com.letmesee.www.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MyLru<K,E> {

    /**
     * 最大大小限制，-1为无限
     */
    private int maxSize = -1;

    /**
     * 最后更新时间
     */
    private Long lastUpdateTimestamp;

    private Map<K,E> cache = Collections.synchronizedMap(new LinkedHashMap<>());

    public MyLru() {
        this.lastUpdateTimestamp = System.currentTimeMillis();
    }

    public MyLru(int maxSize) {
        this.maxSize = maxSize;
        this.lastUpdateTimestamp = System.currentTimeMillis();
    }

    public void runEliminate(){

    }





}
