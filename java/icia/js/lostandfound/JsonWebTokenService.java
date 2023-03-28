package icia.js.lostandfound;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import icia.js.lostandfound.beans.JWTBean;
import icia.js.lostandfound.beans.MemberBean;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JsonWebTokenService {
	private long expiredDate = 1000L * 60 * 30;
	
	public String tokenIssuance(JWTBean tokenBody, String userKey) {
		Map<String, Object> jwtHeaders = new HashMap<>();
		jwtHeaders.put("typ", "JWT");
		jwtHeaders.put("alg", "HS256");
		
		SignatureAlgorithm sa = SignatureAlgorithm.HS256;
		
		Key secretKey = new SecretKeySpec(userKey.getBytes(),sa.getJcaName());
		return Jwts.builder()
				.setHeader(jwtHeaders)
				.setSubject("JWTForJSFramework")
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+expiredDate))
				.claim("TokenBody", tokenBody)
				.signWith(sa, secretKey)
				.compact();
	}
	public boolean tokenIsValid(Claims claims, MemberBean member) throws Exception {
		boolean result=false;
	    Map<String, Object> tokenBody = (Map<String, Object>) claims.get("TokenBody");
	    result=(tokenBody.get("mmName").equals(member.getMmName())
	            && tokenBody.get("mmEmail").equals(member.getMmEmail())
	            && tokenBody.get("mmSns").equals(member.getMmSns())
	            && tokenBody.get("mmPhone").equals(member.getMmPhone()));
	    return result;
	}
	public long getTokenRemainingTime(Claims claims) throws Exception {
	    long remainingTime = 0L;
	    try {
	        Date expirationDate = claims.getExpiration();
	        Date currentDate = new Date();
	        if (expirationDate.after(currentDate)) {
	            remainingTime = expirationDate.getTime() - currentDate.getTime();
	        }
	    } catch (ExpiredJwtException e) {
	        throw new Exception("Token has expired.");
	    }
	    return remainingTime;
	}
	public boolean tokenExpiredDateCheck(String userToken,String userKey) throws Exception{
		boolean result =false;
		try {
			Claims claims =Jwts.parser().setSigningKey(userKey.getBytes())
					.parseClaimsJws(userToken)
					.getBody();
			result= !claims.getExpiration().before(new Date());
		}catch(ExpiredJwtException e) {throw new Exception("token does not contain");}
		
		return result;
	}
	public Claims getTokenInfo(String userToken,String userKey) throws Exception{
		Claims claims = null;
		try {
			claims = Jwts.parser().setSigningKey(userKey.getBytes())
					.parseClaimsJws(userToken)
					.getBody();
		}catch(Exception e) {throw new Exception("Token does Not Exist");}
		return claims;
	}
}
