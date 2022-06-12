package com.letmesee.www.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    public static String getTokenStr(Map<String,Object> map,String keySign,Long time) throws JwtException {
        return Jwts.builder().setClaims(map).setExpiration(new Date(time)).signWith(SignatureAlgorithm.HS256,keySign).compact();
    }

    public static Object parseToken(String token,String keySign,String key) throws JwtException{
        Object o = Jwts.parser().setSigningKey(keySign).parseClaimsJws(token).getBody().get(key);
        return o;
    }
}
