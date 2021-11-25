package com.reliquary.crow.resources.other;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class handles getting the current formatted date and time and can format a given time in milliseconds
 *
 * @version 1.0 2021-19-11
 * @since 1.0
 */
public class DateAndTime {

	/**
	 * This method gets the local time of the bot and formats it
	 * @return Returns current time
	 */
	public static String getTime() {
		DateFormat time = new SimpleDateFormat("hh:mm aa");
		return time.format(new Date());
	}

	/**
	 * This method gets the local date of the bot and formats it
	 * @return Returns current date
	 */
	public static String getDate() {
		DateFormat date = new SimpleDateFormat("MM/dd/yyy");
		return date.format(new Date());
	}

	/**
	 * This method takes a time in milliseconds and formats it into HH/MM/SS
	 * @param timeInMillis Provides the time in milliseconds
	 * @return Returns the formatted time
	 */
	public static String formatTime(long timeInMillis) {
		return String.format("%02d:%02d:%02d",
			TimeUnit.MILLISECONDS.toHours(timeInMillis),
			TimeUnit.MILLISECONDS.toMinutes(timeInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMillis)),
			TimeUnit.MILLISECONDS.toSeconds(timeInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
		);
	}
}
