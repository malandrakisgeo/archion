package org.georgemalandrakis.archion.other;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DateUtil {

	public static Date parseString(String dateString) {
		Date date = now();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		try {
			java.util.Date parsedDate = dateFormat.parse(dateString);
			date = new Date(parsedDate.getTime());
		} catch (ParseException exp) {}
		return date;
	}

	public static Date now() {
		return new Date(new java.util.Date().getTime());
	}

	public static Boolean datePassed(Date checkDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.format(checkDate).compareTo(dateFormat.format(now())) < 0;
	}

	public static Boolean compareDate(Date startDate, Date endDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.format(startDate).compareTo(dateFormat.format(endDate)) == 0;
	}

	public static String getYear(Date dateIn) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		return dateFormat.format(dateIn);
	}

	public static String getShortYear(Date dateIn) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yy");
		return dateFormat.format(dateIn);
	}

	public static String getMonth(Date dateIn) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
		return dateFormat.format(dateIn);
	}


	public static String getShortDate(Date dateIn) {
		if (dateIn != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
			return dateFormat.format(dateIn);
		} else {
			return  null;
		}

	}


	public static String getDate(Date dateIn) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
		return dateFormat.format(dateIn);
	}

	public static Date addDay(Date dateIn, Integer days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateIn);
		cal.add(Calendar.DATE, days);

		return new Date(cal.getTimeInMillis());
	}

	public static Date addMonth(Date dateIn, Integer months) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateIn);
		cal.add(Calendar.MONTH, months);

		return new Date(cal.getTimeInMillis());
	}

	public static Date addYear(Date dateIn, Integer years) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateIn);
		cal.add(Calendar.YEAR, years);

		return new Date(cal.getTimeInMillis());
	}
}
