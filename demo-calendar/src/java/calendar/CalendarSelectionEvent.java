package calendar;

import java.sql.Date;
import java.util.EventObject;

/**
 * Calendar Selection Event - fires when a Selection Change was initiated
 * @author Florian Roks
 *
 */
public class CalendarSelectionEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2334127568641137646L;

	private SelectionType selectionType;
	private SelectionComponent selectionComponent;
	private IAppointment selectedAppointment;
	private Date selectedDate;
	
	/**
	 * Constructs a CalendarSelectionEvent with the given arguments
	 * @param source
	 * @param type
	 * @param selectedAppointment
	 * @param date
	 */
	public CalendarSelectionEvent(Object source, SelectionType type, IAppointment selectedAppointment, Date date)
	{
		super(source);
	
		this.selectionType = type;
		this.selectionComponent = SelectionComponent.APPOINTMENT;
		this.selectedAppointment = selectedAppointment;
		this.selectedDate = date;
	}

	/**
	 * Constructs a CalendarSelectionEvent with the given arguments
	 * @param source
	 * @param type
	 * @param date
	 */
	public CalendarSelectionEvent(Object source, SelectionType type, Date date)
	{
		super(source);
		
		this.selectionType = type;
		this.selectionComponent = SelectionComponent.DATE;
		this.selectedDate = date;
	}
	
	
	/**
	 * Returns if either a DATE, or a APPOINTMENT is affected by this event 
	 * @return
	 */
	public SelectionComponent getAffectedComponent()
	{
		return this.selectionComponent;
	}
	
	/**
	 * Returns if a selection was ADDED or REMOVED 
	 * @return
	 */
	public SelectionType getType()
	{
		return selectionType;
	}
	
	/**
	 * Returns the selected/deselected Appointment (or null if this event's a date)
	 * @return
	 */
	public IAppointment getAppointment()
	{
		return selectedAppointment;
	}
	
	/**
	 * Returns the selected/deselected Date or Date of an Appointment 
	 * @return
	 */
	public Date getDate()
	{
		return selectedDate;
	}

    @Override
    public String toString()
    {
        return this.getType() + " " + this.getAffectedComponent() + " " + this.getDate() + " " + this.getAppointment();
    }
    
    /**
	 * SelectionType 
	 * @author Florian Roks
	 *
	 */
	public enum SelectionType {
		ADDED,
		REMOVED
	}
	
	/**
	 * SelectionComponent
	 * @author Florian roks
	 *
	 */
	public enum SelectionComponent {
		DATE,
		APPOINTMENT
	}
}
