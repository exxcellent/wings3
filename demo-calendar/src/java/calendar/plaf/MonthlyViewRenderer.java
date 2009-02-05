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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Date;

public class MonthlyViewRenderer extends CalendarViewRenderer {

    public void writeAllCellHeader(Device device, AppointmentCalendar appointmentCalendar) throws IOException {
        CalendarModel model = appointmentCalendar.getCalendarModel();
        
        Calendar iterator = Calendar.getInstance(model.getLocale());
        iterator.setTimeInMillis(model.getVisibleFrom().getTime());

        DateFormat formatter = new SimpleDateFormat("EE", model.getLocale());

        Calendar end = (Calendar)iterator.clone();
        end.add(Calendar.DAY_OF_YEAR, 7);

        device.print("<tr>");
        while(iterator.before(end)) {
            device.print("<th>");

            /*
            if(model.isMergeWeekendsEnabled() && iterator.getFirstDayOfWeek() != Calendar.SUNDAY && iterator.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                device.print(formatter.format(iterator.getTime()) + " / ");
                iterator.add(Calendar.DAY_OF_YEAR, 1);
            } */

			device.print(formatter.format(iterator.getTime()));

            device.print("</th>");

            iterator.add(Calendar.DAY_OF_YEAR, 1);
        }
        device.print("</tr>");
    }

    public void writeAppointment(Device device, Appointment appointment, AppointmentCalendar appointmentCalendar, Calendar iterator, int nrOfAppointments) throws IOException {
        if(appointmentCalendar.getSelectionModel().isSelected(appointment, new Date(iterator.getTimeInMillis())))
            device.print("<div class=\"appointment_selected\"");
        else
            device.print("<div class=\"appointment\"");

        writeAppointmentIdAndPopup(device, iterator, appointment, appointmentCalendar);
        if((appointmentCalendar.getSelectionModel().getSelectionMode()&CalendarSelectionModel.APPOINTMENT_BITMASK) != 0) {
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

    public void writeDateCell(Device device, Calendar iterator, AppointmentCalendar appointmentCalendar, String className) throws IOException {
        CalendarModel model = appointmentCalendar.getCalendarModel();
        
        device.print("<td");
        device.print(" id=\"");
        device.print(getDateCellId(appointmentCalendar, iterator));
        device.print("\"");


        if((appointmentCalendar.getSelectionModel().getSelectionMode()& CalendarSelectionModel.DATE_BITMASK) != 0) {
            writeClickability(device, "Date", appointmentCalendar);
        }
        
        if(className != null) {
            device.print(" class=\"");
            device.print(className);
            device.print("\"");
        }

        device.print(">");

        device.print("<div>");
        device.print(iterator.get(Calendar.DAY_OF_MONTH));
        device.print(".");
        device.print("</div>");

        device.print("<div class=\"appointmentcontainer\">");
        Collection<Appointment> appointments = model.getAppointments(new Date(iterator.getTime().getTime()));
        if(appointments != null) {
            for(Appointment appointment : appointments) {
                writeAppointment(device, appointment, appointmentCalendar, iterator, 1);
            }
        }
        device.print("</div>");

        device.print("</td>");
    }

    public void writeAllCells(Device device, AppointmentCalendar appointmentCalendar) throws IOException {
        CalendarModel model = appointmentCalendar.getCalendarModel();
        
        Calendar iterator = Calendar.getInstance(model.getLocale());
        iterator.setTime(model.getVisibleFrom());

        Calendar end = (Calendar)iterator.clone();
        end.add(Calendar.DAY_OF_YEAR, 7*6);

        while(iterator.before(end)) {
            for(int i=0; i<6; ++i) {

                device.print("<tr>");

                for(int j=0; j<7; ++j) {
                    writeDateCell(device, iterator, appointmentCalendar);
                    iterator.add(Calendar.DAY_OF_YEAR, 1);
                }

                device.print("</tr>");
            }
        }

    }


    public void writeDateCell(Device device, Calendar iterator, AppointmentCalendar appointmentCalendar) throws IOException {
        writeDateCell(device, iterator, appointmentCalendar, getDateCellClassname(iterator, appointmentCalendar));
    }

    public String getDateCellClassname(Calendar iterator, AppointmentCalendar appointmentCalendar) throws IOException {
        CalendarModel model = appointmentCalendar.getCalendarModel();

        Calendar activeMonth = Calendar.getInstance(model.getLocale());
        activeMonth.setTimeInMillis((model.getVisibleFrom().getTime() + model.getVisibleUntil().getTime()) / 2);
        Calendar today = Calendar.getInstance();

        boolean isActiveMonth = iterator.get(Calendar.MONTH) == activeMonth.get(Calendar.MONTH);
        boolean isToday = iterator.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);

        if(appointmentCalendar.getSelectionModel().isSelected(new Date(iterator.getTimeInMillis())))
            return "selected";
        else if(isToday)
            return "today";
        else if(isActiveMonth)
            return "activemonth";
        else
            return "inactivemonth";
    }

    public void write(Device device, AppointmentCalendar component) throws IOException {
        device.print("<table class=\"calendar_month\">");

        device.print("<thead>");
        writeAllCellHeader(device, component);
        device.print("</thead>");

        device.print("<tbody>");
        writeAllCells(device, component);
        device.print("</tbody>");

        device.print("</table>");
    }
}
