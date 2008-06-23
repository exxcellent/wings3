package calendar;

import java.util.Collection;
import java.util.Locale;
import java.beans.PropertyChangeListener;
import java.sql.Date;

/**
 * 
 * @author Holger Engels, Florian Roks
 *
 */
public abstract interface CalendarModel {
	/**
	 * Returns the view of this Calendar
	 * @return The CalendarView that is currently set
	 */
	public abstract CalendarView getView();
	
	/**
	 * Sets the view of this Calendar
	 * @param type The CalendarView to be set (CalendarView.MONTH, DAY, WEEK)
	 */
	public abstract void setView(CalendarView type);

	/**
	 * Sets the date of this Calendar
	 * @param date The Date around which the view should be build 
	 */
	public abstract void setDate(Date date);
	
	/**
	 * Returns the Date of this Calendar
	 * @return The Date around which the view should be build 
	 */
	public abstract Date getDate();
	
	/**
	 * Gets all appointments for the given <code>date</code> 
	 * @param date Date for which to retrieve the Appointments
	 * @return Appointments that are active on this day
	 */
	public abstract Collection<Appointment> getAppointments(Date date);

	/**
	 * Sets the <code>Date</code> that this Calendars view starts
	 * @param visibleFrom The <code>Date</code> that this Calendars view (should) start
	 */
	public abstract void setVisibleFrom(Date visibleFrom);
	
	/**
	 * Sets the <code>Date</code> that this Calendars view (should) stop (note: the default CalendarCG ignores this and only uses getRow/ColumnCount)
	 * @param visibleUntil The <code>Date</code> that this Calendars view (should) stop (note: the default CalendarCG ignores this and only uses getRow/ColumnCount)
	 */
	public abstract void setVisibleUntil(Date visibleUntil);
	
	/**
	 * Returns the <code>Date</code> that this Calendars view starts 
	 * @return Start <code>Date</code> of the current view 
	 */
	public abstract Date getVisibleFrom();
	
	/**
	 * Returns the <code>Date</code> that this Calendars view ends
	 * @return End <code>Date</code> of the current view
	 */
	public abstract Date getVisibleUntil();

    /**
     * Returns if date is visible in the current view
     * @param date
     * @return
     */
    public abstract boolean isVisible(Date date);

    /**
	 * Should return the row count of the selected calendar view
	 * @return
	 */
	public abstract int getRowCount();
	
	/**
	 * Should return the column count of the selected calendar view - must be less or equal to 7 
	 * @return Integer with the column count (<= 7!)
	 */
	public abstract int getColumnCount();
	
	/**
	 * Returns a custom cell renderer for rendering the cell-content (null if you want to use the default renderer)
	 * @return null, if you want to use the default renderer, a CustomCellRenderer if you want to use a customized one
	 */
	public abstract CustomCellRenderer getCustomCellRenderer();
	
	/**
	 * Returns the maximum Number of Appointments to be shown in a Cell 
	 * @return
	 */
	public abstract int getMaxNumberAppointmentsPerCell(boolean isMerged);

	/**
	 * Adds a CalendarViewChangeListener
	 * @param listener Listener to be added
	 */
	public void addCalendarViewChangeListener(CalendarViewChangeListener listener);

	/**
	 * Removes a CalendarViewChangeListener
	 * @param listener Listener to be removed
	 */
	public void removeCalendarViewChangeListener(CalendarViewChangeListener listener);
	
	/**
	 * Sets the Locale in this Model 
	 * @param locale
	 */
	public void setLocale(Locale locale);
	
	/**
	 * Gets the Locale from this Model
	 * @return
	 */
	public Locale getLocale();
	
	/**
	 * Adds a Property Change Listener to the CalendarSelectionModel
	 * @param listener The Listener to be added to the CalendarSelectionModel 
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Removes a Property Change Listener from the CalendarSelectionModel  
	 * @param listener The Listener to be removed from the CalendarSelectionModel
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * A Enumeration representing the current View at the Calendar (NONE, MONTH, WEEK, DAY)    
	 * @author Florian Roks
	 *
	 */
	public enum CalendarView
	{
		NONE,
		MONTH,
		WEEK,
		DAY
	}
	
	/**
	 * Must return a unique String for the appointment on date, to be used as a identifier 
	 * looks in DefaultCalendarModel like: "YEAR:DAY_OF_YEAR:appointment_number_on_this_day"
	 * NOTE: It isn't allowed to contain the semicolon ';', as it is used as seperator between values
     * must only be unique withing the current instance of calendar
	 * @param date
	 * @param appointment
	 * @return
	 */
	public String getUniqueAppointmentID(Date date, Appointment appointment);
	
	/**
	 * Must return a Appointment for a Unique ID created by getUniqueAppointmentID(...) - must only be unique within
     * the current instance of calendar
	 * @param uniqueID
	 * @return
	 */
	public Appointment getAppointmentFromID(String uniqueID);

    /**
     * Returns true if weekends (Sa/Su) should be merged into one cell
     * @return
     */
    public boolean isMergeWeekendsEnabled();
}
