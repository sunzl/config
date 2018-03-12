package com.nami.encryp;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * AES对称加密
 * 初始向量：必须16字节的任意值
 * 加密密钥：必须16字节的任意值
 */
public class Aes {

	private static Logger logger = LoggerFactory.getLogger(Aes.class);
	private static final String ALGORITHM_KEY = "AES";
	private static final String ALGORITHM_INSTANCE = "AES/CBC/PKCS5Padding";//加密算法/工作模式/填充方式
	private static final String CHARSET = "UTF-8";
	/**
	 * 加密
	 * @param iv		
	 * 		初始向量
	 * @param keybyte
	 * 		密钥字节
	 * @param src
	 * 		需要加密的数据
	 * @return
	 */
	public static byte[] encryptMode(String iv, byte[] keybyte, byte[] src) {
	    try {
	        Cipher cipher = Cipher.getInstance(ALGORITHM_INSTANCE);
	        int blockSize = cipher.getBlockSize();
	        int plaintextLength = src.length;
	        if (plaintextLength % blockSize != 0) {
	            // 待加密内容的字节长度必须是16的倍数，如果不是16的倍数，就会出如下异常： 
	            plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
	        }
	        byte[] plaintext = new byte[plaintextLength];
	        System.arraycopy(src, 0, plaintext, 0, src.length);
	        SecretKeySpec keyspec = new SecretKeySpec(keybyte, ALGORITHM_KEY);
	        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes(CHARSET));
	        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
	        byte[] encrypted = cipher.doFinal(plaintext);
	        return encrypted;

	    } catch (java.security.NoSuchAlgorithmException e1) {
	    	logger.error("Aes.encryptMode e1",e1);
	    } catch (javax.crypto.NoSuchPaddingException e2) {
	    	logger.error("Aes.encryptMode e2",e2);
	    } catch (Throwable e3) {
	        logger.error("Aes.encryptMode e3",e3);
	    }
	    return null;
	}
	
	/**
	 * 加密
	 * @param iv		
	 * 		初始向量
	 * @param keybyte
	 * 		密钥字节
	 * @param src
	 * 		需要加密的数据
	 * @return
	 */
	public static byte[] encryptToByte(String iv, byte[] keybyte, byte[] src) {
	    try {

	        Cipher cipher = Cipher.getInstance(ALGORITHM_INSTANCE);
	        SecretKeySpec keyspec = new SecretKeySpec(keybyte, ALGORITHM_KEY);
	        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes(CHARSET));
	        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
	        byte[] encrypted = cipher.doFinal(src);
	        return encrypted;

	    } catch (java.security.NoSuchAlgorithmException e1) {
	    	logger.error("Aes.encryptToByte e1",e1);
	    } catch (javax.crypto.NoSuchPaddingException e2) {
	    	logger.error("Aes.encryptToByte e2",e2);
	    } catch (Throwable e3) {
	        logger.error("Aes.encryptToByte e3",e3);
	    }
	    return null;
	}
	
	/**
	 * 加密
	 * @param iv		
	 * 		初始向量
	 * @param keybyte
	 * 		密钥字节
	 * @param src
	 * 		需要加密的数据
	 * @return
	 */
	public static String encryptToStr(String iv, byte[] keybyte, byte[] src) {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(ALGORITHM_INSTANCE);
			SecretKeySpec keyspec = new SecretKeySpec(keybyte, ALGORITHM_KEY);
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes(CHARSET));
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(src);
			return DigestUtil.base64Encoder(encrypted);
		} catch (NoSuchAlgorithmException e1) {
			logger.error("Aes.encryptToStr e1",e1);
		} catch (NoSuchPaddingException e2) {
			logger.error("Aes.encryptToStr e2",e2);
		} catch (InvalidKeyException e3) {
			logger.error("Aes.encryptToStr e3",e3);
		} catch (InvalidAlgorithmParameterException e4) {
			logger.error("Aes.encryptToStr e4",e4);
		} catch (IllegalBlockSizeException e5) {
			logger.error("Aes.encryptToStr e5",e5);
		} catch (BadPaddingException e6) {
			logger.error("Aes.encryptToStr e6",e6);
		} catch (UnsupportedEncodingException e7) {
			logger.error("Aes.encryptToStr e7",e7);
		}
		return null;
	}

	/**
	 * 解密
	 * @param iv		
	 * 		初始向量
	 * @param keybyte
	 * 		密钥字节
	 * @param src
	 * 		需要解密的数据
	 * @return
	 */
	public static byte[] decryptToByte(String iv, byte[] keybyte, byte[] src) {
	    try {
	        Cipher cipher = Cipher.getInstance(ALGORITHM_INSTANCE);
	        SecretKeySpec keyspec = new SecretKeySpec(keybyte, ALGORITHM_KEY);
	        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes(CHARSET));
	        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
	        byte[] original = cipher.doFinal(src);
	        return original;
	    } catch (java.security.NoSuchAlgorithmException e1) {
	        logger.error("Aes.decryptToByte e1",e1);
	    } catch (javax.crypto.NoSuchPaddingException e2) {
	        logger.error("Aes.decryptToByte e2",e2);
	    } catch (java.lang.Exception e3) {
	        logger.error("Aes.decryptToByte e3",e3);
	    }
	    return null;
	}
	
	/**
	 * 解密
	 * @param iv		
	 * 		初始向量
	 * @param keybyte
	 * 		密钥字节
	 * @param src
	 * 		需要解密的数据
	 * @return
	 */
	public static String decryptToStr(String iv, byte[] keybyte, byte[] src) {
	    try {
	        Cipher cipher = Cipher.getInstance(ALGORITHM_INSTANCE);
	        SecretKeySpec keyspec = new SecretKeySpec(keybyte, ALGORITHM_KEY);
	        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
	        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
	        byte[] original = cipher.doFinal(src);
	        return new String(original, CHARSET);
	    } catch (java.security.NoSuchAlgorithmException e1) {
	        logger.error("Aes.decryptToStr e1",e1);
	    } catch (javax.crypto.NoSuchPaddingException e2) {
	        logger.error("Aes.decryptToStr e2",e2);
	    } catch (java.lang.Exception e3) {
	        logger.error("Aes.decryptToStr e3",e3);
	    }
	    return null;
	}
	
	public static void main(String[] args) throws Exception {
	    String iv = "12345Qw81!3*567a"; // 必须16字节, 加解密的iv值可以不同
	    String key = "d36e9d414ee0@3$%";  // 必须16字节  
	    String params = "加解密的iv值";
	    byte[] bs_src = params.getBytes(CHARSET);
	    System.out.println("原来的字符串："+params);
	    String toStr = encryptToStr(iv, key.getBytes(CHARSET), bs_src);
	    System.out.println("加密后的字符串："+toStr);
	    byte[] src_encode = DigestUtil.base64Decoder(toStr);
	    String str = decryptToStr(iv, key.getBytes(CHARSET), src_encode);
	    System.out.println("解密后的字符串："+str);
	 
	}
}
