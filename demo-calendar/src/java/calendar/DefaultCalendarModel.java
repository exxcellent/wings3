package calendar;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Default Calendar Model for the AppointmentCalendar
 * 
 * @author Florian Roks
 *
 */
public class DefaultCalendarModel implements CalendarModel {
	private static final Log LOG = LogFactory.getLog(DefaultCalendarModel.class);
	public PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private CalendarView view;
	protected Collection<IAppointment> appointments;
	private Date visibleFrom;
	private Date visibleUntil;
	private Date date;
	private ArrayList<CalendarViewChangeListener> viewChangeListener = new ArrayList<CalendarViewChangeListener>();
	private Locale locale;
	
	/**
	 * Constructs the DefaultCalendarModel
	 */
	public DefaultCalendarModel()
	{
		setDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
		setLocale(Locale.getDefault());
		setView(CalendarView.MONTH);
	}
	
	@Override
	public CalendarView getView() {
		return view;
	}

	/**
	 * Sets the date
	 * @param date Date
	 */
	public void setDate(Date date) 
	{
		if(date == null)
			throw new IllegalArgumentException("date must be not null");
		
		Date oldVal = this.date;
		this.date = date;

		updateVisibleDates();
		
		if(oldVal != date)
			fireViewChangeEvent(new CalendarViewChangeEvent(this, date));
	}

	public Date getDate()
	{
		return this.date;
	}
	
	public Collection<IAppointment> getAppointments()
	{
		return appointments;
	}
	
	/**
	 * Sets the type of view on this calendar (e.g. MONTH, DAY, WEEK)
	 * @param view The view to be set  
	 */
	public void setView(CalendarView view) {
		CalendarView oldVal = this.view;
		this.view = view;

		if(date == null)
			return;
		
		updateVisibleDates();
		
		if(oldVal != view)
			fireViewChangeEvent(new CalendarViewChangeEvent(this, view));
	}
 
