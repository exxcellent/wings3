package calendar.plaf;

import org.wings.io.Device;
import calendar.AppointmentCalendar;
import calendar.Appointment;
import calendar.CalendarModel;
import calendar.CalendarSelectionModel;

import javax.swing.text.DateFormatter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Collection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: fr
 * Date: 24.01.2009
 * Time: 16:01:13
 * To change this template use File | Settings | File Templates.
 */
public class WeeklyViewRenderer extends CalendarViewRenderer {
    public void write(Device device, AppointmentCalendar component) throws IOException {
        CalendarModel model = component.getCalendarModel();
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(component.getCalendarModel().getVisibleFrom().getTime());
        startCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = (Calendar)startCal.clone();
        endCal.add(Calendar.DAY_OF_YEAR, 7);
        
        device.print("<table class=\"calendar_week\">");

        while(startCal.before(endCal)) {
            device.print("<tr>");

            if(model.isMergeWeekendsEnabled() && startCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                writeDateCell(device, startCal, component);
                startCal.add(Calendar.DAY_OF_YEAR, 1);

                device.print("<td>");

                device.print("<table>");
                device.print("<tr class=\"weekendmerge\">");
                writeDateCell(device, startCal, component);
                startCal.add(Calendar.DAY_OF_YEAR, 1);
                device.print("</tr>");
                device.print("<tr>");
                writeDateCell(device, startCal, component);
                startCal.add(Calendar.DAY_OF_YEAR, 1);
                device.print("</tr>");
                device.print("</table>");

                device.print("</td>");
            } else {
                writeDateCell(device, startCal, component);
                startCal.add(Calendar.DAY_OF_YEAR, 1);
                writeDateCell(device, startCal, component);
                startCal.add(Calendar.DAY_OF_YEAR, 1);
            }

            device.print("</tr>");
        }
        device.print("</table>");
    }

    public void writeDateCell(Device device, Calendar iterator, AppointmentCalendar appointmentCalendar) throws IOException {
        DateFormat dateLongFormat = new SimpleDateFormat("EEEEEE, dd. MMMMMMM yyyy", appointmentCalendar.getLocale());

        device.print("<td class=\"");
        device.print(getDateCellClassname(iterator, appointmentCalendar));
        device.print("\"");
        device.print(" id=\"");
        device.print(getDateCellId(appointmentCalendar, iterator));
        device.print("\"");
        
        if((appointmentCalendar.getSelectionModel().getSelectionMode() & CalendarSelectionModel.DATE_BITMASK) != 0) {
            writeClickability(device, "Date", appointmentCalendar);
        }

        device.print(">");
        
        device.print("<div class=\"dateheader\">");
        device.print(dateLongFormat.format(iterator.getTime()));
        device.print("</div>");
        device.print("<div class=\"appointmentcontainer\">");
        Collection<Appointment> appointments = appointmentCalendar.getCalendarModel().getAppointments(new java.sql.Date(iterator.getTimeInMillis()));
        if(appointments != null) {
            for(Appointment appointment : appointments) {
                writeAppointment(device, appointment, appointmentCalendar, iterator);
            }
        }
        device.print("</div>");

        device.print("</td>");
    }

    public void writeAppointment(Device device, Appointment appointment, AppointmentCalendar appointmentCalendar, Calendar iterator) throws IOException {
        if(appointmentCalendar.getSelectionModel().isSelected(appointment, new java.sql.Date(iterator.getTimeInMillis())))
            device.print("<div class=\"appointment_selected\"");
        else
            device.print("<div class=\"appointment\"");

        writeAppointmentIdAndPopup(device, iterator, appointment, appointmentCalendar);
        if((appointmentCalendar.getSelectionModel().getSelectionMode() & CalendarSelectionModel.APPOINTMENT_BITMASK) != 0) {
            writeClickability(device, "Appointment", appointmentCalendar);
        }

        if(appointment.getAppointmentType() == Appointment.AppointmentType.ALLDAY) {
            device.print("style=\"text-align:center;\"");
        }

        device.print(">");


        if(appointment.getAppointmentType() == Appointment.AppointmentType.NORMAL)
        {
            String startTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(appointment.getAppointmentStartDate());
            device.print(startTime + " ");
        }

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
