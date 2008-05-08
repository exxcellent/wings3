package calendar;

import java.sql.Date;
import java.util.EnumSet;
import java.util.Locale;
import java.awt.Color;
 
public interface IAppointment {
	/**
	 * Returns the Start Date of an Appointment
	 * @return The Appointments Start Date/Time
	 */
	public abstract Date getAppointmentStartDate();
	
	/**
	 * Returns the End Date of an Appointment
	 * @return The Appointments End Date/Time
	 */
	public abstract Date getAppointmentEndDate();
	
	/**
	 * Returns true, if the Appointment is recurring, false if not
	 * @return Boolean if the appointment is recurring or not
	 */
	public abstract boolean isAppointmentRecurring();
	
	/**
	 * Returns the type of the Appointment (see AppointmentType)
	 * @return Returns the Type of the Appointment 
	 * @see AppointmentType
	 */
	public abstract AppointmentType getAppointmentType();
	
	/**
	 * Returns a EnumSet containing the Weekdays, this Appointment is recurring 
	 * @return A EnumSet of Weekdays, representing when this Appointment has recurring days
	 */
	public abstract EnumSet<Weekday> getAppointmentRecurringDays();
	
	/**
	 * Returns the name of the appointment 
	 * @return String that represents the name of this appointment
	 */
	public abstract String getAppointmentName();
	
	/**
	 * Returns the description of the appointment
	 * @return String that represents the description of this appointment
	 */
	public abstract String getAppointmentDescription();
	
	/**
	 * Returns additional information about the appointment
	 * @return String that represents additional information about this appointment
	 */
	public abstract String getAdditionalAppointmentInformation();
	
	/**
	 * Returns the foreground color of the appointment
	 * @return Foreground color of the appointment
	 */
	public abstract Color getForegroundColor();
	
	/**
	 * Returns the background color of the appointment
	 * @return Background color of the appointment
	 */
	public abstract Color getBackgroundColor();
	
	/**
	 * Gets a String that represents the Weekdays this Appointment recurrs
	 * @param weekdays EnumSet of Weekdays that to build String
	 * @param locale Locale to use when building the string
	 * @return String representing the given Weekday Enum in the given Locale 
	 */
	public abstract String getAppointmentRecurringDaysString(Locale locale);

	/**
	 * Gets a localized String of the AppointmentType
	 * @param type Type of the Appointment
	 * @param locale Locale to use to build the String (supports US, UK, CANADA, GERMAN)
	 * @return Comma-Seperated Localized String with the Weekdays in human-readable form 
	 */
	public abstract String getAppointmentTypeString(AppointmentType type, Locale locale);

	/**
	 * Gets a localized String of the Start/End Date of a Appointment
	 * @return
	 */
	public abstract String getAppointmentStartEndDateString(Locale locale);
	
	/**
	 * Gets a localized String of the Start/end Time of a Appointment
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public abstract String getAppointmentStartEndTimeString(Locale locale);
	
	/**
	 * A Enumeration representing the Type of an Appointment (either NORMAL or ALLDAY) 
	 * @author Florian Roks
	 *
	 */
	public enum AppointmentType {
		NORMAL,
		ALLDAY
	}
	
	/**
	 * A Enumeration representing a Weekday in a week (MONDAY, TUESDAY, ...) 
	 * @author Florian Roks
	 *
	 */
	public enum Weekday
	{
		MONDAY,
		TUESDAY,
		WEDNESDAY,
		THURSDAY,
		FRIDAY,
		SATURDAY,
		SUNDAY
	}
}
