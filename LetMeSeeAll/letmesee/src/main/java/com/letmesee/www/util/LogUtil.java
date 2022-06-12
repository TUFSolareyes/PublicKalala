package com.letmesee.www.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("all")
public enum LogUtil {

    INFO,WARN,ERROR;

    public static void logMessage(String msg,LogUtil type){
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS"));
        switch (type){
            case INFO:
                System.out.println(date+" [INFO] "+"Message: "+msg);
                break;
            case WARN:
                System.out.println(date+" [WARN!!] "+"Message: "+msg);
                break;
            case ERROR:
                System.out.println(date+" [ERROR!!!] "+"Message: "+msg);
                break;
        }
    }


    public static void logMessage(String msg,LogUtil type,String savePath){
        File file = new File(savePath);
        if(savePath==null||!file.exists()){
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS"));
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
        }
    }

}

