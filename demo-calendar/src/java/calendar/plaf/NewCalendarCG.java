package calendar.plaf;

import calendar.*;
import org.wings.plaf.css.AbstractComponentCG;
import org.wings.plaf.css.Utils;
import org.wings.plaf.css.AbstractUpdate;
import org.wings.plaf.css.UpdateHandler;
import org.wings.plaf.Update;
import org.wings.io.Device;
import org.wings.io.StringBuilderDevice;
import org.wings.header.Header;
import org.wings.header.SessionHeaders;
import org.wings.SComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.sql.Date;

public class NewCalendarCG extends AbstractComponentCG<AppointmentCalendar> {
    private CalendarViewRenderer monthRenderer;
    private CalendarViewRenderer weekRenderer;
    private CalendarViewRenderer workweekRenderer;
    private CalendarViewRenderer dayRenderer;
    protected final transient static Log LOG = LogFactory.getLog(NewCalendarCG.class);

    protected final static List<Header> headers;
    static {
        headers = new ArrayList<Header>();
        headers.add(Utils.createExternalizedCSSHeader("calendar/css/calendar_month.css"));
        headers.add(Utils.createExternalizedCSSHeader("calendar/css/calendar_week.css"));
        headers.add(Utils.createExternalizedCSSHeader("calendar/css/calendar_workweek.css"));
        headers.add(Utils.createExternalizedJSHeader("calendar/js/calendar.js"));
    }

    @Override
    public void installCG(AppointmentCalendar component) {
        super.installCG(component);

        SessionHeaders.getInstance().registerHeaders(headers);

        monthRenderer = new MonthlyViewRenderer();
        weekRenderer = new WeeklyViewRenderer();
        workweekRenderer = new WorkWeekRenderer();
        dayRenderer = new DayRenderer();
    }


    @Override
    public void uninstallCG(AppointmentCalendar component) {
        super.uninstallCG(component);

        SessionHeaders.getInstance().deregisterHeaders(headers);
    }

    public void writeAppointment(Device device, Appointment appointment, AppointmentCalendar appointmentCalendar, Calendar iterator, int nrOfAppointments) throws IOException {
        CalendarModel model = appointmentCalendar.getCalendarModel();
        switch(model.getView()) {
            case MONTH:
                monthRenderer.writeAppointment(device, appointment,  appointmentCalendar, iterator, nrOfAppointments);
            break;
            case WEEK:
                weekRenderer.writeAppointment(device, appointment, appointmentCalendar, iterator, nrOfAppointments);
            break;
            default:
                throw new RuntimeException("unsupported view");
        }
    }

    public String getDateCellClassName(Calendar iterator, AppointmentCalendar appointmentCalendar) throws IOException {
        CalendarModel model = appointmentCalendar.getCalendarModel();
        switch(model.getView()) {
            case MONTH:
                return monthRenderer.getDateCellClassname(iterator, appointmentCalendar);
            case WEEK:
                return weekRenderer.getDateCellClassname(iterator, appointmentCalendar);
            default:
                throw new RuntimeException("unsupported view");
        }
    }

    public void writeInternal(Device device, AppointmentCalendar component) throws IOException {
        CalendarModel model = component.getCalendarModel();
        assert(model != null);
        assert(model.getVisibleFrom() != null && model.getVisibleUntil() != null);
        assert(model.getVisibleFrom().before(model.getVisibleUntil()));

        device.print("<div");
        Utils.writeAllAttributes(device, component);
        device.print(">");

        switch(model.getView()) {
            case MONTH:
                    monthRenderer.write(device, component);
                break;
            case WEEK:
                    weekRenderer.write(device, component);
                break;
            case WORKWEEK:
                    workweekRenderer.write(device, component);
                break;
            case DAY:
                    dayRenderer.write(device, component);
                break;
            default:
                    throw new RuntimeException("unsupported view");
        }

        device.print("</div>");
    }

