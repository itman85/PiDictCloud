package com.gae.pidictcloud.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilFn {
	public static String truncateWords(String str,int n){
		if(str==null || "".equals(str))
			return "";
		String[] words = str.split(" ");  
		String res ="";  
		for(int i=0;i<words.length && i<n;i++)
			res+= words[i]+" ";
		if(words.length>n)
			res+="...";
		return res.trim();
	}
	
	public static String getDateString(Date date,String format){
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
}
