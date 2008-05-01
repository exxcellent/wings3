package calendar;

import java.sql.Date;
import java.util.Collection;
import java.util.EventObject;

import calendar.CalendarModel.CalendarView;

/**
 * Event to be fired, when a View Change happens - A View Change is either a change
 * in the "view" (i.e. if the calendar shows a MONTH and is set to show a WEEK-View),
 * when the appointments of a view changes or if the Date around the view is built 
 * changes (and therefore the view itself) 
 * @author Florian Roks
 *
 */
public class CalendarViewChangeEvent extends EventObject {
	private static final long serialVersionUID = -6315890972303735738L;
	private Collection<IAppointment> appointments;
	private Date date;
	private ChangeType type;
	private CalendarView view;
	
	/**
	 * Constructs a ViewChangeEvent with the given arguments
	 * @param source Source of the Event
	 * @param appointments New Appointments
	 */
	public CalendarViewChangeEvent(Object source, Collection<IAppointment> appointments)
	{
		super(source);
		
		this.type = ChangeType.APPOINTMENT;
		this.appointments = appointments;
	}
	
	/**
	 * Constructs a ViewChangeEvent with the given arguments
	 * @param source Source of the Event
	 * @param date New Date of the View 
	 */
	public CalendarViewChangeEvent(Object source, Date date)
	{
		super(source);
		
		this.type = ChangeType.DATE;
		this.date = date;
	}
	
	/**
	 * Constructs a ViewChangeEvent with the given arguments
	 * @param source Source of the Event
	 * @param type Type of the Event (APPOINTMENT or DATE)
	 * @param appointments New Appointments / null if date
	 * @param date New Date / null if appointment
	 */
	public CalendarViewChangeEvent(Object source, ChangeType type, Collection<IAppointment> appointments, Date date)
	{
		super(source);
		
		this.type = type;
		this.appointments = appointments;
		this.date = date;
	}
	
	public CalendarViewChangeEvent(Object source, CalendarView view)
	{
		super(source);
		
		this.type = ChangeType.VIEW;
		this.view = view;
	}
	
	/**
	 * Returns the Type of this Event (DATE/APPOINTMENT)
	 * @return Type of this event
	 */
	public ChangeType getType()
	{
		return this.type;
	}
	
	/**
	 * Returns the new Date of the View 
	 * @return
	 */
	public Date getDate()
	{
		return this.date;
	}
	
	/**
	 * Returns the View of the Event (if getType() is VIEW)
	 * @return
	 */
	public CalendarView getView()
	{
		return this.view;
	}
	
	/**
	 * Returns the new Appointments of the view
	 * @return
	 */
	public Collection<IAppointment> getAppointments()
	{
		return this.appointments;
	}
	
	/**
	 * Enumeration representype the type of Change for a View 
	 * @author Florian Roks
	 *
	 */
	public enum ChangeType {
		APPOINTMENT,
		DATE,
		VIEW
	}
}
