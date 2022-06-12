package com.letmesee.www.Interceptor;

import com.letmesee.www.util.LogUtil;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CurrentLimiting implements Runnable{

    AtomicInteger ts = new AtomicInteger(0);

    int limit = 1000;

    public void setLimit(int limit){
        this.limit = limit;
    }

    public CurrentLimiting(){}

    public void addToken(){
        if(ts.intValue()<limit){
            ts.incrementAndGet();
            LogUtil.logMessage("添加了令牌:"+ts.intValue(),LogUtil.INFO);
        }
    }

    public boolean getToken(){
        if(ts.intValue()>0){
            synchronized (this){
                if(ts.intValue()>0){
                    ts.decrementAndGet();
                    return true;
                }else{
                    return false;
                }
            }
        }else{
            return false;
        }
    }


    @Override
    public void run() {
        addToken();
    }


    public void limitStart(Runnable runnable){
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
        executorService.scheduleAtFixedRate(runnable,0,10, TimeUnit.MILLISECONDS);
    }
}
