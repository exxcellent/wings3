package calendar.RemoteCalendar;

import calendar.CalendarModel;

/**
 * Remote Calendar - proof of concept
 * @author Florian Roks
 *
 */
public class RemoteCalendar {
	
	CalendarModel model;
	
	public RemoteCalendar(CalendarType type, String uri) {
		this(type, uri, null, 0);
	}
	
	public RemoteCalendar(CalendarType type, String uri, String proxy, int proxyport) {
		switch(type) {
			case ICAL:
				model = new ICalCalendarModel(uri, proxy, proxyport);
			break;
		}
	}
	
	public CalendarModel getModel()	{
		return model;
	}
	
	public enum CalendarType {
		ICAL
	}
}
