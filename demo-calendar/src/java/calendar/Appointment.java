package calendar;

import java.sql.Date;
import java.awt.Color;
import java.util.EnumSet;
import java.util.Calendar;
import java.util.Locale;
import java.text.DateFormat;

/**
 * Class that represents an Appointment in the Calendar
 * @author Holger Engels, Florian Roks
 *
 */
public class Appointment implements IAppointment {
	private String name;
	private String description;
	private Date startDate, endDate;
	private Color backgroundColor;
	private Color foregroundColor;
	private boolean isRecurring;
	private EnumSet<Weekday> recurringWeekdays;
	private AppointmentType appointmentType;
	private String additional;
	
	/**
	 * Creates a new normal or allday Event with the default Colors
	 * @param name
	 * @param description
	 * @param appointmentType
	 * @param startDate
	 * @param endDate
	 */
	public Appointment(String name, String description, AppointmentType appointmentType, Date startDate, Date endDate)
	{
		this(name, description, appointmentType, startDate, endDate, false, null, Color.BLACK, Color.RED);
	}

	/**
	 * Creates a normal or allday event with fore- and background Colors 
	 * @param name Name of the appointment
	 * @param description Description of the appointment
	 * @param appointmentType Type of the appointment
	 * @param startDate Start Date/Time of the appointment
	 * @param endDate End Date/Time of the appointment
	 * @param foregroundColor Foreground Color of an appointment 
	 * @param backgroundColor Background Color of an appointment
	 */
	public Appointment(String name, String description, IAppointment.AppointmentType appointmentType, Date startDate, Date endDate, Color foregroundColor, Color backgroundColor)
	{
		this(name, description, appointmentType, startDate, endDate, false, null, foregroundColor, backgroundColor);
	}

	/**
	 * Creates a normal or allday event with recurring weekdays with the default Colors
	 * @param name Name of the appointment
	 * @param description Description of the appointment
	 * @param appointmentType Type of the appointment
	 * @param startDate Start Date/Time of the appointment
	 * @param endDate End Date/Time of the appointment
	 * @param recurringWeekdays Reccuring Weekdays of the appointment
	 */
	public Appointment(String name, String description, IAppointment.AppointmentType appointmentType, Date startDate, Date endDate, EnumSet<Weekday> recurringWeekdays)
	{
		this(name, description, appointmentType, startDate, endDate, true, recurringWeekdays, Color.BLACK, Color.RED);
	}
	
	/**
	 * Creates a normal or allday event with recurring weekdays with fore- and background Colors
	 * @param name Name of the appointment
	 * @param description Description of the appointment
	 * @param appointmentType Type of the appointment
	 * @param startDate Start Date/Time of the appointment
	 * @param endDate End Date/Time of the appointment
	 * @param recurringWeekdays Recurring Weekdays of the appointment
	 * @param foregroundColor Foreground Color of the appointment
	 * @param backgroundColor Background COlor of the appointment
	 */
	public Appointment(String name, String description, IAppointment.AppointmentType appointmentType, Date startDate, Date endDate, EnumSet<Weekday> recurringWeekdays, Color foregroundColor, Color backgroundColor)
	{
		this(name, description, appointmentType, startDate, endDate, true, recurringWeekdays, foregroundColor, backgroundColor);
	}
	
	public Appointment()
	{
		this.setAppointmentType(AppointmentType.NORMAL);
	}
	
	private Appointment(String name, String description, IAppointment.AppointmentType appointmentType, Date startDate, Date endDate, boolean isRecurring, EnumSet<Weekday> recurringWeekdays, Color foregroundColor, Color backgroundColor)
	{
			this.name = name;
			this.description = description;
			this.appointmentType = appointmentType;
			this.startDate = startDate;
			this.endDate = endDate;
			this.isRecurring = isRecurring;
			if(this.isRecurring)
				this.recurringWeekdays = recurringWeekdays;
			else 
				this.recurringWeekdays = null;
			this.foregroundColor = foregroundColor;
			this.backgroundColor = backgroundColor;
	}
	
	public void setAdditionalAppointmentInformation(String additional) {
		this.additional = additional;
	}
	
