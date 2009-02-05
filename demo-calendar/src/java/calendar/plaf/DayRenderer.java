package calendar.plaf;

import org.wings.io.Device;
import org.wings.plaf.css.Utils;
import calendar.AppointmentCalendar;
import calendar.CalendarModel;
import calendar.Appointment;
import calendar.CalendarSelectionModel;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: fr
 * Date: 04.02.2009
 * Time: 22:12:50
 * To change this template use File | Settings | File Templates.
 */
public class DayRenderer extends CalendarViewRenderer {
    protected int DAYS_TO_SHOW = 1;

    protected void writeAllCellHeader(Device device, AppointmentCalendar component) throws IOException {
        CalendarModel model = component.getCalendarModel();

        Calendar iterator = Calendar.getInstance(model.getLocale());
        iterator.setTimeInMillis(model.getVisibleFrom().getTime());

        DateFormat formatter = new SimpleDateFormat("EE, dd.MM.yyyy", model.getLocale());

        Calendar end = (Calendar)iterator.clone();
        end.add(Calendar.DAY_OF_YEAR, DAYS_TO_SHOW);

        device.print("<tr>");

        device.print("<th class=\"timeprefix\">TP</th>");

        while(iterator.before(end)) {
            device.print("<th>");
			device.print(formatter.format(iterator.getTime()));
            device.print("</th>");

            iterator.add(Calendar.DAY_OF_YEAR, 1);
        }
        device.print("</tr>");
    }

    protected void writeAllDayCells(Device device, Calendar iterator, AppointmentCalendar component) throws IOException {
        CalendarModel model = component.getCalendarModel();
        Collection<Appointment> appointments = model.getAppointments(new java.sql.Date(iterator.getTime().getTime()));
        for(Appointment appointment : appointments) {
            if(appointment.getAppointmentType() == Appointment.AppointmentType.ALLDAY) {
                writeAppointment(device, appointment, component, iterator, 1);
            }
        }
    }

    protected void writeAllTimeCells(Device device, Calendar dateIterator, AppointmentCalendar component) throws IOException {
    }

    protected boolean occursInTime(Calendar timeIterator, Appointment appointment) {
        // only compare time - if you compare dates it causes problems with recurring appointments
        long startTime = appointment.getAppointmentStartDate().getTime() % (24*60*60*1000);
        long endTime = appointment.getAppointmentEndDate().getTime() % (24*60*60*1000);
        long timeFrameStart = (timeIterator.getTimeInMillis()) % (24*60*60*1000);
        long timeFrameEnd = (timeIterator.getTimeInMillis()+60*60*1000) % (24*60*60*1000);

        if(startTime < timeFrameEnd && endTime > timeFrameStart)
            return true;

        return false;
    }

