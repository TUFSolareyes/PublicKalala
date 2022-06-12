package com.letmesee.www.Interceptor;

import com.letmesee.www.pojo.ResultVO;
import com.letmesee.www.util.Jackson;
import com.letmesee.www.util.JwtUtil;
import com.letmesee.www.util.SomeUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


/**
 * 双token验证拦截器
 */
@Component
public class Login implements HandlerInterceptor {

    public static final ThreadLocal<String> tl = new ThreadLocal<>();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //双token实现

        String method = request.getMethod();
        if("option".equalsIgnoreCase(method)){
            return true;
        }

        //用于刷新token的token
        String re = request.getHeader("re");
        //通行证token
        String ac = request.getHeader("ac");

        Jackson jackson = new Jackson();
        String keysign = SomeUtils.getValueFromProFile("keysign","some/key.properties");
        //查看前端是否传来了用于刷新的token
        if(re!=null){
            //如果有并且没过期就直接刷新当前token（两个token一起刷新）
            try{
                String uid = (String) Jwts.parser().setSigningKey(keysign).parseClaimsJws(re).getBody().get("uid");
                if(!stringRedisTemplate.opsForSet().isMember("hasLoginUsers",uid)){
                    return false;
                }
                Map<String,Object> uidMap = new HashMap<>(1);
                uidMap.put("uid",uid);
                String newRe = JwtUtil.getTokenStr(uidMap,keysign,System.currentTimeMillis()+1000*60);
                String newAc = JwtUtil.getTokenStr(uidMap,keysign,System.currentTimeMillis()+1000*20);
                response.setHeader("re",newRe);
                response.setHeader("ac",newAc);
                tl.set(uid);
                return true;
            }catch (ExpiredJwtException e){
                ResultVO rv = new ResultVO(ResultVO.NOLOGIN,"已经过期，需要重新登录",null);
                doResponse(response,jackson.getJsonStr(rv));
                return false;
            }catch (JwtException e){
                ResultVO rv = new ResultVO(ResultVO.EXCEPTION,"token解析出现错误",null);
                doResponse(response,jackson.getJsonStr(rv));
                return false;
            }
        }

        //如果没有用于刷新的token，判断通行证token是否过期
        if(ac!=null){
            try{
                String uid = (String) JwtUtil.parseToken(ac,keysign,"uid");
                if(!stringRedisTemplate.opsForSet().isMember("hasLoginUsers",uid)){
                    return false;
                }
                tl.set(uid);
                return true;
            }catch (ExpiredJwtException e){
                ResultVO rv = new ResultVO(ResultVO.TOKENEXP,"通行证token过期",null);
                doResponse(response,jackson.getJsonStr(rv));
                return false;
            }catch (JwtException e){
                ResultVO rv = new ResultVO(ResultVO.EXCEPTION,"token解析出现错误",null);
                doResponse(response,jackson.getJsonStr(rv));
                return false;
            }
        }

        ResultVO rv = new ResultVO(ResultVO.EXCEPTION,"缺少通行证",null);
        doResponse(response,jackson.getJsonStr(rv));
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        tl.remove();
    }


    private void doResponse(HttpServletResponse response,String jsonStr){
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter pw = null;

        try {
            pw = response.getWriter();
            pw.print(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(pw!=null){
                pw.close();
            }
        }


    }



}
