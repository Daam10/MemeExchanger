package ru.miron.Utils;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ru.miron.Entities.DateInterval;

public class CommonUtils {
	private static final int hoursInDay = 24;
	private static final int minutesInHour = 60;
	private static final int secondsInMinute = 60;
	
	public static String getEnumeration(List<String> values, String delimiter) {
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < values.size() - 1; i++) {
			result.append(values.get(i) + delimiter);
		}
		result.append(values.get(values.size() - 1));
		return result.toString();
	}

	public static DateInterval getDateIntervalWithMinFromDate(List<DateInterval> lastDateIntervals) {
		Iterator<DateInterval> iterator = lastDateIntervals.iterator();
		DateInterval minDateInterval = iterator.next();
		while(iterator.hasNext()) {
			DateInterval next = iterator.next();
			if(minDateInterval.getFromDate().after(next.getFromDate())) {
				minDateInterval = next;
			}
		}
		return minDateInterval;
	}
	
	public static Date getSumm(Date d1, Date d2) {
		return new Date(d1.getTime() + d2.getTime());
	}
	
	public static int getSecondsFromDays(int days) {
		return days * hoursInDay * minutesInHour * secondsInMinute;
	}
	
	public static int getSecondsFromHours(int hours) {
		return hours * minutesInHour * secondsInMinute;
	}

	public static String connectTo(String[] splittedByDots, int to) {
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < to; i++) {
			result.append(splittedByDots[i]);
		}
		return result.toString();
	}
}