	private void updateVisibleDates()
	{
		if(this.view == null)
			return;
		
		switch(this.view)
		{
			case MONTH:
				Calendar calendar = Calendar.getInstance();
				
				calendar.setTime(this.date);
				calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
				
				Calendar begin = (Calendar)calendar.clone();
				begin.add(Calendar.DAY_OF_YEAR, -1);		
				begin.add(Calendar.DAY_OF_YEAR, -((begin.get(Calendar.DAY_OF_WEEK) + 5) % 7));
				
				Calendar end = (Calendar)calendar.clone();
				end.add(Calendar.WEEK_OF_YEAR, 5);
				end.add(Calendar.DAY_OF_YEAR, ((8 - end.get(Calendar.DAY_OF_WEEK)) % 7));
				
				if(locale == Locale.US)
				{
					begin.add(Calendar.DAY_OF_YEAR, -1);
					end.add(Calendar.DAY_OF_YEAR, -1);
				}
			
				setVisibleFrom(new Date(begin.getTimeInMillis()));
				setVisibleUntil(new Date(end.getTimeInMillis()));
			break;
			case WEEK:
				Calendar calendar2 = Calendar.getInstance();
				calendar2.setTime(this.date);

				calendar2.add(Calendar.DAY_OF_YEAR, -2);
				setVisibleFrom(new Date(calendar2.getTimeInMillis()));
				
				calendar2.setTime(this.date);
				calendar2.add(Calendar.DAY_OF_YEAR, +5);
				setVisibleUntil(new Date(calendar2.getTimeInMillis()));
			break;
			case DAY:
			break;
		}
	}
	/**
	 * Returns if the appointment is on the day of date 
	 * @param appointment
	 * @param date
	 * @return
	 */
	private boolean isAppointmentOnDateDay(IAppointment appointment, Date date)
	{
		Calendar calTestDate = Calendar.getInstance();
		calTestDate.setTime(date);
		
		Calendar appStartDate = Calendar.getInstance();
		Date startDate = appointment.getAppointmentStartDate();
		if(startDate == null) {
			LOG.warn("Appointment with startDate == null");
			return false;
		}
		appStartDate.setTime(startDate);
		
		Calendar appEndDate = Calendar.getInstance();
		Date endDate = appointment.getAppointmentEndDate();
		if(endDate == null) {
			LOG.warn("Appointment with endDate == null");
			return false;
		}
		appEndDate.setTime(endDate);
		
		if(appStartDate.get(Calendar.YEAR) <= calTestDate.get(Calendar.YEAR) && 
				appEndDate.get(Calendar.YEAR) >= calTestDate.get(Calendar.YEAR))
		{
			// date is in the correct year
			if(appStartDate.get(Calendar.DAY_OF_YEAR) <= calTestDate.get(Calendar.DAY_OF_YEAR) &&
					appEndDate.get(Calendar.DAY_OF_YEAR) >= calTestDate.get(Calendar.DAY_OF_YEAR)
					)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isAppointmentAdded(IAppointment appointment, Date date)
	{
		if(isAppointmentOnDateDay(appointment, date))
		{
			if(appointment.isAppointmentRecurring())
			{
				if(appointment.getAppointmentRecurringDays().contains(Appointment.getWeekdayFromDate(date)))
					return true;
			}
			else
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Compares a Date Timestamps 
	 * @author Florian Roks
	 *
	 */
	private class TimeComparator implements Comparator<IAppointment>
	{
		@Override
		public int compare(IAppointment arg0, IAppointment arg1) {
			Date date0 = arg0.getAppointmentStartDate();
			Date date1 = arg1.getAppointmentStartDate();
			 
			// this will fail if the dates are to far apart
			return (int)(date0.getTime()-date1.getTime());
		}
	}
	
	protected Comparator<IAppointment> timeComparator = new TimeComparator();
	
	/**
	 * Returns the appointments on date
	 */
	@Override
	public Collection<IAppointment> getAppointments(Date date) {
		if(appointments == null)
			return null;
		
		ArrayList<IAppointment> eventListAllDay = new ArrayList<IAppointment>();
		ArrayList<IAppointment> eventListNormal = new ArrayList<IAppointment>();
		
		// TODO: Pregenerate this data from visiblefrom to visibleuntil on setAppointments
		for(IAppointment appointment:appointments)
		{
			// this should add all appointments that are active on this day
			if(isAppointmentAdded(appointment, date))
			{
				if(appointment.getAppointmentType() == IAppointment.AppointmentType.NORMAL)
					eventListNormal.add(appointment);
				if(appointment.getAppointmentType() == IAppointment.AppointmentType.ALLDAY)
					eventListAllDay.add(appointment);
			}
		}
		Collections.sort(eventListAllDay, timeComparator);
		Collections.sort(eventListNormal, timeComparator);
		
		
		eventListAllDay.addAll(eventListNormal);
		
		return eventListAllDay;
	}

	@Override
	public Date getVisibleFrom() {
		return visibleFrom;
	}

	@Override
	public Date getVisibleUntil() {
		return visibleUntil;
	}

	@Override
	public void setVisibleFrom(Date visibleFrom) {
		this.visibleFrom = visibleFrom;
	}

	@Override
	public void setVisibleUntil(Date visibleUntil) {
		this.visibleUntil = visibleUntil;
	}


	/**
	 * Sets the Appointments for this CalendarModel
	 * @param appointments
	 */
	public void setAppointments(Collection<IAppointment> appointments) {
		this.appointments = appointments;
		
		// TODO: implement a equals/hashCode for appointments to check if they changed
		fireViewChangeEvent(new CalendarViewChangeEvent(this, appointments));
	}

	@Override
	public int getColumnCount() {
		// changes here do screw up the layout, as the CG can't handle it yet
		switch(this.view)
		{
			case NONE:
				return 0;
			case MONTH:
				return 7; // maximum with the default CG
		}
		return 0;
	}

	@Override
	public int getRowCount() {
		switch(this.view)
		{
			case NONE:
				return 0;
			case MONTH:
				return 6;
		}
		
		return 0;
	}

	@Override
	public CustomCellRenderer getCustomCellRenderer() {
		return null;
	}

	@Override
	public int getMaxNumberAppointmentsPerCell() {
		switch(this.view)
		{
			case MONTH:
				return 4;
			case WEEK:
				return 10;
			case DAY:
				return 1000;
		}
		
		return 1000;
	}

	private void fireViewChangeEvent(CalendarViewChangeEvent e) {
		for(CalendarViewChangeListener listener:viewChangeListener)
		{
			listener.valueChanged(e);
		}
	}
	
	@Override
	public String getUniqueAppointmentID(Date date, IAppointment appointmentToGetIDFrom) {
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(date);
		Collection<IAppointment> appointments = this.getAppointments(date);
		int i = 0;
		for(IAppointment appointment:appointments)
		{
			if(appointment == appointmentToGetIDFrom)
			{
				String uniqueIDApp = tempCal.get(Calendar.YEAR) + ":" + tempCal.get(Calendar.DAY_OF_YEAR) + ":" + i;
				return uniqueIDApp;
			}
			i++;
		}
		
		return null;
	}
	
	@Override
	public IAppointment getAppointmentFromID(String uniqueID) {
		String[] values = uniqueID.split(":"); // year:day_of_year:appointmentNR
		
		Calendar tempCal = Calendar.getInstance();
		tempCal.set(Calendar.YEAR, Integer.parseInt(values[0]));
		tempCal.set(Calendar.DAY_OF_YEAR, Integer.parseInt(values[1]));

		Collection<IAppointment> appointments = this.getAppointments(new java.sql.Date(tempCal.getTimeInMillis()));
		int i = 0;
		int lookForNr = Integer.parseInt(values[2]);
		for(IAppointment appointment:appointments)
		{
			if(i == lookForNr)
			{
				return appointment;
			}
			i++;
		}
		
		return null;
	}

	@Override
	public void addCalendarViewChangeListener(CalendarViewChangeListener listener) {
		viewChangeListener.add(listener);
	}

	@Override
	public void removeCalendarViewChangeListener(CalendarViewChangeListener listener) {
		viewChangeListener.add(listener);
	}

	@Override
	public Locale getLocale() {
		return this.locale;
	}

	@Override
	public void setLocale(Locale locale) {
		Locale oldVal = this.locale;
		this.locale = locale;
		
		propertyChangeSupport.firePropertyChange("locale", oldVal, locale);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
}
