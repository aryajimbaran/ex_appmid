package com.numazu.export.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	
	public static String currentDate() {
//		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		Date date = new Date();
		return dateFormat.format(date);
		
	}

}
