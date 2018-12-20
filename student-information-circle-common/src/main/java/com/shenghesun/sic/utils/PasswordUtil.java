package com.shenghesun.sic.utils;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;

public class PasswordUtil {

	public static String encrypt(String password, String salt) {
//		return encrypt(password, salt, ShiroConfig.HASH_ALGORITHM_NAME, ShiroConfig.HASH_ITERATIONS);
		return encrypt(password, salt, 10);
	}

	public static String encrypt(String password, String salt, int iterations) {
		// 加密操作
		ByteSource source = ByteSource.Util.bytes(salt);
		// 加密方式，明文密码，盐值byte，加密次数
//		Object result = new SimpleHash(ShiroConfig.HASH_ALGORITHM_NAME, password, source, iterations);
		Sha256Hash result = new Sha256Hash(password, source, iterations);
		return result.toString();
	}
	
	public static void main(String[] args) {
		String p = "Sic@2018";
		String salt = RandomUtil.randomString(16);
		System.out.println(salt);
		System.out.println(encrypt(p, salt));
	}

}
