package calendar;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
import org.wings.sdnd.SDropMode;

import calendar.CalendarModel.CalendarView;
import calendar.remote.RemoteCalendar;

import javax.swing.*;

/**
 * Simple Example to show the Calendar Component
 * @author Florian Roks
 *
 */
public class CalendarExample {
	private static final Log LOG = LogFactory.getLog(CalendarExample.class);
	final AppointmentCalendar calendar = new AppointmentCalendar();
	private final String iCalURL = null; //"http://www.google.com/calendar/ical/nn4ucvrich7gnedk1al8l7vu48%40group.calendar.google.com/private-9196b23d4fab909a7c40ea46496cb27c/basic.ics";

	/**
	 * Constructs the Calendar Example
	 */
	public CalendarExample()
	{
		LOG.info("Calendar Example startup");

		if(iCalURL != null) {
			RemoteCalendar remoteCal = new RemoteCalendar(RemoteCalendar.CalendarType.ICAL, iCalURL, "10.1.0.13", 5000);
			this.calendar.setCalendarModel(remoteCal.getModel());
			this.calendar.setDate(Calendar.getInstance().getTime());
		} else {
			DefaultCalendarModel model = new DefaultCalendarModel();
            model.setLocale(Locale.US);
            model.setMergeWeekends(true);
            calendar.setCalendarModel(model);
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
        calendar.setDragEnabled(true);
        calendar.setDropMode(SDropMode.USE_SELECTION);
        
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

						public void actionPerformed(ActionEvent e) {
							Calendar tempCal = Calendar.getInstance();

							tempCal.setTime(calendar.getDate());
							switch(calendar.getCalendarModel().getView())
							{
								case WEEK:
									tempCal.add(Calendar.DAY_OF_YEAR, +7);
								break;
								case MONTH:
									tempCal.add(Calendar.MONTH, +1);
								break;
								case DAY:
									tempCal.add(Calendar.DAY_OF_YEAR, +1);
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

					public void actionPerformed(ActionEvent e) {
						Calendar tempCal = Calendar.getInstance();

						tempCal.setTime(calendar.getDate());
						switch(calendar.getCalendarModel().getView())
						{
							case WEEK:
								tempCal.add(Calendar.DAY_OF_YEAR, -7);
							break;
							case MONTH:
								tempCal.add(Calendar.MONTH, -1);
							break;
							case DAY:
								tempCal.add(Calendar.DAY_OF_YEAR, -1);
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

        DateFormat format = new SimpleDateFormat("MMMMMMM yyyy", calendar.getLocale());
        monthLabel.setText(format.format(tempCal.getTime()));
// jdk 1.6
//		monthLabel.setText(tempCal.getDisplayName(Calendar.MONTH, Calendar.LONG, calendar.getLocale()).trim() + " " + tempCal.get(Calendar.YEAR));

		calendar.addCalendarViewChangeListener(
				new CalendarViewChangeListener()
				{

					public void valueChanged(CalendarViewChangeEvent e) {
						switch(e.getType())
						{
							case DATE:
								Calendar tempCal = Calendar.getInstance();
								tempCal.setTime(e.getDate());

                                DateFormat format = new SimpleDateFormat("MMMMMMMM yyyy", calendar.getLocale());
                                monthLabel.setText(format.format(tempCal.getTime()));
								// jdk1.6
                                //monthLabel.setText(tempCal.getDisplayName(Calendar.MONTH, Calendar.LONG, calendar.getLocale()).trim() + " " + tempCal.get(Calendar.YEAR));
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
					public void appointmentSelected(Appointment appointment) {
						LOG.info("DefaultAppointment selected: " + appointment.toString());
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

        SButton button0 = new SButton("Delete Events");
        button0.addActionListener(
                new ActionListener()
                {


                    public void actionPerformed(ActionEvent e) {
                        deleteAppointments();
                    }

                });

		SButton button1 = new SButton("Generate Random Events");
		button1.addActionListener(
				new ActionListener()
				{


					public void actionPerformed(ActionEvent e) {
						generateAndSetTestAppointments2();
					}

				});

		String[] array = {"Monat", "Woche", "Tag"};
		SComboBox comboBox1 = new SComboBox(array);
		comboBox1.addActionListener(
					new ActionListener()
					{


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

		String[] dateArray = {"NONE", "SINGLE_DATE", "MULTIPLE_DATE", "DESELECT_APPOINTMENT_ON_DATE|SINGLE_DATE", "DESELECT_APPOINTMENT_ON_DATE|MULTIPLE_DATE"};
		SComboBox comboBox2 = new SComboBox(dateArray);
		comboBox2.addActionListener(
				new ActionListener()
				{
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
                        if(selection == 4)
                            newVal |= CalendarSelectionModel.DESELECT_APPOINTMENT_ON_DATE_SELECTION|CalendarSelectionModel.MULTIPLE_DATE_SELECTION;

						calendar.getSelectionModel().setSelectionMode(newVal);
					}
				}
			);

		String[] appArray = {"NONE", "SINGLE_APPOINTMENT", "MULTIPLE_APPOINTMENT", "DESELECT_DATE_ON_APPOINTMENT|SINGLE_APPOINTMENT", "DESELECT_DATE_ON_APPOINTMENT|MULTIPLE_APPOINTMENT"};
		SComboBox comboBox3 = new SComboBox(appArray);
		comboBox3.addActionListener(
				new ActionListener()
				{
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
                        if(selection == 4)
                            newVal |= CalendarSelectionModel.DESELECT_DATE_ON_APPOINTMENT_SELECTION | CalendarSelectionModel.MULTIPLE_APPOINTMENT_SELECTION;

						calendar.getSelectionModel().setSelectionMode(newVal);
					}
				}
			);

		panel.setHorizontalAlignment(SConstants.CENTER);
        panel.add(button0);
		panel.add(button1);
		panel.add(comboBox1);
		panel.add(comboBox2);
		panel.add(comboBox3);
		return panel;
	}

    private void deleteAppointments()
    {
        ((DefaultCalendarModel)this.calendar.getCalendarModel()).setAppointments(null);
    }
    /**
	 * Generates and sets some test appointments for the current month
	 */
	private void generateAndSetTestAppointments2()
	{
		final long ONE_MINUTE_IN_MILLISECONDS = 60*1000;
		Collection<Appointment> appointments = new ArrayList<Appointment>();

		Calendar todayCal = Calendar.getInstance();

		Date startDate = new Date(todayCal.getTime().getTime());
		Date endDate = new Date(todayCal.getTime().getTime()+120*ONE_MINUTE_IN_MILLISECONDS);
		DefaultAppointment simpleEvent = new DefaultAppointment("NormalEvent lang lang lang lang lang lang lang", null, Appointment.AppointmentType.NORMAL, startDate, endDate);
		simpleEvent.setForegroundColor(Color.BLACK);
		simpleEvent.setBackgroundColor(Color.MAGENTA);
		appointments.add(simpleEvent);


		startDate = new Date(todayCal.getTime().getTime()+-3*24*60*ONE_MINUTE_IN_MILLISECONDS);
		endDate = new Date(todayCal.getTime().getTime()+2*24*60*ONE_MINUTE_IN_MILLISECONDS);
		DefaultAppointment alldayEvent = new DefaultAppointment("AlldayEvent", "Description", Appointment.AppointmentType.ALLDAY, startDate, endDate);
		alldayEvent.setForegroundColor(Color.WHITE);
		alldayEvent.setBackgroundColor(Color.BLUE);
		appointments.add(alldayEvent);

		startDate = new Date(todayCal.getTime().getTime()+ -10*24*60*ONE_MINUTE_IN_MILLISECONDS);
		endDate = new Date(todayCal.getTime().getTime()+10*24*60*ONE_MINUTE_IN_MILLISECONDS + 90*ONE_MINUTE_IN_MILLISECONDS);

		EnumSet<Appointment.Weekday> weekdays = EnumSet.<Appointment.Weekday>of(Appointment.Weekday.FRIDAY, Appointment.Weekday.TUESDAY);
		DefaultAppointment recurringEvent = new DefaultAppointment("RecurringNormalEvent", "lang lang lang lang lang lang lagn", Appointment.AppointmentType.NORMAL, startDate, endDate, weekdays);
		recurringEvent.setForegroundColor(null);
		recurringEvent.setBackgroundColor(null);
		appointments.add(recurringEvent);
		((DefaultCalendarModel)this.calendar.getCalendarModel()).setAppointments(appointments);
	}
}
