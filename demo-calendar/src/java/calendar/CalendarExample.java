package calendar;
// TODO: Bug: Browserfenster viel viel zu klein => ajax-hammering
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.SBorderFactory;
import org.wings.SBoxLayout;
import org.wings.SButton;
import org.wings.SComboBox;
import org.wings.SConstants;
import org.wings.SDimension;
import org.wings.SBorderLayout;
import org.wings.SFrame;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.SURLIcon;
import org.wings.SFont;

import calendar.CalendarModel.CalendarView;
import calendar.RemoteCalendar.RemoteCalendar;

/**
 * Simple Example to show the Calendar Component
 * @author Florian Roks
 *
 */
public class CalendarExample {
	private static final Log LOG = LogFactory.getLog(CalendarExample.class);
	final AppointmentCalendar calendar = new AppointmentCalendar(Locale.GERMAN);
	private final String iCalURL = null;
	
	/**
	 * Constructs the Calendar Example
	 */
	public CalendarExample()
	{
		LOG.info("Calendar Example startup");

		if(iCalURL != null) {
			RemoteCalendar remoteCal = new RemoteCalendar(RemoteCalendar.CalendarType.ICAL, iCalURL);
			this.calendar.setCalendarModel(remoteCal.getModel());
			this.calendar.setDate(Calendar.getInstance().getTime());
		}
		
		SFrame rootFrame = new SFrame();
		rootFrame.setTitle("Demo Event-Calendar!");
		rootFrame.setBackground(Color.LIGHT_GRAY);
		
		SPanel mainPanel = new SPanel();
		mainPanel.setLayout(new SBorderLayout());

		SLabel titleLabel = new SLabel("Calendar Example!");
		mainPanel.add(titleLabel, SBorderLayout.NORTH);
		
		calendar.setBorder(SBorderFactory.createSLineBorder(new java.awt.Color(100, 100, 255), 2));
		calendar.setPreferredSize(new SDimension("100%", "600"));
		calendar.setHorizontalAlignment(SConstants.CENTER);
		 
		mainPanel.add(calendar, SBorderLayout.CENTER);
		mainPanel.add(getBottomPanel(), SBorderLayout.SOUTH);
		mainPanel.add(getNavigationPanel(), SBorderLayout.NORTH);
		
		rootFrame.getContentPane().add(mainPanel);
		rootFrame.setVisible(true);
	}

