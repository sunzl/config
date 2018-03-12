package com.namibank.df.fl.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneyUtils {
	
	/**
	 * 金额转换方式
	 * 输出: 123,456,789 
	 * @param pattern
	 * @param bd
	 * @return
	 */
	public static String parseMoney(String pattern,BigDecimal bd){
		DecimalFormat df=new DecimalFormat(pattern);
		    return df.format(bd);
		}

		public static void main(String [] arg){
			
		     BigDecimal bd=new BigDecimal(123456789.589);
		     System.out.println(parseMoney(",###,###",bd)); //out: 123,456,790                         
		     System.out.println(parseMoney("##,####,###",bd)); //out: 123,456,790
		     System.out.println(parseMoney("######,###",bd)); //out: 123,456,790
		     System.out.println(parseMoney("#,##,###,###",bd)); //out: 123,456,790
		     System.out.println(parseMoney(",###,###.00",bd)); //out: 123,456,789.59
		     System.out.println(parseMoney(",###,##0.00",bd)); //out: 123,456,789.59

		     BigDecimal bd1=new BigDecimal(0);
		     System.out.println(parseMoney(",###,###",bd1)); //out: 0
		     System.out.println(parseMoney(",###,###.00",bd1)); //out: .00
		     System.out.println(parseMoney(",###,##0.00",bd1)); //out: 0.00
		}

}