    /**
     * Writes a Popup for appointment to device
     * @param device
     * @param calendar
     * @param appointment
     * @throws IOException
     */
    public void writePopupText(final Device device, final AppointmentCalendar calendar, final Appointment appointment) throws IOException
    {
        if(appointment == null)
            return;

        device.print(appointment.getAppointmentName() + "<br />");
        if(appointment.getAppointmentDescription() != null && appointment.getAppointmentDescription().length() > 0)
            device.print(appointment.getAppointmentDescription() + "<br />");

        Calendar cal1 = Calendar.getInstance(calendar.getCalendarModel().getTimeZone());
        cal1.setTime(appointment.getAppointmentStartDate());
        Calendar cal2 = Calendar.getInstance(calendar.getCalendarModel().getTimeZone());
        cal2.setTime(appointment.getAppointmentEndDate());

        device.print(appointment.getAppointmentStartEndDateString(calendar.getCalendarModel().getTimeZone(), calendar.getLocale()) + "<br />");

        if(appointment.getAppointmentType() == Appointment.AppointmentType.NORMAL)
            device.print(appointment.getAppointmentStartEndTimeString(calendar.getCalendarModel().getTimeZone(), calendar.getLocale()) + "<br />");
        else // if the appointment ain't normal, it must be ALLDAY => timeframe is useless
            device.print(appointment.getAppointmentTypeString(appointment.getAppointmentType(), calendar.getLocale()) + "<br />");

        if(appointment.isAppointmentRecurring() && appointment.getAppointmentRecurringDays() != null)
            device.print(appointment.getAppointmentRecurringDaysString(calendar.getLocale()) + "<br />");

        String additionalInformation = appointment.getAdditionalAppointmentInformation();
        if(additionalInformation != null && additionalInformation.length() > 0)
            device.print(additionalInformation + "<br />");
    }

