package com.reliquary.crow.resources.RandomClasses;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateAndTime {

	/*
	getTime
	Get local time for bot
	 */
	public static String getTime() {
		DateFormat time = new SimpleDateFormat("hh:mm aa");
		return time.format(new Date());
	}

	/*
	getDate
	Get local date for bot
	 */
	public static String getDate() {
		DateFormat date = new SimpleDateFormat("MM/dd/yyy");
		return date.format(new Date());
	}

	/*
	formatTime
	Formats time given the milliseconds
	 */
	public static String formatTime(long timeInMillis) {
		return String.format("%02d:%02d:%02d",
			TimeUnit.MILLISECONDS.toHours(timeInMillis),
			TimeUnit.MILLISECONDS.toMinutes(timeInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMillis)),
			TimeUnit.MILLISECONDS.toSeconds(timeInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
		);
	}
}
