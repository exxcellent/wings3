package calendar;

/*import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;*/
import org.wings.LowLevelEventListener;
import org.wings.SComponent;

import calendar.CalendarModel.CalendarView;
import calendar.plaf.CalendarCG;

import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.wings.plaf.Update;
import org.wings.*;

/**
 * Calendar Component - always starts in the MONTH view
 * @author Holger Engels, Florian Roks
 *
 */
public class AppointmentCalendar extends SComponent implements LowLevelEventListener {
	private static final long serialVersionUID = 2537490018780756796L;
	//private static final Log LOG = LogFactory.getLog(CalendarExample.class);
    
	private CalendarModel model;
    private CalendarSelectionModel selectionModel;
    
    private ModifierKeyStatus modifierKeyStatus = new ModifierKeyStatus();
    
    /**
     * Forwards Selection Events to the CG
     */
    private CalendarSelectionListener fwdSelectionEvents = new CalendarSelectionListener() {
		@Override
		public void valueChanged(CalendarSelectionEvent e) {
			if(isUpdatePossible() && AppointmentCalendar.class.isAssignableFrom(AppointmentCalendar.this.getClass()))
			{
				Update update = ((CalendarCG)getCG()).getSelectionUpdate(AppointmentCalendar.this, selectionModel, e);
				update(update);
			}
			else
			{
				reload();
			}
		}
    };
    
    private PropertyChangeListener fwdSelectionModeChange = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// reload the page to remove/add the onClick event handlers to the calendar
			// and/or change the selections (i.e. if the user switches from multiple appointments to single)
			if(evt.getPropertyName() == "selectionMode") {
				reload();
			}
		}
    
    };
    
    private CalendarViewChangeListener fwdCalendarViewEvents = new CalendarViewChangeListener() {
		@Override
		public void valueChanged(CalendarViewChangeEvent e) {
			switch(e.getType())
			{
				case DATE:
					((CalendarCG)getCG()).setToday(e.getDate());
					reload();
				break;
				case APPOINTMENT:
					reload();
				break;
				case VIEW:
					reload();
				break;
			}
		}
    	
    	
    };
    
    private PropertyChangeListener fwdLocaleChange = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// Reload the component if the locale changes
			if(evt.getPropertyName() == "locale") {
				reload();
			}
		}
    	
    };
    
    /**
     * Constructs the Calendar component using the current (Server) Time/Date for the current Day/Month/Year and the Servers locale with the DefaultCalendarModel
     */
	public AppointmentCalendar() {
		this(Locale.getDefault());
	}
	
	/**
	 * Constructs the Calendar component using the current (Server) Time/Date for the current Day/Month/Year with the DefaultCalendarModel 
	 * @param locale Locale 
	 * @param model CalendarModel to use
	 */
	public AppointmentCalendar(Locale locale, CalendarModel model) {
		this(new Date(Calendar.getInstance().getTimeInMillis()), locale, model);
	}
	
    /**
     * Constructs the Calendar component using the current (Server) Time/Date for the current Day/Month/Year and given Locale with the DefaultCalendarModel
     */
	public AppointmentCalendar(Locale locale) {
		this(Calendar.getInstance().getTime(), locale);
	}
	
	
	/**
	 * Constructs the Calendar component using the given date and locale with the DefaultCalendarModel
	 * @param date Date to build the calendar around
	 * @param locale Locale for the calendar instance
	 */
	public AppointmentCalendar(java.util.Date date, Locale locale)
	{
		this(new Date(date.getTime()), locale);
	}

	/**
	 * Constructs the Calendar component using the given <code>java.sql.Date</code>
	 * @param date <code>Date</code> to be used for the Calendar
	 * @param locale Locale to be used for the calendar instance 
	 */
	public AppointmentCalendar(Date date, Locale locale)
	{
		this(date, locale, null);
		
	}
	
	/**
	 * Constructs the Calendar component using the given parameters
	 * @param date
	 * @param locale
	 * @param model
	 */
	public AppointmentCalendar(Date date, Locale locale, CalendarModel model)
	{
		super();
		
		this.setName("AppointmentCalendar");
		
		this.getSession().getDispatcher().register(this);

		/// TODO: do this in the default.properties
		CalendarCG calCG = new CalendarCG();
		this.setCG(calCG);
		
		if(model == null)
			setCalendarModel(new DefaultCalendarModel());
		else
			setCalendarModel(model);
		
		this.model.setView(CalendarView.MONTH);

		setLocale(locale);
		setDate(date);

		setSelectionModel(new DefaultCalendarSelectionModel());
		
		//getSelectionModel().setSelectionMode(CalendarSelectionModel.SINGLE_EXCLUSIVE_DATE_OR_APPOINTMENT_SELECTION);
		//getSelectionModel().setSelectionMode(CalendarSelectionModel.MULTIPLE_APPOINTMENT_SELECTION | CalendarSelectionModel.MULTIPLE_DATE_SELECTION);		
	}
	
	/**
	 * Sets the CalendarModel
	 * @param model
	 */
	public void setCalendarModel(CalendarModel model)
	{
		if(model == null)
			throw new IllegalArgumentException("model must be not null");
		
		CalendarModel oldVal = this.model;
		
		if(oldVal == null && model != null) {
			this.model = model;
			this.model.addPropertyChangeListener(fwdLocaleChange);
			this.model.addCalendarViewChangeListener(fwdCalendarViewEvents);
		} else if(oldVal != null && model != null) {
			this.model = model;
			oldVal.removePropertyChangeListener(fwdLocaleChange);
			oldVal.removeCalendarViewChangeListener(fwdCalendarViewEvents);
			this.model.addPropertyChangeListener(fwdLocaleChange);
			this.model.addCalendarViewChangeListener(fwdCalendarViewEvents);
		}
		
		propertyChangeSupport.firePropertyChange("model", oldVal, model);
	}
	
	/**
	 * Sets the Date this calendar is build around 
	 * @param date Sets the date this calendar is build around
	 */
	public void setDate(java.util.Date date) {
		// looks bad, but gets the job done
		setDate(new Date(date.getTime()));
	}

	/**
	 * Sets the Date this calendar is build around 
	 * @param date Sets the date this calendar is build around
	 */
	public void setDate(Date date) {
		getCalendarModel().setDate(date);
	}

	/**
	 * Gets the Date this Calendar is build around
	 * @return Returns the Date this Calendar is build around
	 */
	public Date getDate() {
		return getCalendarModel().getDate();
	}

	/**
	 * Returns the currently used <code>CalendarModel</code> for this instance of Calendar
	 * @return The currently used <code>CalendarModel</code>
	 */
	public CalendarModel getCalendarModel() {
		return model;
	}

