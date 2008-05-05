package calendar.RemoteCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.BufferedInputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TimeZone;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Summary;

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
	
	public ICalCalendarModel(String uri, String proxy, int proxyport) {
		super();
		
		setUri(uri, proxy, proxyport);
	}
	
	public ICalCalendarModel(String uri) {
		this(uri, null, 0);
	}
	
	private void setUri(String uri, String proxy, int proxyport) {
		try {
			String oldUri = this.uri;
			this.uri = uri;
			URL url = new URL(uri);
			URLConnection urlCon;
			if(proxy != null)
				urlCon = url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy, proxyport)));
			else
				urlCon = url.openConnection();
			
			BufferedInputStream in = new BufferedInputStream(urlCon.getInputStream());

			CalendarBuilder builder = new CalendarBuilder();
			iCalendar = builder.build(in);
			updateAppointments();
			
			propertyChangeSupport.firePropertyChange("uri", oldUri, this.uri);
		} catch(Throwable t) {
			t.printStackTrace();
			LOG.fatal("FATAL Error: " + t.getMessage());
		}
	}
	
	private void updateAppointments() {
		Collection<IAppointment> appointments = new ArrayList<IAppointment>();
		
		for(Iterator<Component> i = iCalendar.getComponents().iterator(); i.hasNext();) {
			Component component = i.next();
			
			if(component.getName() != "VEVENT")
				continue;
			
			Appointment appointment = new Appointment();

			if(component.getProperty("DTSTART") == null)
			{
				LOG.info("no Start date");
				continue;
			}
			if(component.getProperty("DTEND") == null)
			{
				LOG.info("no end date");
				continue;
			}
				
			appointment.setAppointmentStartDate(new Date(((DtStart)component.getProperty("DTSTART")).getDate().getTime()));
			appointment.setAppointmentEndDate(new Date(((DtEnd)component.getProperty("DTEND")).getDate().getTime()));
			
			if(component.getProperty("SUMMARY") != null) {
				String summary = ((Summary)component.getProperty("SUMMARY")).getValue();
				appointment.setAppointmentName(summary);
			}
			
			if(component.getProperty("DESCRIPTION") != null) {
				String description = ((Description)component.getProperty("DESCRIPTION")).getValue();
				appointment.setAppointmentDescription(description);
			}

			int sequenceNr = ((Sequence)component.getProperty("SEQUENCE")).getSequenceNo();
			if(sequenceNr == 1) {
				// probably a allday event
				appointment.setAppointmentType(IAppointment.AppointmentType.ALLDAY);
			}
			
			appointments.add(appointment);
		}
		
		this.setAppointments(appointments);
	}
	/*
	private Date parseDate(String date) {
		LOG.info("to parse: " + date);
		java.util.Calendar cal = java.util.Calendar.getInstance();
		if(date.indexOf(":") != -1) {
			String timeZone = date.substring(0, date.indexOf(":"));
			LOG.info("timezone: " + timeZone);
			cal.setTimeZone(TimeZone.getTimeZone(timeZone));
			
			date = date.substring(date.indexOf(':')+1);
		}
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
	} */
}
