package com.example.administrator.topactivity.utils;

import android.text.format.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {
	/**
	 * datetimeFormat yyyy-MM-dd hh:mm:ss
	 */
	public static final String datetimeFormat = "yyyy-MM-dd HH:mm:ss";

	/**
	 * datetimeFormat yyyy-MM-dd hh:mm:ss
	 */
	public static final String datetimeFormat2 = "yyyy-MM-dd HH:mm";
	/**
	 * dateFormat yyyy-MM-dd
	 */
	public static final String dateFormat = "yyyy-MM-dd";
	/**
	 * 时间格式   HH:mm:ss
	 */
	private static final String timeFormat = "HH:mm:ss";
	public static final String timeFormatChinese = "yyyy年MM月dd日";
	/**
	 * 日期格式    yyyy.MM.dd
	 */
	public static final String dateFormatSplitMarkPoint = "yyyy.MM.dd";

	public static final String dateFormat2 = "yyyyMMdd";

	private static SimpleDateFormat DatetimeFormatSDF = new SimpleDateFormat(
			datetimeFormat);

	private static SimpleDateFormat TimeFormatSDF = new SimpleDateFormat(
			timeFormat);

	private static SimpleDateFormat DatetimeFormatSDF2 = new SimpleDateFormat(
			datetimeFormat2);

	private static SimpleDateFormat DateFormatSDF = new SimpleDateFormat(
			dateFormat);

	private static SimpleDateFormat dateFormatSpiltMarkPoint = new SimpleDateFormat(
			dateFormatSplitMarkPoint);
	private static final String FORMAT_ONLY_DATE = "yyyy-MM-dd";


	public static String getTime() {
		return getTime(new Date());
	}

	public static String getTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
		return sdf.format(date);
	}
	public static String getDateTimeByFormat(String format)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}
	/**
	 * 获得当前时间 小时分钟
	 *
	 * @return
	 */
	public static String getCurrentTime() {
		String strTime = "";
		Time time = new Time();
		time.setToNow();
		int hour = time.hour;
		if (hour < 10) {
			strTime += "0";
		}
		strTime += hour;
		strTime += ":";
		int minute = time.minute;
		if (minute < 10) {
			strTime += "0";
		}
		strTime += minute;
		return strTime;
	}

	/**
	 * 计算两个时间间隔是否在七天内 03. *
	 *
	 * @param endStr
	 *            结束时间
	 * @param startStr
	 *            开始时间
	 * @param intervalDay
	 *            开始时间与结束时间指定的间隔 *@return 如果开始时间与结束时间的日期间隔之差小于或者intervalDay
	 *
	 */
	public static boolean computeTwoDaysWithInSpecified(String startStr,
														String endStr, final int intervalDay) {
		int tmpDay = intervalDay;
		boolean isPositive = tmpDay == Math.abs(tmpDay);
		Date startDate = transDate(startStr);
		Date endDate = transDate(endStr);
		if (startDate == null || endDate == null) {
			return false; // 日期格式错误，判断不在范围内
		}
		long timeLong = endDate.getTime() - startDate.getTime();
		int dayInterval = (int) (timeLong / 1000 / 60 / 60 / 24);
		if(isPositive){
			return dayInterval >=0 && dayInterval <= intervalDay;
		}else{
			return dayInterval >= intervalDay && dayInterval <=0;
		}
	}


	/**
	 * 获取今天指定天数之前的日期
	 * @param dayInterval
	 * @return
	 */
	public static String findBeforeToday(int dayInterval){
		Calendar calendar = Calendar.getInstance();
		calendar.roll(Calendar.DAY_OF_MONTH, -dayInterval);
		return dateConvertDateString(calendar.getTime());
	}
	/**
	 * 获取今天指定天数之后的日期
	 * @param dayInterval
	 * @return
	 */
	public static  String findAfterToday(int dayInterval)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.roll(Calendar.DAY_OF_MONTH, +dayInterval);
		return dateConvertDateString(calendar.getTime());
	}

	/**
	 * string 转为 Date <br />
	 * 格式是<code>yyyy-MM-dd</code>
	 *
	 * @param str
	 *            "yyyy-MM-dd"
	 * @return
	 */
	public static Date transDate(String str) {
		// Log.v(TAG, "时间字符串:" + str);
		String fmt = FORMAT_ONLY_DATE;
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		Date date = null;
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * string 转为 Date <br />
	 * 格式是<code>yyyy-MM-dd</code>
	 *
	 * @param str
	 *            "yyyy-MM-dd"
	 * @return
	 */
	public static Date transDateTime(String str) {
		// Log.v(TAG, "时间字符串:" + str);
		String fmt = datetimeFormat;
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		Date date = null;
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public static String getNowTime(String format) {
		SimpleDateFormat date = new SimpleDateFormat(format);
		return date.format(new Date(System.currentTimeMillis()));
	}

	/**
	 * 分钟转化成秒
	 *
	 * @param minutes
	 * @return
	 */
	public static int MinutesConvertSeconds(int minutes) {
		return minutes * 60;
	}

	/**
	 * 小时转化成秒
	 *
	 * @param hour
	 * @return
	 */
	public static int HourConvertSeconds(int hour) {
		return MinutesConvertSeconds(hour * 60);
	}

	/**
	 * 日期字符串转化成日期对象
	 *
	 * @param string
	 * @return
	 */
	public static Date stringConvertDateTime(String string) {
		try {
			return DatetimeFormatSDF.parse(string);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 日期字符串转化成日期对象，根据时区
	 *
	 * @param string
	 * @return
	 */
	public static Date stringConvertDateTimeByZone(String string, String timeZone) {
		try {
			DatetimeFormatSDF.setTimeZone(TimeZone.getTimeZone(timeZone));
			return DatetimeFormatSDF.parse(string);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 日期对象转化成日期时间字符串 <br />
	 * 格式：{@value #datetimeFormat}
	 *
	 * @param date
	 * @return
	 */
	public static String dateConvertDateTimeString(Date date) {
		return DatetimeFormatSDF.format(date);
	}

	public static String dateConvertTimeString(Date date) {
		return TimeFormatSDF.format(date);
	}

	/**
	 * 日期对象转化成日期时间字符串 <br />
	 * 格式：{@value #datetimeFormat2}
	 * @param date
	 * @return
	 */
	public static String dateConvertDateTimeString2(Date date) {
		return DatetimeFormatSDF2.format(date);
	}

	public static Date getDateTime(String date) {
		try {
			return DatetimeFormatSDF.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
		//return new Date();
	}

	/**
	 * 日期对象转化成日期时间字符串 <br />
	 * 格式：2012.12.21
	 *
	 * @param date
	 * @return
	 */
	public static String dateConvertDateStringSplitMarkPoint(Date date) {
		return dateFormatSpiltMarkPoint.format(date);
	}

	/**
	 * 日期对象转化成日期时间字符串 <br />
	 * 格式：{@value #dateFormat}
	 *
	 * @param date
	 * @return
	 */
	public static String dateConvertDateString(Date date) {
		return DateFormatSDF.format(date);
	}

	/*
	*/

	/**
	 * 获得指定年、月、日的日期对象
	 *
	 * @param year
	 * @param month
	 *            例如需要获取8月，实际传入的值是7
	 * @param day
	 * @return
	 */
	public static Date getDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		Date date = calendar.getTime();
		return date;
	}

	/**
	 * 时间毫秒数转为日期类型
	 *
	 * @param milliseconds
	 * @return
	 */
	public static Date millisecondsConvertDate(long milliseconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliseconds);
		return calendar.getTime();
	}

	/**
	 * 使用给定的年月日拼接日期字符串(yyyy-MM-dd)
	 *
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static String getDateString(final int year, final int month,
									   final int day) {
		return year + "-" + month + "-" + day;
	}

	/**
	 * @return 获取当前所在的年份
	 */
	public static int getThisYear() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR);
	}

	/**
	 * @return 获取当前所在的月份
	 */
	public static int getThisMonth() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.MONTH);
	}

	/**
	 * @return 获取今天
	 */
	public static int getThisDay() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取星期几
	 * @return
	 */
	public static int getWeekDay(){
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.DAY_OF_WEEK)-1;
	}

	/**
	 * 拼接成指定日期的开始时间
	 *
	 * @param date
	 *            (yyyy-MM-dd)
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String pinFirstTime(String date) {
		return date + " 00:00:00";
	}

	/**
	 * 拼接成指定日期的结束时间
	 *
	 * @param date
	 *            (yyyy-MM-dd)
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String pinLastTime(String date) {
		return date + " 23:59:59";
	}

	/**
	 * 指定的时间点是否在今天之后
	 *
	 * @param aYear
	 * @param aMonth
	 * @param aDay
	 * @return
	 */
	public static boolean after(int aYear, int aMonth, int aDay) {
		Calendar a = Calendar.getInstance();
		a.set(aYear, aMonth, aDay);
		Calendar b = Calendar.getInstance();
		b.set(Calendar.HOUR_OF_DAY, 23);
		b.set(Calendar.MINUTE, 59);
		b.set(Calendar.SECOND, 59);
		return a.after(b);
	}

	/**
	 * 将时间毫秒数转换成天数
	 * @param timeMills
	 * @return
	 */
	public static int convertTimeMillsToDays(long timeMills) {
		if (timeMills > 0) {
			long oneDayMills = 1000 * 60 * 60 * 24;//一天的毫秒数
			return (int) ((timeMills + oneDayMills - 1) / oneDayMills);
		} else {
			return 0;
		}
	}

	public static Date convertStringToDate(String strDate, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String convertDateToString(Date date, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得本天的开始时间，即2014-08-29 00:00:00
	 *
	 * @return
	 */
	public static Date getCurrentDayStartTime() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		try {
			now = sdf.parse(sdf.format(now));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

}
