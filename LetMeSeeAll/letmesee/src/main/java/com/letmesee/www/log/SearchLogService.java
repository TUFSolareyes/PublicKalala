package com.letmesee.www.log;

import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.util.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SearchLogService {

    public ResultVO searchLogger(String msg, LogUtil type,String savePath){
        File file = new File(savePath);
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS"));
        if(savePath==null||!file.exists()){
            FileOutputStream fos = null;
            String out = date+" [INFO] "+"Message: "+msg;
            try {
                fos = new FileOutputStream(savePath,true);
                byte[] bytes = out.getBytes();
                fos.write(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return new ResultVO(ResultVO.EXCEPTION,"no",null);
        }
        switch (type){
            case INFO:
                System.out.println(date+" [INFO] "+"Message: "+msg+" 已输出到: "+savePath);
                break;
            case WARN:
                System.out.println(date+" [WARN!!] "+"Message: "+msg+" 已输出到: "+savePath);
                break;
            case ERROR:
                System.out.println(date+" [ERROR!!!] "+"Message: "+msg+" 已输出到: "+savePath);
                break;
            default:
                break;
        }
        return new ResultVO(ResultVO.OK,"ok",null);
    }

}



