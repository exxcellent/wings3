package calendar.RemoteCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.BufferedInputStream;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import calendar.Appointment;
import calendar.DefaultCalendarModel;
import calendar.IAppointment;

/**
 * Example Calendar Model for the iCal Format (using ical4j) - it's incomplete, as it's only a proof-of-concept 
 * @author Florian Roks
 *
 */
public class ICalCalendarModel extends DefaultCalendarModel {
	private static final Log LOG = LogFactory.getLog(ICalCalendarModel.class);
	private String uri;
	private Calendar iCalendar;
	
	public ICalCalendarModel(String uri) {
		super();
		
		setUri(uri);
	}
	
	private void setUri(String uri) {
		try {
			String oldUri = this.uri;
			this.uri = uri;
			URL url = new URL(uri);
			BufferedInputStream in = new BufferedInputStream(url.openStream());
				
			CalendarBuilder builder = new CalendarBuilder();
			iCalendar = builder.build(in);
			updateAppointments();
			
			propertyChangeSupport.firePropertyChange("uri", oldUri, this.uri);
		} catch(Exception e) {
			LOG.fatal(e.getMessage());
		}
	}
	
	private void updateAppointments() {
		Collection<IAppointment> appointments = new ArrayList<IAppointment>();
		
		for(Iterator<Component> i = iCalendar.getComponents().iterator(); i.hasNext();) {
			Component component = i.next();
			
			if(component.getName() != "VEVENT")
				continue;
			
			Appointment appointment = new Appointment();
			
			for(Iterator<Property> j = component.getProperties().iterator(); j.hasNext();) {
				Property property = j.next();
				if(property.getName() == "DTSTART") {
					Date date = parseDate(property.getValue());
					appointment.setAppointmentStartDate(date);
				}
				if(property.getName() == "DTEND") {
					Date date = parseDate(property.getValue());
					appointment.setAppointmentEndDate(date);
				}
				if(property.getName() == "SUMMARY") {
					appointment.setAppointmentName(property.getValue());
				}
				if(property.getName() == "DESCRIPTION") {
					appointment.setAppointmentDescription(property.getValue());
				}
				LOG.info("[" + property.getName() + ":" + property.getValue() + "]");
			}
			
			appointments.add(appointment);
		}
		
		this.setAppointments(appointments);
	}
	
	private Date parseDate(String date) {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		int year = Integer.parseInt(date.substring(0, 4));
		int month  = Integer.parseInt(date.substring(4, 6)) -1;
		int day = Integer.parseInt(date.substring(6, 8));
		int hours = Integer.parseInt(date.substring(9, 11));
		int minutes = Integer.parseInt(date.substring(11, 13));
		int seconds = Integer.parseInt(date.substring(13, 15));
		
//		LOG.info("date: " + year + " " + month + " " + day + " " + hours + ":" + minutes + ":" + seconds);
		cal.set(java.util.Calendar.YEAR, year);
		cal.set(java.util.Calendar.MONTH, month);
		cal.set(java.util.Calendar.DAY_OF_MONTH, day);
		
		cal.set(java.util.Calendar.HOUR_OF_DAY, hours);
		cal.set(java.util.Calendar.MINUTE, minutes);
		cal.set(java.util.Calendar.SECOND, seconds);

		return new java.sql.Date(cal.getTimeInMillis());
	}
}