	@Override
	public String getAdditionalAppointmentInformation() {
		return additional;
	}
	
	public void setAppointmentDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String getAppointmentDescription() {
		return description;
	}
	
	public void setAppointmentStartDate(Date date) {
		this.startDate = date;
	}
	
	@Override
	public Date getAppointmentStartDate() {
		return startDate;
	}
	
	public void setAppointmentEndDate(Date date) {
		this.endDate = date;
	}
	
	@Override
	public Date getAppointmentEndDate() {
		return endDate;
	}
	
	public void setAppointmentName(String name) {
		this.name = name;
	}
	
	@Override
	public String getAppointmentName() {
		return name;
	}
	
	public void setAppointmentRecurringDays(EnumSet<Weekday> recurringWeekdays) {
		this.recurringWeekdays = recurringWeekdays;
	}
	
	@Override
	public EnumSet<Weekday> getAppointmentRecurringDays() {
		return recurringWeekdays;
	}
	
	public void setAppointmentType(AppointmentType appointmentType) {
		this.appointmentType = appointmentType;
	}
	
	@Override
	public AppointmentType getAppointmentType() {
		return appointmentType;
	}
	
	@Override
	public Color getForegroundColor() {
		return foregroundColor;
	}

	/**
	 * Sets the foreground Color of an Appointment
	 * @param foregroundColor the foreground Color (to be set) of an appointment
	 */
	public void setForegroundColor(Color foregroundColor)
	{
		this.foregroundColor = foregroundColor;
	}

	@Override
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	/**
	 * Sets the background Color of an Appointment
	 * @param backgroundColor The background Color (to be seT) of an appointment
	 */
	public void setBackgroundColor(Color backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}
	
	
	public void setAppointmentRecurring(boolean isRecurring) {
		this.isRecurring = isRecurring;
	}
	
	@Override
	public boolean isAppointmentRecurring() {
		return isRecurring;
	}
	
