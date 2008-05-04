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
		switch(type) {
			case ICAL:
				model = new ICalCalendarModel(uri);
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