    protected void writeAllCells(Device device, AppointmentCalendar component) throws IOException {
        CalendarModel model = component.getCalendarModel();

        Calendar iterator = Calendar.getInstance(model.getLocale());
        iterator.setTimeInMillis(model.getVisibleFrom().getTime());
        iterator.set(Calendar.HOUR_OF_DAY, 0);
        iterator.set(Calendar.MINUTE, 0);
        iterator.set(Calendar.SECOND, 0);
        iterator.set(Calendar.MILLISECOND, 0);

        Calendar end = (Calendar)iterator.clone();
        end.add(Calendar.DAY_OF_YEAR, DAYS_TO_SHOW);

        // write all day cells
        device.print("<tr>");
        device.print("<td class=\"timeprefix\"></td>");

        while(iterator.before(end)) {
            device.print("<td class=\"");
            device.print(getDateCellClassname(iterator, component));
            device.print("\"");
            device.print(" id=\"");
            device.print(getDateCellId(component, iterator) + ":" + iterator.get(Calendar.HOUR_OF_DAY));
            device.print("\"");

            if((component.getSelectionModel().getSelectionMode() & CalendarSelectionModel.DATE_BITMASK) != 0) {
                writeClickability(device, "Date", component);
            }
            device.print(">");

            Collection<Appointment> appointmentsForDay = model.getAppointments(new java.sql.Date(iterator.getTimeInMillis()));
            if(appointmentsForDay == null) {
                iterator.add(Calendar.DAY_OF_YEAR, 1);
                continue;
            }

            List<Appointment> appointments = new ArrayList<Appointment>();
            for(Appointment appointment : appointmentsForDay) {
                if(appointment.getAppointmentType() != Appointment.AppointmentType.ALLDAY)
                    continue;

                appointments.add(appointment);
            }

            for(Appointment appointment : appointments) {
                writeAppointment(device, appointment, component, iterator, appointments.size());
            }
            device.print("</td>");

            iterator.add(Calendar.DAY_OF_YEAR, 1);
        }
        device.print("</tr>");


        DateFormat formatter = new SimpleDateFormat("HH:mm", model.getLocale());


        // write all Time cells
        for(int i=0; i<24; ++i) {
            iterator.setTimeInMillis(model.getVisibleFrom().getTime());
            iterator.set(Calendar.HOUR_OF_DAY, i);
            iterator.set(Calendar.MINUTE, 0);
            iterator.set(Calendar.SECOND, 0);
            iterator.set(Calendar.MILLISECOND, 0);

            device.print("<tr>");
            device.print("<td class=\"timeprefix\">");
            device.print(formatter.format(iterator.getTime()));
            device.print("</td>");

            while(iterator.before(end)) {
                Collection<Appointment> appointmentsForDay = model.getAppointments(new java.sql.Date(iterator.getTimeInMillis()));

                device.print("<td class=\"");
                device.print(getDateCellClassname(iterator, component));
                device.print("\"");
                device.print(" id=\"");
                device.print(getDateCellId(component, iterator) + ":" + iterator.get(Calendar.HOUR_OF_DAY));
                device.print("\"");

                if((component.getSelectionModel().getSelectionMode() & CalendarSelectionModel.DATE_BITMASK) != 0) {
                    writeClickability(device, "Date", component);
                }
                device.print(">");

                List<Appointment> appointments = new ArrayList<Appointment>();

                // print the relevant appointments
                //System.out.println("appointments: " + appointments.size());
                if(appointmentsForDay != null) {
                    for(Appointment appointment : appointmentsForDay) {
                        if(appointment.getAppointmentType() == Appointment.AppointmentType.ALLDAY)
                            continue;


                        // appointment start date BEFORE end of cell AND
                        // appointment end date AFTER start of cell
                        if(!occursInTime(iterator, appointment))
                            continue;

                        appointments.add(appointment);
                    }

                    for(Appointment appointment : appointments) {
                        writeAppointment(device, appointment, component, iterator, appointments.size());
                    }
                }

                device.print("</td>");

                iterator.add(Calendar.DAY_OF_YEAR, 1);
            }

            device.print("</tr>");
        }

    }

    public void write(Device device, AppointmentCalendar component) throws IOException {
        device.print("<table class=\"calendar_workweek\">");

        device.print("<thead>");
        writeAllCellHeader(device, component);
        device.print("</thead>");

        device.print("<tbody>");
        writeAllCells(device, component);
        device.print("</tbody>");

        device.print("</table>");
    }

    public void writeAppointment(Device device, Appointment appointment, AppointmentCalendar appointmentCalendar, Calendar iterator, int nrOfAppointments) throws IOException {
        if(appointmentCalendar.getSelectionModel().isSelected(appointment, new java.sql.Date(iterator.getTimeInMillis())))
            device.print("<div class=\"appointment_selected\"");
        else
            device.print("<div class=\"appointment\"");

        writeAppointmentIdAndPopup(device, iterator, appointment, appointmentCalendar);
        if((appointmentCalendar.getSelectionModel().getSelectionMode() & CalendarSelectionModel.APPOINTMENT_BITMASK) != 0) {
            writeClickability(device, "Appointment", appointmentCalendar);
        }
        Utils.optAttribute(device, "style", "float:left; margin-left:2px; margin-right:auto; width:" + (90/nrOfAppointments) +"%; border:1px solid black;");
        device.print(">");

        device.print(appointment.getAppointmentName());
        device.print("</div>");
    }

    public String getDateCellClassname(Calendar iterator, AppointmentCalendar appointmentCalendar) throws IOException {
        CalendarModel model = appointmentCalendar.getCalendarModel();

        Calendar activeMonth = Calendar.getInstance(model.getLocale());
        activeMonth.setTimeInMillis((model.getVisibleFrom().getTime()));
        Calendar today = Calendar.getInstance();

        boolean isActiveMonth = iterator.get(Calendar.MONTH) == activeMonth.get(Calendar.MONTH);
        boolean isToday = iterator.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);

        if(appointmentCalendar.getSelectionModel().isSelected(new java.sql.Date(iterator.getTimeInMillis())))
            return "selected";
        else if(isToday)
            return "today";
        else if(isActiveMonth)
            return "activemonth";
        else
            return "inactivemonth";
    }
}
