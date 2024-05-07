package com.example.shoppapp.Components;

import com.example.shoppapp.Models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;

@Component
public class JwTokenUtils {
    @Value("${jwt.expiration}")
    private Long expiration; //lưu vào một biến môi trường
    @Value("${jwt.secretKey}")
    private String secretKey;
    public String generationToken(User user) throws Exception{
        //properties=>claims
        Map<String,Object> claims = new HashMap<>();
        //this.gennerateSecretKey();
        claims.put("phoneNumber",user.getPhoneNumber());
        claims.put("userId", user.getId());
        try {
            String token= Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getPhoneNumber())
                    .setExpiration(new Date(System.currentTimeMillis()+expiration*1000L))
                    .signWith(getSignInkey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        }catch (Exception e){
            throw new InvalidParameterException("can not create jwt"+e.getMessage());
          //  return null;
        }
    }

    private Key getSignInkey(){
        byte[] bytes = Decoders.BASE64.decode(secretKey); //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=
        return Keys.hmacShaKeyFor(bytes);

    }
    private String gennerateSecretKey(){
        SecureRandom random= new SecureRandom();
        byte[] keyBytes= new byte[32];
        String secretKey= Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInkey())
                .build()
                //Phân tích token JWT đã cho và trả về một Jws<Claims> đối tượng.
                // Jws<Claims> đối tượng này chứa tất cả các yêu cầu (claims) từ token.
                .parseClaimsJws(token)
                //Lấy phần body từ Jws<Claims> đối tượng, đây là nơi chứa tất cả các yêu cầu (claims) của token.
                .getBody();
    }

   // Một phương thức chung trích xuất một claim cụ thể từ JWT token.

    //trích xuaats tất cả claim trong token
    public  <T> T extractClain(String token, Function<Claims,T> claimsResolver){
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //check expiration :kiểm tra token hết hạn
    public boolean isTokenExpired(String token){
        Date expirationDate=this.extractClain(token,Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    //lấy ra token phone
    public String extractPhoneNumber(String token){
        return extractClain(token, Claims::getSubject);
    }

    //kiểm tra user name với token còn hạn không,với user truyền vào có tùnghayfy không
    public boolean validateToken(String token , UserDetails userDetails){
        String phoneNumber=extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername()))&& !isTokenExpired(token);
    }
}
