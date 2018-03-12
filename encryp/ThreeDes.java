package com.nami.encryp;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 3DES加密
 * 初始向量：必须是8字节任意值
 * 安全密钥：必须是24/32/64字节任意值
 */
public class ThreeDes {
    
	private static Logger logger = LoggerFactory.getLogger(ThreeDes.class);
		
	private static final String ALGORITHM_KEY = "DESede";// 
	private static final String ALGORITHM_INSTANCE = "DESede/CBC/PKCS5Padding";//加密算法/工作模式/填充方式

	
	/**
	 * 加密
	 * @param iv
	 * @param keybyte
	 * @param src
	 * @return
	 */
	public static String encryptMode(String iv, byte[] keybyte, byte[] src) {
	    try {
	         Key deskey = null ; 
	         DESedeKeySpec spec = new DESedeKeySpec(keybyte); 
	         SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(ALGORITHM_KEY); 
	         deskey = keyfactory.generateSecret(spec); 
	         Cipher cipher = Cipher.getInstance(ALGORITHM_INSTANCE); 
	         IvParameterSpec ips = new IvParameterSpec(iv.getBytes(DigestUtil.CHARSET)); 
	         cipher.init(Cipher.ENCRYPT_MODE, deskey, ips); 
	         byte [] encryptData = cipher.doFinal(src); 
	         return DigestUtil.base64Encoder(encryptData); 
	    } catch (java.security.NoSuchAlgorithmException e1) {
	    	logger.error("ThreeDes.encryptMode e1",e1);
	    } catch (javax.crypto.NoSuchPaddingException e2) {
	    	logger.error("ThreeDes.encryptMode e2",e2);
	    } catch (Throwable e3) {
	        logger.error("ThreeDes.encryptMode e3",e3);
	    }
	    return null;
	}

	/**
	 * 解密
	 * @param iv
	 * @param keybyte
	 * @param src
	 * @return
	 */
	public static String decryptMode(String iv, byte[] keybyte, byte[] src)  {
	    try{
	     Key deskey = null ; 
	     DESedeKeySpec spec = new DESedeKeySpec(keybyte);  
	     SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(ALGORITHM_KEY); 
	     deskey = keyfactory.generateSecret(spec); 
	     Cipher cipher = Cipher.getInstance(ALGORITHM_INSTANCE); 
	     IvParameterSpec ips = new IvParameterSpec(iv.getBytes(DigestUtil.CHARSET)); 
	     cipher.init(Cipher.DECRYPT_MODE, deskey, ips); 
	     byte [] decryptData = cipher.doFinal(src); 
	     return new String(decryptData,DigestUtil.CHARSET); 
	    }catch(Throwable e1){
	    	logger.error("ThreeDes.decryptMode e1",e1);
	    }
	    return null;
	}
	

	public static void main(String[] args) throws Exception{
	    //String key = "d36e9d2555414ee0182e55x2ae055b4p3f7a4122273d5b4x34896d5b434pd4d7"; // 64字节
	    //String key = "d36e9d2555414ee0d36e9d2555414ee0"; // 32字节
		String key = "d36e9d2555414ee0d36e9d25"; // 24字节
	    String params = "{\"body\":{\"bid_no\":\"PHAEQLFRDDDFNOOC\"},\"head\":{\"appid\":\"LNVEweUeMcusx45e\",\"transactionId\":\"95f24834ffb841559f1931057de41111\"}}";
	    String iv = "01234567";
	    byte[] bs_src = params.getBytes(DigestUtil.CHARSET);
	    String bs_encode = encryptMode(iv,key.getBytes(DigestUtil.CHARSET), bs_src);
	    byte[] decoder = DigestUtil.base64Decoder(bs_encode);
	    String decryptMode = decryptMode(iv, key.getBytes(), decoder);
	    System.out.println("源来的内容："+ params);
	    System.out.println("加密后内容："+ bs_encode);
	    System.out.println("解密后内容："+ decryptMode);
	}
}