	private SPanel getNavigationPanel()
	{
		SURLIcon forwardButtonIcon = new SURLIcon("../icons/navigate_right.png");
		forwardButtonIcon.setIconHeight(32);
		forwardButtonIcon.setIconWidth(32);
		SButton forwardButton = new SButton(forwardButtonIcon);
		forwardButton.setShowAsFormComponent(false);
		forwardButton.addActionListener(
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Calendar tempCal = Calendar.getInstance();
							
							tempCal.setTime(calendar.getDate());
							switch(calendar.getCalendarModel().getView())
							{
								case WEEK:
									tempCal.add(Calendar.DAY_OF_YEAR, +3);
								break;
								case MONTH:
									tempCal.add(Calendar.MONTH, +1);
								break;
							}
							calendar.setDate(tempCal.getTime());
						}
						
					}
			);

		SURLIcon backButtonIcon = new SURLIcon("../icons/navigate_left.png");
		backButtonIcon.setIconHeight(32);
		backButtonIcon.setIconWidth(32);
		SButton backButton = new SButton(backButtonIcon);
		backButton.setShowAsFormComponent(false);
		backButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Calendar tempCal = Calendar.getInstance();
						
						tempCal.setTime(calendar.getDate());
						switch(calendar.getCalendarModel().getView())
						{
							case WEEK:
								tempCal.add(Calendar.DAY_OF_YEAR, -3);
							break;
							case MONTH:
								tempCal.add(Calendar.MONTH, -1);
							break;
						}
						calendar.setDate(tempCal.getTime());
					}
					
				}
		);
		
		SPanel navigationPanel = new SPanel();

		final SLabel monthLabel = new SLabel();
		SFont font = new SFont();
		font.setSize(14);
		monthLabel.setFont(font);
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(calendar.getDate());
		monthLabel.setText(tempCal.getDisplayName(Calendar.MONTH, Calendar.LONG, calendar.getLocale()).trim() + " " + tempCal.get(Calendar.YEAR));
		
		calendar.addCalendarViewChangeListener(
				new CalendarViewChangeListener()
				{
					@Override
					public void valueChanged(CalendarViewChangeEvent e) {
						// TODO Auto-generated method stub
						switch(e.getType())
						{
							case DATE:
								Calendar tempCal = Calendar.getInstance();
								tempCal.setTime(e.getDate());
								monthLabel.setText(tempCal.getDisplayName(Calendar.MONTH, Calendar.LONG, calendar.getLocale()).trim() + " " + tempCal.get(Calendar.YEAR));
							break;
						}
					}
				}
			);
		
		/*
		calendar.addSelectionChangeListener(
				new SelectionChangeListener()
				{
					@Override
					public void appointmentSelected(IAppointment appointment) {
						LOG.info("Appointment selected: " + appointment.toString());
					}

					@Override
					public void cellSelected(Date date) {
						LOG.info("Cell selected: " + date.toString());
					}

					@Override
					public void cellDeselected(Date date) {
						LOG.info("Cell deselected: " + date.toString());
					}
					
				}
			);
		*/
		SBoxLayout layout = new SBoxLayout(navigationPanel, SBoxLayout.HORIZONTAL);
		layout.setHgap(4);
		navigationPanel.setLayout(layout);
		navigationPanel.add(backButton);
		navigationPanel.add(monthLabel);
		navigationPanel.add(forwardButton);
		navigationPanel.setHorizontalAlignment(SBoxLayout.CENTER_ALIGN);
		
		return navigationPanel;
	}
	
	private SPanel getBottomPanel()
	{
		SPanel panel = new SPanel();
		SBoxLayout layout = new SBoxLayout(panel, SBoxLayout.HORIZONTAL);
		layout.setHgap(4);
		panel.setLayout(layout);
		
		SButton button1 = new SButton("Generate Random Events");
		button1.addActionListener(
				new ActionListener() 
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						generateAndSetTestAppointments2();
					}
					
				});
		
		String[] array = {"Monat", "Woche", "Tag"};
		SComboBox comboBox1 = new SComboBox(array);
		comboBox1.addActionListener(
					new ActionListener() 
					{

						@Override
						public void actionPerformed(ActionEvent arg0) {
							int selection = ((SComboBox)arg0.getSource()).getSelectedIndex();
							if(selection == 0)
								calendar.getCalendarModel().setView(CalendarView.MONTH);
							if(selection == 1)
								calendar.getCalendarModel().setView(CalendarView.WEEK);
							if(selection == 2)
								calendar.getCalendarModel().setView(CalendarView.DAY);
						}
						
					}
				);
		
		String[] dateArray = {"NONE", "SINGLE_DATE", "MULTIPLE_DATE", "DESELECT_APPOINTMENT_ON_DATE|SINGLE_DATE"};
		SComboBox comboBox2 = new SComboBox(dateArray);
		comboBox2.addActionListener(
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						int oldVal = calendar.getSelectionModel().getSelectionMode();
						// preserve all bits not reserved for dates 
						int newVal = oldVal & ~CalendarSelectionModel.DATE_BITMASK;
						int selection = ((SComboBox)e.getSource()).getSelectedIndex();
						if(selection == 1)
							newVal |= CalendarSelectionModel.SINGLE_DATE_SELECTION;
						if(selection == 2)
							newVal |= CalendarSelectionModel.MULTIPLE_DATE_SELECTION;
						if(selection == 3)
							newVal |= CalendarSelectionModel.DESELECT_APPOINTMENT_ON_DATE_SELECTION|CalendarSelectionModel.SINGLE_DATE_SELECTION;

						calendar.getSelectionModel().setSelectionMode(newVal);
					}
				}
			);

		String[] appArray = {"NONE", "SINGLE_APPOINTMENT", "MULTIPLE_APPOINTMENT", "DESELECT_DATE_ON_APPOINTMENT|SINGLE_APPOINTMENT"};
		SComboBox comboBox3 = new SComboBox(appArray);
		comboBox3.addActionListener(
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						int oldVal = calendar.getSelectionModel().getSelectionMode();
						// preserve all bits not reserved for appointments 
						int newVal = oldVal & ~CalendarSelectionModel.APPOINTMENT_BITMASK;
						int selection = ((SComboBox)e.getSource()).getSelectedIndex();
						if(selection == 1)
							newVal |= CalendarSelectionModel.SINGLE_APPOINTMENT_SELECTION;
						if(selection == 2)
							newVal |= CalendarSelectionModel.MULTIPLE_APPOINTMENT_SELECTION;
						if(selection == 3)
							newVal |= CalendarSelectionModel.DESELECT_DATE_ON_APPOINTMENT_SELECTION | CalendarSelectionModel.SINGLE_APPOINTMENT_SELECTION;
						
						calendar.getSelectionModel().setSelectionMode(newVal);
					}
				}
			);
		
		panel.setHorizontalAlignment(SConstants.CENTER);
		panel.add(button1);
		panel.add(comboBox1);
		panel.add(comboBox2);
		panel.add(comboBox3);
		return panel;
	}
	/**
	 * Generates and sets some test appointments for the current month
	 */
	private void generateAndSetTestAppointments2()
	{
		final long ONE_MINUTE_IN_MILLISECONDS = 60*1000; 
		Collection<IAppointment> appointments = new ArrayList<IAppointment>();
		
		Calendar todayCal = Calendar.getInstance();
		
		Date startDate = new Date(todayCal.getTime().getTime());
		Date endDate = new Date(todayCal.getTime().getTime()+120*ONE_MINUTE_IN_MILLISECONDS);
		Appointment simpleEvent = new Appointment("NormalEvent lang lang lang lang lang lang lang", null, IAppointment.AppointmentType.NORMAL, startDate, endDate);
		simpleEvent.setForegroundColor(Color.BLACK);
		simpleEvent.setBackgroundColor(Color.MAGENTA);
		appointments.add(simpleEvent);

		
		startDate = new Date(todayCal.getTime().getTime()+-3*24*60*ONE_MINUTE_IN_MILLISECONDS);
		endDate = new Date(todayCal.getTime().getTime()+2*24*60*ONE_MINUTE_IN_MILLISECONDS);
		Appointment alldayEvent = new Appointment("AlldayEvent", "Description", IAppointment.AppointmentType.ALLDAY, startDate, endDate);
		alldayEvent.setForegroundColor(Color.WHITE);
		alldayEvent.setBackgroundColor(Color.BLUE);
		appointments.add(alldayEvent);

		startDate = new Date(todayCal.getTime().getTime()+ -10*24*60*ONE_MINUTE_IN_MILLISECONDS);
		endDate = new Date(todayCal.getTime().getTime()+10*24*60*ONE_MINUTE_IN_MILLISECONDS + 90*ONE_MINUTE_IN_MILLISECONDS);
		
		EnumSet<IAppointment.Weekday> weekdays = EnumSet.<IAppointment.Weekday>of(IAppointment.Weekday.SUNDAY, IAppointment.Weekday.TUESDAY);
		Appointment recurringEvent = new Appointment("RecurringNormalEvent", "lang lang lang lang lang lang lagn", IAppointment.AppointmentType.NORMAL, startDate, endDate, weekdays);
		recurringEvent.setForegroundColor(null);
		recurringEvent.setBackgroundColor(null);
		appointments.add(recurringEvent);
		((DefaultCalendarModel)this.calendar.getCalendarModel()).setAppointments(appointments);
	}
}
