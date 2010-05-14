package chau.nguyen;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class EventManager {
	public static Uri EVENTS_URI = Uri.parse("content://calendar/events");
	public static Uri REMINDERS_URI = Uri.parse("content://calendar/reminders");
	private Cursor monthCursor;
	private Set<Long> dateHasEvents;
	
	private ContentResolver contentResolver;
	public EventManager(ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
	}
	
	private Cursor getEventsByDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long beginDate = cal.getTimeInMillis();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		long beginNextDate = cal.getTimeInMillis();
		Cursor cur = this.contentResolver.query(EVENTS_URI, null, "dtstart >= ? AND dtstart < ?", new String[] { String.valueOf(beginDate), String.valueOf(beginNextDate) }, null);
		return cur;
	}
	
	public String getSumarize(Date date) {
		Cursor cur = getEventsByDate(date);
		String sumarize = "";
		if (cur.moveToFirst()) {
			int titleColumn = cur.getColumnIndex("title");
			int locationColumn = cur.getColumnIndex("eventLocation");
			String title;
			String location;
			do {
				title = cur.getString(titleColumn);
				location = cur.getString(locationColumn);
				location = (location == null || location.length() == 0 ? "" : " (" + location + ")");
				if (sumarize.length() == 0) {
					sumarize = title + location;
				} else {
					sumarize = sumarize + ", " + title + location;
				}
			} while (cur.moveToNext());
			
		}
		return sumarize;
	}
	
	public void setMonth(int month, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long beginDateOfMonth = cal.getTimeInMillis();
		cal.add(Calendar.MONTH, 1);
		long beginDateOfNextMonth = cal.getTimeInMillis();
		monthCursor = this.contentResolver.query(EVENTS_URI, new String[] { "dtstart" }, "dtstart >= ? AND dtstart < ?", new String[] { String.valueOf(beginDateOfMonth), String.valueOf(beginDateOfNextMonth) }, null);
		dateHasEvents = new HashSet<Long>();
		if (monthCursor.moveToFirst()) {
			int dateColumn = monthCursor.getColumnIndex("dtstart");
			long date;
			do {
				date = monthCursor.getLong(dateColumn);
				cal.setTimeInMillis(date);
				date = cal.get(Calendar.DAY_OF_MONTH) + 
					cal.get(Calendar.MONTH) * 100 + cal.get(Calendar.YEAR) * 10000;							
				if (dateHasEvents.contains(date)) {
					continue;
				}
				dateHasEvents.add(date);
			} while (monthCursor.moveToNext());
		}
	}
	
	public boolean hasEvent(int dayOfMonth, int month, int year) {
		long date = dayOfMonth + month * 100 + year * 10000;		
		return dateHasEvents.contains(date);		
	}
	
}
