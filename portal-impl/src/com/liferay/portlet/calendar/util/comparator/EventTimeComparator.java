/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.calendar.util.comparator;

import com.liferay.portal.kernel.util.Time;
import com.liferay.portlet.calendar.model.CalEvent;
import com.liferay.portlet.calendar.util.CalUtil;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <a href="EventTimeComparator.java.html"><b><i>View Source</i></b></a>
 *
 * @author Samuel Kong
 */
public class EventTimeComparator implements Comparator<CalEvent> {

	public EventTimeComparator(TimeZone timeZone, Locale locale) {
		_timeZone = timeZone;
		_locale = locale;
	}

	public int compare(CalEvent event1, CalEvent event2) {
		boolean allDay1 = CalUtil.isAllDay(event1, _timeZone, _locale);
		boolean allDay2 = CalUtil.isAllDay(event2, _timeZone, _locale);

		if (allDay1 && allDay2) {
			return compareTitle(event1, event2);
		}
		else if (allDay1) {
			return -1;
		}
		else if (allDay2) {
			return 1;
		}

		int value = compareTime(
			getStartDate(event1, _timeZone), getStartDate(event2, _timeZone));

		if (value != 0) {
			return value;
		}

		value = compareTime(
			getEndDate(event1, _timeZone), getEndDate(event2, _timeZone));

		if (value != 0) {
			return value;
		}

		return compareTitle(event1, event2);
	}

	protected int compareTitle(CalEvent event1, CalEvent event2) {
		return event1.getTitle().toLowerCase().compareTo(
			event2.getTitle().toLowerCase());
	}

	protected int compareTime(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance(_timeZone, _locale);

		cal1.setTime(date1);

		cal1.set(Calendar.YEAR, 1970);
		cal1.set(Calendar.MONTH, 1);
		cal1.set(Calendar.WEEK_OF_MONTH, 1);
		cal1.set(Calendar.DAY_OF_YEAR, 1);

		Calendar cal2 = Calendar.getInstance(_timeZone, _locale);

		cal2.setTime(date2);

		cal2.set(Calendar.YEAR, 1970);
		cal2.set(Calendar.MONTH, 1);
		cal2.set(Calendar.WEEK_OF_MONTH, 1);
		cal2.set(Calendar.DAY_OF_YEAR, 1);

		return cal1.compareTo(cal2);
	}

	protected Date getEndDate(CalEvent event, TimeZone timeZone) {
		if (event.isTimeZoneSensitive()) {
			return Time.getDate(CalUtil.getEndTime(event), timeZone);
		}
		else {
			return CalUtil.getEndTime(event);
		}
	}

	protected Date getStartDate(CalEvent event, TimeZone timeZone) {
		if (event.isTimeZoneSensitive()) {
			return Time.getDate(event.getStartDate(), timeZone);
		}
		else {
			return event.getStartDate();
		}
	}

	private TimeZone _timeZone;
	private Locale _locale;

}