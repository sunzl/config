package com.nami.encryp;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigestUtil {
	private static Logger logger = LoggerFactory.getLogger(DigestUtil.class);
	public static final String CHARSET = "UTF-8";
	private static final String SEED = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-=+_";
	/**
	 * 获取摘要
	 * @param origin
	 * @param charsetname
	 * @return
	 */
	public static String md5Hex(String origin) {
	    String resultString = null;
	    
	    try {
	        if (origin==null || "".equals(origin.trim())) {
				return null;
			}
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        resultString = byteToHexStr(md.digest(origin.getBytes(CHARSET)));
	    } catch (Exception e) {
	    	logger.error("DigestUtils.md5Hex error ",e);
	    }
	    return resultString;
	}
	
	/**
	 * 字节数组转换成十六进制字符串
	 * @param data
	 * @return
	 */
	public static String byteToHexStr(byte[] data) {
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<data.length;i++) {
			String shaHex = Integer.toHexString(data[i] & 0xFF);
            if (shaHex.length() <2) {
            	builder.append(0);
            }
            builder.append(shaHex);
		}
		return builder.toString();
	}
	
	/**
	 * 校验签名
	 * @param checkData
	 * @param sign
	 * @return
	 */
	public boolean checkSign(String checkData,String sign) {
		if (checkData==null || sign==null || "".equals(sign.trim())){
			return false;
		}
		String newSign = md5Hex(checkData);
		if (newSign.equals(sign)) {
			return true;
		}
		return false;
	}
	
	/**
	 * base64编码字节成字符串
	 * @param src
	 * @return
	 */
	public static String base64Encoder(byte[] src) {
		return Base64.getEncoder().encodeToString(src);
	}
	
	/**
	 * base64解码字符串成字节
	 * @param src
	 * @return
	 */
	public static byte[] base64Decoder(String src) {
		return Base64.getDecoder().decode(src);
	}
	
	/**
	 * 随机获取指定长度的值
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length){  
        Random random = new Random();  
        StringBuilder sb = new StringBuilder();  
        for(int i = 0 ; i < length; ++i){  
            int number = random.nextInt(SEED.length()); 
            sb.append(SEED.charAt(number));  
        }  
        return sb.toString();  
    }  
	
}
