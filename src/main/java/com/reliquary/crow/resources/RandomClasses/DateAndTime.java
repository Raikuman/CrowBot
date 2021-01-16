package com.reliquary.crow.resources.RandomClasses;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndTime {

	/*
	getTime
	Get local time for bot
	 */
	public static String getTime() {
		DateFormat time = new SimpleDateFormat("hh:mm aa");
		return time.format(new Date()).toString();
	}

	/*
	getDate
	Get local date for bot
	 */
	public static String getDate() {
		DateFormat date = new SimpleDateFormat("MM/dd/yyy");
		return date.format(new Date()).toString();
	}

}
