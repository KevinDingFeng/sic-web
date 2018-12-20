package com.shenghesun.sic.utils;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shenghesun.sic.config.CustomConfig;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

public class JWTUtil {

	public static final String ACCOUNT_KEY = "account";
	public static final String ID_KEY = "id";
	public static final String ERR_MSG = "err_msg";	

	/**
	 * 生成 token 的方法，可以自定义放入 token 的信息
	 * 
	 * @param userId       用户id
	 * @param userAccount  用户登录名
	 * @param generateTime 用户登录时间戳
	 * @return
	 */
	public static String create(String userId, String userAccount, Long generateTime) {
		Map<String, Object> map = new HashMap<>();
		// 可以把任何安全的数据放到map里面
		map.put(ACCOUNT_KEY, userAccount);
		map.put(ID_KEY, userId);
		String jwt = Jwts.builder().setClaims(map)
				.setExpiration(new Date(generateTime + CustomConfig.EXPIRATION_TIME_MILLISECOND))
				.signWith(SignatureAlgorithm.HS512, CustomConfig.SECRET).compact();
		return jwt;
	}

	/**
	 * @param token
	 * @return
	 */
	public static Map<String, Object> decode(String token) {
		Map<String, Object> resp = new HashMap<String, Object>();
//		if (token != null) {
		if (StringUtils.isNotBlank(token)) {
			// 解析token
			try {
				Map<String, Object> body = Jwts.parser().setSigningKey(CustomConfig.SECRET).parseClaimsJws(token)
						.getBody();
				String userAccount = (String) (body.get(ACCOUNT_KEY));
//				printMap(body);
				if (userAccount == null || userAccount.isEmpty()) {
					resp.put(ERR_MSG, "用户名为空");
					return resp;
				}
				resp.put(ACCOUNT_KEY, userAccount);
				resp.put(ID_KEY, body.get(ID_KEY));
				return resp;
			} catch (SignatureException | MalformedJwtException e) {
				//  handle exception
				// don't trust the JWT!
				// jwt 解析错误
				e.printStackTrace();
				resp.put(ERR_MSG, "token 解析错误，该请求不适合使用 jwt 解析");
				return resp;
			} catch (ExpiredJwtException e) {
				//  handle exception
				// jwt 已经过期，在设置jwt的时候如果设置了过期时间，这里会自动判断jwt是否已经过期，如果过期则会抛出这个异常，我们可以抓住这个异常并作相关处理。
				e.printStackTrace();
				resp.put(ERR_MSG, "token 已过期");
				return resp;
			} catch (Exception e) {
				e.printStackTrace();
				resp.put(ERR_MSG, "token 异常");
				return resp;
			}
		} else {
			resp.put(ERR_MSG, "token 是空");
			return resp;
		}
	}

	/*private static void printMap(Map<String, Object> body) {
		Iterator<String> it = body.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			System.out.println("key = " + key + "; value = " + body.get(key));
		}
	}*/
}