/*	
	public void generateAndSetTestAppointments()
	{
		Collection<Appointment> appointments = new ArrayList<Appointment>();
        Random random = new Random();
        java.util.Calendar from = java.util.Calendar.getInstance();
        from.add(java.util.Calendar.MONTH, -1);
        for (int i = 0; i < 30; i++) {
            java.util.Calendar until = (java.util.Calendar)from.clone();
            until.add(java.util.Calendar.DAY_OF_YEAR, random.nextInt(10));

            Event event = new Event(new Date(from.getTimeInMillis()), new Date(until.getTimeInMillis()));
            int hour = 10 + random.nextInt(14);
            java.sql.Time fromTime = new java.sql.Time(hour, 0, 0);
            java.sql.Time untilTime = new java.sql.Time(hour + 2, 0, 0);
            event.setFromTime(fromTime);
            event.setUntilTime(untilTime);
            event.setName(((random.nextInt(2) == 0)?"Meeting ":"Betriebsfeier ")); // + i); 
            events.add(event);
            from.add(java.util.Calendar.DAY_OF_YEAR, random.nextInt(5));
        }
        
        setAppointments(appointments);
	}
	
	*/
	
	/**
	 * Sets the Locale of the Calendar
	 * @param locale Locale to be set
	 */
	public void setLocale(Locale locale)
	{
		getCalendarModel().setLocale(locale);
	}
	
	/**
	 * Returns the locale of the Calendar
	 * @return
	 */
	public Locale getLocale()
	{
		return getCalendarModel().getLocale();
	}

	/**
	 * Returns the SelectionModel of this calendar
	 * @return SelectionModel of this calendar
	 */
	public CalendarSelectionModel getSelectionModel()
	{
		return this.selectionModel;
	}
	
	/**
	 * Sets the SelectionMOdel of this calendar
	 * @param selectionModel
	 */
	public void setSelectionModel(CalendarSelectionModel selectionModel)
	{
		if(selectionModel == null) {
			throw new IllegalArgumentException("selectionModel must be non null");
		}
		
		CalendarSelectionModel oldVal = this.selectionModel;
		this.selectionModel = selectionModel;
		
		if(oldVal == null && selectionModel != null) {
			selectionModel.addCalendarSelectionListener(fwdSelectionEvents);
			selectionModel.addPropertyChangeListener(this.fwdSelectionModeChange);
		}
		
		if(oldVal != null && oldVal != selectionModel) {
			oldVal.removePropertyChangeListener(this.fwdSelectionModeChange);
			oldVal.removeCalendarSelectionListener(fwdSelectionEvents);
			selectionModel.addCalendarSelectionListener(fwdSelectionEvents);
			selectionModel.addPropertyChangeListener(this.fwdSelectionModeChange);
		}
		
		propertyChangeSupport.firePropertyChange("selectionModel", oldVal, this.selectionModel);
	}

	/**
	 * Adds a CalendarViewChangeListener
	 * @param listener Listener to be added
	 */
	public void addCalendarViewChangeListener(CalendarViewChangeListener listener)
	{
		getCalendarModel().addCalendarViewChangeListener(listener);
	}
	
	/**
	 * Removes a CalendarViewChangeListener
	 * @param listener Listener to be removed
	 */
	public void removeCalendarViewChangeListener(CalendarViewChangeListener listener)
	{
		getCalendarModel().removeCalendarViewChangeListener(listener);
	}
	
	
	/**
	 * Adds a selection Change Listener to the Component
	 * @param listener The SelectionChangeListener to be added
	 */
	public void addCalendarSelectionListener(CalendarSelectionListener listener)
	{
		getSelectionModel().addCalendarSelectionListener(listener);
	}
	
	/**
	 * Remove a selection Change Listener from the Component 
	 * @param listener The SelectionChangeListener to be removed
	 */
	public void removeCalendarSelectionChangeListener(CalendarSelectionListener listener)
	{
		getSelectionModel().removeCalendarSelectionListener(listener);
	}

	@Override
	public void processLowLevelEvent(String componentName, String[] values)
	{
		String[] processedValues; 
		
		// TODO: find out why this doesn't work without splitting!
		if(values[0].contains(";"))
			processedValues = values[0].split(";");
		else
			processedValues = values;
			
		SForm.addArmedComponent(this);

		for(int i=0; i<processedValues.length; i++)
		{
			String value = processedValues[i];
			if(value.length() < 2)
				continue;

			getSelectionModel().setDelayEvents(true);
			
			if(value.startsWith("q:")) {
				// q: means update the popup window for more information
				Update update = ((CalendarCG)this.getCG()).getPopupUpdate(this, value.substring(2));
				if(update != null && isUpdatePossible())
					update(update);
			} else if(value.startsWith("ctrlKey")) {
				modifierKeyStatus.ctrlKey = Boolean.parseBoolean(value.split("=")[1]);
			} else if(value.startsWith("shiftKey")) {
				modifierKeyStatus.shiftKey = Boolean.parseBoolean(value.split("=")[1]);
			} else if(value.startsWith("altKey")) {
				modifierKeyStatus.altKey = Boolean.parseBoolean(value.split("=")[1]);
			} else if(value.startsWith("d:")) {
				Calendar cal = Calendar.getInstance();
				String[] data = value.split(":");
				cal.set(Calendar.YEAR, Integer.parseInt(data[1]));
				cal.set(Calendar.DAY_OF_YEAR, Integer.parseInt(data[2]));

				this.getSelectionModel().clickDate(new java.sql.Date(cal.getTimeInMillis()), modifierKeyStatus);
			} else if(value.startsWith("a:")) {
				Calendar cal = Calendar.getInstance();
				String[] data = value.split(":");
				cal.set(Calendar.YEAR, Integer.parseInt(data[1]));
				cal.set(Calendar.DAY_OF_YEAR, Integer.parseInt(data[2]));
				IAppointment appointment = getCalendarModel().getAppointmentFromID(value.substring(2));

				this.getSelectionModel().clickAppointment(appointment, new Date(cal.getTimeInMillis()), modifierKeyStatus);
			}
		}

		modifierKeyStatus.reset();
	}

	public void fireFinalEvents() {
		super.fireFinalEvents();
	}
	
	@Override
	public void fireIntermediateEvents() {
		SForm.addArmedComponent(this);
		
		// all state changes are done, fire the events to get updates
		getSelectionModel().fireDelayedFinalEvents();
		getSelectionModel().setDelayEvents(false);
	}

	@Override
	public boolean isEpochCheckEnabled() {
		return false;
	}
}