    /**
     * Gets a Popup update for the given uniqueAppointmentID
     * @param calendar
     * @param uniqueAppointmentID
     * @return
     */
    public Update getPopupUpdate(AppointmentCalendar calendar, String uniqueAppointmentID)
    {
        String[] data = uniqueAppointmentID.split(":");
        // data[0] = Year, data[1] = day of year, data[2] appointment number of the day (starting with 0)
        // this is NOT safe, as the input from browser could be manipulated and lead to a exception
        Calendar cal = Calendar.getInstance(calendar.getCalendarModel().getTimeZone());
        cal.set(Calendar.YEAR, Integer.parseInt(data[0]));
        cal.set(Calendar.DAY_OF_YEAR, Integer.parseInt(data[1]));

        try {
            Collection<Appointment> appointments = calendar.getCalendarModel().getAppointments(new Date(cal.getTimeInMillis()));
            if(appointments != null) {
                Object appointmentArray[] = appointments.toArray();
                return new AppointmentPopupUpdate(calendar, (Appointment)appointmentArray[Integer.parseInt(data[2])]);
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            LOG.debug("A Popup was requested for a non-existing appointment.");
        }

        //System.out.println("no appointments on that day - should not happen");
        return null;
    }

    /**
     * AppointmentPopupUpdate - Update to be sent when a Popup is requested
     * @author Florian Roks
     *
     */
    public class AppointmentPopupUpdate extends AbstractUpdate {
        String htmlCode = "";
        String exception = null;
        Appointment appointment;

        /**
         * Constructs a Popup Update which is to be sent after a popup request
         * @param component
         * @param appointment
         */
        @SuppressWarnings("unchecked")
        public AppointmentPopupUpdate(SComponent component, Appointment appointment) {
            super(component);

            this.appointment = appointment;
        }

        @Override
        public Handler getHandler() {
            UpdateHandler handler = new UpdateHandler("runScript");
            String htmlCode = "";

            try
            {
                StringBuilderDevice htmlDevice = new StringBuilderDevice(1024);
                ((NewCalendarCG)component.getCG()).writePopupText(htmlDevice, (AppointmentCalendar)component, this.appointment);
                htmlCode = htmlDevice.toString();
                htmlCode = htmlCode.replaceAll("\\r", "");
                htmlCode = htmlCode.replaceAll("\r", "");
                htmlCode = htmlCode.replaceAll("\\n", "<br />");
                htmlCode = htmlCode.replaceAll("\n", "<br />");

            }
            catch(Throwable t)
            {
                LOG.fatal("An error occured during rendering of AppointmentCalendar-Popup");
                exception = t.getClass().getName();
                t.printStackTrace();
            }

            handler.addParameter("Tip(\"" + htmlCode +  "\", DELAY, 0, FADEIN, 0, FADEOUT, 0, OPACITY, 100, FOLLOWMOUSE, true, DURATION, 0, BGCOLOR, 'white');");

            if(exception != null) {
                handler.addParameter(exception);
            }

            return handler;
        }
    }

    /**
     * Gets an Selection Update for the Updates caused by event
     * @param calendar
     * @param selectionModel
     * @param event
     * @return
     */
    public Update getSelectionUpdate(AppointmentCalendar calendar, CalendarSelectionModel selectionModel, CalendarSelectionEvent event)
    {
        if(calendar.getCalendarModel().getView() == CalendarModel.CalendarView.WORKWEEK ||
                calendar.getCalendarModel().getView() == CalendarModel.CalendarView.DAY)
            return new ComponentUpdate(this, calendar);
        
        return new SelectionUpdate(calendar, selectionModel, event);
    }

    /**
     * Constructs a SelectionUpdate
     * @author Florian Roks
     *
     */
    @SuppressWarnings("unchecked")
    public class SelectionUpdate extends AbstractUpdate {
        CalendarSelectionModel model;
        CalendarSelectionEvent event;
        AppointmentCalendar calendar;
        String exception = null;

        /**
         * Constructs a SelectionUpdate for the given Model and event
         * @param component
         * @param model
         * @param event
         */
        @SuppressWarnings("unchecked")
        public SelectionUpdate(final SComponent component, final CalendarSelectionModel model, final CalendarSelectionEvent event) {
            super(component);

            this.model = model;
            this.event = event;
            this.calendar = (AppointmentCalendar)component;
        }

        @Override
        public int hashCode()
        {
            int hash1 =  super.hashCode();
            int hash2 = getHandler().hashCode();
            int hash3 = getHandler().getParameters().hashCode();
            return hash1 ^ hash2 ^ hash3;
        }

        @Override
        public boolean equals(Object obj)
        {
            if(obj == this)
                return true;
            if(obj == null || obj.getClass() != this.getClass())
                return false;

            if(obj instanceof Update)
            {
                Update other = (Update)obj;
                if(!this.getComponent().equals(other.getComponent()))
                    return false;
                if(this.getProperty() != other.getProperty())
                    return false;
                if(this.getPriority() != other.getPriority())
                    return false;
                if(!this.getHandler().equals(other.getHandler()))
                    return false;
            }
            else
            {
                return false;
            }
            return false;
        }

        @Override
        public Handler getHandler() {
            Calendar cal = Calendar.getInstance(((AppointmentCalendar)getComponent()).getCalendarModel().getTimeZone());
            cal.setTime(event.getDate());
            Date visibleFrom = ((AppointmentCalendar)getComponent()).getCalendarModel().getVisibleFrom();
            Date visibleUntil = ((AppointmentCalendar)getComponent()).getCalendarModel().getVisibleUntil();
            Calendar from = Calendar.getInstance(((AppointmentCalendar)getComponent()).getCalendarModel().getTimeZone());
            Calendar until = Calendar.getInstance(((AppointmentCalendar)getComponent()).getCalendarModel().getTimeZone());
            from.setTime(visibleFrom);
            until.setTime(visibleUntil);
            StringBuilderDevice htmlDevice = new StringBuilderDevice(1024);

            switch(event.getAffectedComponent())
            {
                case DATE:
                    String elementID = getComponent().getName() + "_" + cal.get(Calendar.YEAR) + ":" + cal.get(Calendar.DAY_OF_YEAR);

                    UpdateHandler chandler = new UpdateHandler("className");
                    chandler.addParameter(elementID);
                    try {
                        String className = ((NewCalendarCG)calendar.getCG()).getDateCellClassName(cal, (AppointmentCalendar)component);
                        chandler.addParameter(className);
                    } catch (IOException e) {
                        LOG.fatal("An error occured during rendering of AppointmentCalendar-Popup");
                        exception = e.getClass().getName();
                        e.printStackTrace();
                    }
                    return chandler;

                case APPOINTMENT:
                    UpdateHandler handler = new UpdateHandler("component");
                    // add the component id
                    String id = calendar.getCalendarModel().getUniqueAppointmentID(event.getDate(), event.getAppointment());
                    if(id == null) { // appointment does not exist, reload whole component
                        Update update = component.getCG().getComponentUpdate(component);
                        return update.getHandler();
                    }
                    String uniqueAppointmentID = getComponent().getName() + "_" + id;
                    if(uniqueAppointmentID == null)
                    {
                        LOG.info("valid appointment was sent: date:" + event.getDate() + " app: " + event.getAppointment());
                        handler.addParameter("invalid appointment");
                        //calendar.getSelectionModel().removeSelection(event.getAppointment(), event.getDate());
                        return handler;
                    }
                    handler.addParameter(uniqueAppointmentID);
                    String htmlCode;
                    String exception = null;
                    try {
                        ((NewCalendarCG)component.getCG()).writeAppointment(htmlDevice, event.getAppointment(), this.calendar, cal, 1);
                        htmlCode = htmlDevice.toString();
                        handler.addParameter(htmlCode);
                    } catch(Throwable t) {
                        exception = t.getClass().getName();
                    }
                    if(exception != null)
                        handler.addParameter(exception);
                    return handler;
            }

            LOG.fatal("this should never happen!");
            return null;
        }
    }
}
