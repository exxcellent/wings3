package calendar;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Date;
import java.util.*;

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
	protected Collection<Appointment> appointments;
	private Date visibleFrom;
	private Date visibleUntil;
	private Date date;
	private ArrayList<CalendarViewChangeListener> viewChangeListener = new ArrayList<CalendarViewChangeListener>();
	private Locale locale;
    private boolean mergeWeekends;
    private Map<Integer, Collection<Appointment>> dateAppointmentsMap = new LinkedHashMap<Integer, Collection<calendar.Appointment>>();
    private Calendar tempCalFrom = Calendar.getInstance();
    private Calendar tempCalUntil = Calendar.getInstance();

    /**
	 * Constructs the DefaultCalendarModel
	 */
	public DefaultCalendarModel()
	{
		setDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
		setLocale(Locale.getDefault());
		setView(CalendarView.MONTH);
    }

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

	public Collection<Appointment> getAppointments()
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
	private boolean isAppointmentOnDateDay(Appointment appointment, Date date)
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

	private boolean isAppointmentAdded(Appointment appointment, Date date)
	{
		if(isAppointmentOnDateDay(appointment, date))
		{
            if(appointment.isAppointmentRecurring())
			{
				if(appointment.getAppointmentRecurringDays().contains(DefaultAppointment.getWeekdayFromDate(date)))
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
	private class TimeComparator implements Comparator<Appointment>
	{

		public int compare(Appointment arg0, Appointment arg1) {
			Date date0 = arg0.getAppointmentStartDate();
			Date date1 = arg1.getAppointmentStartDate();

			// this will fail if the dates are to far apart
			return (int)(date0.getTime()-date1.getTime());
		}
	}

	protected Comparator<Appointment> timeComparator = new TimeComparator();



    /**
	 * Returns the appointments on date
	 */
	public Collection<Appointment> getAppointments(Date date) {
        tempCalUntil.setTimeInMillis(date.getTime());
        return dateAppointmentsMap.get(tempCalUntil.get(Calendar.DAY_OF_YEAR));
    }

	public Date getVisibleFrom() {
		return visibleFrom;
	}

	public Date getVisibleUntil() {
		return visibleUntil;
	}

	public void setVisibleFrom(Date visibleFrom) {
		this.visibleFrom = visibleFrom;

        if(getVisibleFrom() != null && getAppointments() != null)
            updateDateAppointmentMapping();
	}

	public void setVisibleUntil(Date visibleUntil) {
		this.visibleUntil = visibleUntil;

        if(getVisibleUntil() != null && getAppointments() != null)
            updateDateAppointmentMapping();
	}

    public boolean isVisible(Date date) {
        Calendar from = Calendar.getInstance();
        Calendar until = (Calendar)from.clone();
        Calendar checkDate = (Calendar)from.clone();

        from.setTime(visibleFrom);
        until.setTime(visibleUntil);
        checkDate.setTime(date);

        if(from.get(Calendar.YEAR) <= checkDate.get(Calendar.YEAR) &&
                until.get(Calendar.YEAR) >= checkDate.get(Calendar.YEAR))
        {
            // date is in the correct year
            if(from.get(Calendar.DAY_OF_YEAR) <= checkDate.get(Calendar.DAY_OF_YEAR) &&
                    until.get(Calendar.DAY_OF_YEAR) >= checkDate.get(Calendar.DAY_OF_YEAR)
                    )
            {
                return true;
            }
        }

        return false;
    }

    private void updateDateAppointmentMapping()
    {
        tempCalUntil.setTime(getVisibleUntil());
        tempCalFrom.setTime(getVisibleFrom());
        dateAppointmentsMap.clear();
        
        while(tempCalFrom.before(tempCalUntil) || tempCalFrom.equals(tempCalUntil))
        {
            ArrayList<calendar.Appointment> eventListAllDay = new ArrayList<calendar.Appointment>();
            ArrayList<calendar.Appointment> eventListNormal = new ArrayList<calendar.Appointment>();

            if(appointments == null)
                break;

            for(calendar.Appointment appointment:appointments)
            {
                // this should add all appointments that are active on this day
                if(isAppointmentAdded(appointment, new java.sql.Date(tempCalFrom.getTimeInMillis())))
                {
                    if(appointment.getAppointmentType() == calendar.Appointment.AppointmentType.NORMAL)
                        eventListNormal.add(appointment);
                    if(appointment.getAppointmentType() == calendar.Appointment.AppointmentType.ALLDAY)
                        eventListAllDay.add(appointment);
                }
            }
            Collections.sort(eventListAllDay, timeComparator);
            Collections.sort(eventListNormal, timeComparator);


            eventListAllDay.addAll(eventListNormal);

            dateAppointmentsMap.put(tempCalFrom.get(Calendar.DAY_OF_YEAR), eventListAllDay);

            tempCalFrom.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

	/**
	 * Sets the Appointments for this CalendarModel
	 * @param appointments
	 */
	public void setAppointments(Collection<Appointment> appointments) {
	    // if only properties of objects in the list changed, this.appointments.equals(appointments)
        propertyChangeSupport.firePropertyChange("preAppointmentsChange", null, appointments);

        this.appointments = appointments;

        if(getVisibleUntil() != null && getVisibleFrom() != null)
            updateDateAppointmentMapping();

		fireViewChangeEvent(new CalendarViewChangeEvent(this, appointments));
	}

	public int getColumnCount() {
		switch(this.view)
		{
			case NONE:
				return 0;
			case MONTH:
				return 7; // maximum with the default CG
		}
		return 0;
	}

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

	public CustomCellRenderer getCustomCellRenderer() {
		return null;
	}

	public int getMaxNumberAppointmentsPerCell(boolean isMerged) {
		switch(this.view)
		{
			case MONTH:
                if(isMerged)
                    return 1;
                return 4;
			case WEEK:
				return 10;
			case DAY:
				return 1000;
		}

		return 1000;
	}

	public void fireViewChangeEvent(CalendarViewChangeEvent e) {
		for(CalendarViewChangeListener listener:viewChangeListener)
		{
			listener.valueChanged(e);
		}
	}

	public String getUniqueAppointmentID(Date date, Appointment appointmentToGetIDFrom) {
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(date);
		Collection<Appointment> appointments = this.getAppointments(date);
		int i = 0;
		for(Appointment appointment:appointments)
		{
			if(appointment == appointmentToGetIDFrom)
			{
				return tempCal.get(Calendar.YEAR) + ":" + tempCal.get(Calendar.DAY_OF_YEAR) + ":" + i;
			}
			i++;
		}

		return null;
	}

	public Appointment getAppointmentFromID(String uniqueID) {
		String[] values = uniqueID.split(":"); // year:day_of_year:appointmentNR

		Calendar tempCal = Calendar.getInstance();
		tempCal.set(Calendar.YEAR, Integer.parseInt(values[0]));
		tempCal.set(Calendar.DAY_OF_YEAR, Integer.parseInt(values[1]));

		Collection<Appointment> appointments = this.getAppointments(new java.sql.Date(tempCal.getTimeInMillis()));
		int i = 0;
		int lookForNr = Integer.parseInt(values[2]);
		for(Appointment appointment:appointments)
		{
			if(i == lookForNr)
			{
				return appointment;
			}
			i++;
		}

		return null;
	}

    public void setMergeWeekends(boolean mergeWeekends) {
        boolean oldVal = this.mergeWeekends;
        this.mergeWeekends = mergeWeekends;

        propertyChangeSupport.firePropertyChange("mergeWeekends", oldVal, this.mergeWeekends);
    }

    public boolean isMergeWeekendsEnabled() {
        return this.mergeWeekends;
    }

	public void addCalendarViewChangeListener(CalendarViewChangeListener listener) {
		viewChangeListener.add(listener);
	}

	public void removeCalendarViewChangeListener(CalendarViewChangeListener listener) {
		viewChangeListener.add(listener);
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void setLocale(Locale locale) {
		Locale oldVal = this.locale;
		this.locale = locale;

		propertyChangeSupport.firePropertyChange("locale", oldVal, locale);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
}
