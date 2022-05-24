package com.letmesee.www.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MyMap<K,E> {

    private int maxSize = -1;

    private Long lastUpdateTimestamp;

    private Map<K,E> cache = new ConcurrentHashMap<>();

    private Map<K,E> lru = Collections.synchronizedMap(new LinkedHashMap<>());

    public MyMap() {
        this.lastUpdateTimestamp = System.currentTimeMillis();
    }

    public MyMap(int maxSize) {
        this.maxSize = maxSize;
        this.lastUpdateTimestamp = System.currentTimeMillis();
    }

    public E get(K key){
        if(key==null){
            return null;
        }
        E e = lru.remove(key);
        lru.put(key,e);
        return cache.get(key);
    }

    public boolean put(K key,E e){
        if(key==null){
            return false;
        }
        if(cache.size()<maxSize||maxSize==-1){
            synchronized (this){
                if(cache.size()<maxSize||maxSize==-1){
                    lru.put(key,e);
                    cache.put(key, e);
                    this.lastUpdateTimestamp = System.currentTimeMillis();
                }
            }
        }
        return true;
    }

    public E remove(K key){
        lru.remove(key);
        return cache.remove(key);
    }


    public int getCurrentSize(){
        return cache.size();
    }


    public List<E> getSomeTopElement(int count){
        Set<K> set = lru.keySet();
        List<E> ans = new ArrayList<>(Math.min(count, set.size()));
        Iterator<K> iterator = set.iterator();
        while(iterator.hasNext()&&count>0){
            K key = iterator.next();
            E e = lru.get(key);
            ans.add(e);
            count--;
        }
        return ans;
    }

}
