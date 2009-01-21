package calendar;

import java.beans.PropertyChangeListener;
import java.sql.Date;
import java.util.Collection;
import org.wings.SDelayedEventModel;

/**
 * Interface for Calendar Selection Models  
 * @author Florian Roks
 *
 */
public interface CalendarSelectionModel extends SDelayedEventModel {
	public static final int SINGLE_APPOINTMENT_SELECTION = 0x01;
	public static final int MULTIPLE_APPOINTMENT_SELECTION = 0x02;
	
	/**
	 * This is an ADDITIONAL value, it doesn't work if it's set alone without SINGLE/MULTIPLE selection
	 */
	public static final int DESELECT_DATE_ON_APPOINTMENT_SELECTION = 0x04;
	
	/*
	public static final int WHOLE_APPOINTMENT_SINGLE_SELECTION = 8;
	public static final int WHOLE_APPOINTMENT_MULTIPLE_SELECTION = 16;
	*/
	
	public static final int SINGLE_DATE_SELECTION = 0x00010000;
	public static final int MULTIPLE_DATE_SELECTION = 0x00020000;

	/**
	 * This is an ADDITIONAL value, it doesn't work if it's set alone without SINGLE/MULTIPLE selection
	 */
	public static final int DESELECT_APPOINTMENT_ON_DATE_SELECTION = 0x00040000;
	
	public static final int SINGLE_EXCLUSIVE_DATE_OR_APPOINTMENT_SELECTION = SINGLE_APPOINTMENT_SELECTION | SINGLE_DATE_SELECTION | DESELECT_DATE_ON_APPOINTMENT_SELECTION | DESELECT_APPOINTMENT_ON_DATE_SELECTION;
    public static final int MULTIPLE_EXCLUSIVE_DATE_OR_APPOINTMENT_SELECTION = MULTIPLE_APPOINTMENT_SELECTION | MULTIPLE_DATE_SELECTION | DESELECT_DATE_ON_APPOINTMENT_SELECTION | DESELECT_APPOINTMENT_ON_DATE_SELECTION;

	public static final int DATE_BITMASK = 0xffff0000;
	public static final int APPOINTMENT_BITMASK = 0x0000ffff;
	
	/**
	 * Sets the Selection Mode to be used
	 * Note for SelectionModel developers: This has to fire a "selectionMode" PropertyChange
	 * @param selectionMode
	 */
	void setSelectionMode(int selectionMode);
	
	/**
	 * Returns the currently set Selection Mode 
	 * @return
	 */
	int getSelectionMode();
	
	/**
	 * Returns the Number of selected Appointments
	 * @return
	 */
	int getAppointmentSelectionCount();
	
	/**
	 * Returns the number of selected Dates
	 * @return
	 */
	int getDateSelectionCount();

	/**
	 * Deselects all selected Appointments
	 */
	void clearAppointmentSelection();

	/**
	 * Deselects all selected Dates 
	 */
	void clearDateSelection();
	
	UniqueAppointment getLastSelectedAppointment();
	
	/**
	 * Returns the selected Appointments  
	 * @return The selected Appointments
	 */
	Collection<UniqueAppointment> getSelectedAppointments();
	
	/**
	 * Returns the last selected Date
	 * @return Last selected Date
	 */
	Date getLastSelectedDate();
	
	/**
	 * Returns the selected Dates (NOTE: don't try getSelectedDates().contains(...)!)
	 * @return The selected Dates
	 */
	Collection<Date> getSelectedDates();
	
	/**
	 * Selects a Date according to the SelectionMode
	 * @param date
	 */
	void addSelection(Date date);

	/**
	 * Selects a Appointment on Date date according to the SelectionMode
	 * @param appointment
	 * @param date
	 */
	void addSelection(Appointment appointment, Date date);
	
	/**
	 * Deselects an appointment at a given Date
	 * @param appointment Appointment to be deselected
	 * @param date Date of the appointment to be deselected
	 */
	void removeSelection(Appointment appointment, Date date);
	
	/**
	 * Deselects the given Date 
	 * @param date Date to be deselected
	 */
	void removeSelection(Date date);
	
	/**
	 * Gets Called when a Appointment is clicked
	 * @param appointment Appointment that was clicked on
	 * @param date Date that the appointment was clicked on 
	 * @param keyStatus Status of the Modifier Keys
	 */
	void clickAppointment(Appointment appointment, Date date, ModifierKeyStatus keyStatus);
	
	/**
	 * Geta called when a Date is clicked
	 * @param date Date that the click registered
	 * @param keyStatus Status of the Modifier Keys
	 */
	void clickDate(Date date, ModifierKeyStatus keyStatus);


    /**
     * Gets Called when a Appointment is double clicked
     * @param appointment Appointment that was clicked on
     * @param date Date that the appointment was clicked on
     * @param keyStatus Status of the Modifier Keys
     */
    void doubleClickAppointment(Appointment appointment, Date date, ModifierKeyStatus keyStatus);

    /**
     * Geta called when a Date is double clicked
     * @param date Date that the click registered
     * @param keyStatus Status of the Modifier Keys
     */
    void doubleClickDate(Date date, ModifierKeyStatus keyStatus);


	/**
	 * Returns if a Appointment on Date date is selected
	 * @param appointment Appointment to be checked
	 * @param date Date to be checked
	 * @return true if the Appointment appointment is selected on Date date, false if not
	 */
	boolean isSelected(Appointment appointment, Date date);
	
	/**
	 * Returns if the given Date is currently selected. 
	 * USE THIS instead of getSelectedDates().contains(...), because 
	 * contains would check for the EXACT (millisecond) date, this function checks
	 * only the year and day
	 *   
	 * @param date Date to be checked
	 * @return true if the Date is selected, false if not
	 */
	boolean isSelected(Date date);
	
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
	 * Adds a CalendarSelectionListener to the CalendarSelectionModel
	 * @param listener The Listener to be added to the CalendarSelectionModel
	 */
	void addCalendarSelectionListener(CalendarSelectionListener listener);
	
	/**
	 * Removes a CalendarSelectionListener from the CalendarSelectionModel 
	 * @param listener The Listener to be removed from the CalendarSelectionModel
	 */
	void removeCalendarSelectionListener(CalendarSelectionListener listener);
}