	public String toString()
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		return name + " " + appointmentType + " " + isRecurring;
	}

	/**
	 * Gets a localized String of the AppointmentType
	 * @param type Type of the Appointment
	 * @param locale Locale to use to build the String (supports US, UK, CANADA, GERMAN)
	 * @return Comma-Seperated Localized String with the Weekdays in human-readable form 
	 */
	@Override
	public String getAppointmentTypeString(AppointmentType type, Locale locale)
	{
		return Appointment.StaticGetAppointmentTypeString(type, locale);
	}
	
	public static String StaticGetAppointmentTypeString(AppointmentType type, Locale locale)
	{
		if(locale == Locale.US || locale == Locale.UK || locale == Locale.CANADA)
		{
			switch(type)
			{
				case ALLDAY:
					return "Allday";
				case NORMAL:
					return "Normal";
			}
		}
		
		switch(type)
		{
			case ALLDAY:
				return "Ganztags";
			case NORMAL:
				return "Normal";
		}
		
		return null;
	}
	
	/**
	 * Gets a String that represents the Weekdays this Appointment recurrs
	 * @param weekdays EnumSet of Weekdays that to build String
	 * @param locale Locale to use when building the string
	 * @return String representing the given Weekday Enum in the given Locale 
	 */
	@Override
	public String getAppointmentRecurringDaysString(Locale locale)
	{
		return Appointment.StaticGetAppointmentRecurringDaysString(this, locale);
	}
	
	public static String StaticGetAppointmentRecurringDaysString(Appointment appointment, Locale locale)
	{
		Calendar tempCal = Calendar.getInstance(locale);
		EnumSet<Weekday> weekdays = appointment.getAppointmentRecurringDays();

		String recurringDays = "";
		
		if(weekdays == null)
			return recurringDays;
		
		int i = 0;
		for(Weekday weekday:weekdays)
		{
			tempCal.set(Calendar.DAY_OF_WEEK, getCalendarWeekdayFromWeekday(weekday));
			recurringDays += ((i>0)?", ":"") + tempCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, locale);
			
			i++;
		}
		
		if(recurringDays.length() > 0)
			return recurringDays;
		else
			return null;
	}

	
	/**
	 * Gets the Calendar.WEEKDAY integer that a Weekday enum represents
	 * @param weekday The Weekday to get the Calendar integer from
	 * @return Integer that represents the Weekday that is given in <code>weekday</code>
	 */
	public static int getCalendarWeekdayFromWeekday(Weekday weekday)
	{
		if(IAppointment.Weekday.SUNDAY == weekday)
			return Calendar.SUNDAY;
		if(IAppointment.Weekday.MONDAY == weekday)
			return Calendar.MONDAY;
		if(IAppointment.Weekday.TUESDAY == weekday)
			return Calendar.TUESDAY;
		if(IAppointment.Weekday.WEDNESDAY == weekday)
			return Calendar.WEDNESDAY; 
		if(IAppointment.Weekday.THURSDAY == weekday)
			return Calendar.THURSDAY;
		if(IAppointment.Weekday.FRIDAY == weekday)
			return Calendar.FRIDAY;
		if(IAppointment.Weekday.SATURDAY == weekday)
			return Calendar.SATURDAY;
		
		return -1;
	}
	
	/**
	 * Gets a Weekday Enum from a Date
	 * @param date Date to be extracted the Weekday from 
	 * @return Weekday Enum representing the Weekday given with date
	 */
	public static IAppointment.Weekday getWeekdayFromDate(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		switch(cal.get(Calendar.DAY_OF_WEEK))
		{
			case Calendar.SUNDAY: 
				return IAppointment.Weekday.SUNDAY;
			case Calendar.MONDAY: 
				return IAppointment.Weekday.MONDAY;
			case Calendar.TUESDAY: 
				return IAppointment.Weekday.TUESDAY;
			case Calendar.WEDNESDAY: 
				return IAppointment.Weekday.WEDNESDAY;
			case Calendar.THURSDAY: 
				return IAppointment.Weekday.THURSDAY;
			case Calendar.FRIDAY: 
				return IAppointment.Weekday.FRIDAY;
			case Calendar.SATURDAY: 
				return IAppointment.Weekday.SATURDAY;
		}
		

		// to satisfy the compiler
		return IAppointment.Weekday.SUNDAY;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Appointment)
		{
			Appointment cmp = (Appointment)obj;
			if(	cmp.getAppointmentName() == this.getAppointmentName() &&
				cmp.getAppointmentDescription() == this.getAppointmentDescription() &&
				cmp.isAppointmentRecurring() == this.isAppointmentRecurring() &&
				cmp.getAppointmentRecurringDays() == this.getAppointmentRecurringDays() &&
				cmp.getAppointmentStartDate().getTime() == this.getAppointmentStartDate().getTime() &&
				cmp.getAppointmentEndDate().getTime() == this.getAppointmentEndDate().getTime() &&
				cmp.getAdditionalAppointmentInformation() == this.getAdditionalAppointmentInformation() &&
				cmp.getBackgroundColor() == this.getBackgroundColor() &&
				cmp.getForegroundColor() == this.getForegroundColor())
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String getAppointmentStartEndDateString(Locale locale) {
		return Appointment.StaticGetAppointmentStartEndDateString(this, locale);
	}


    private static Calendar cal1 = Calendar.getInstance();
    private static Calendar cal2 = Calendar.getInstance();

    private static String StaticGetAppointmentStartEndDateString(IAppointment appointment, Locale locale) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);

        cal1.setTime(appointment.getAppointmentStartDate());
        cal2.setTime(appointment.getAppointmentEndDate());

        if(cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
            return dateFormat.format(appointment.getAppointmentStartDate());
        else        
            return dateFormat.format(appointment.getAppointmentStartDate()) + " - " + dateFormat.format(appointment.getAppointmentEndDate());
    }

    @Override
	public String getAppointmentStartEndTimeString(Locale locale) {
        return Appointment.StaticGetAppointmentStartEndTimeString(this, locale);
    }

    private static String StaticGetAppointmentStartEndTimeString(IAppointment appointment, Locale locale)
    {
        DateFormat formatTime = DateFormat.getTimeInstance(DateFormat.SHORT, locale);

        return formatTime.format(appointment.getAppointmentStartDate()) + "-" + formatTime.format(appointment.getAppointmentEndDate());
    }
}
