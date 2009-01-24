package calendar.plaf;

import calendar.AppointmentCalendar;
import calendar.Appointment;
import org.wings.io.Device;
import org.wings.plaf.css.Utils;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;

public abstract class CalendarViewRenderer {
    protected void writeClickability(final Device device, final String dateOrApp, final AppointmentCalendar calendar) throws IOException
    {
        device.print(" onClick=\"");
        device.print("javascript:AppCalendar.click" + dateOrApp + "(this, event, '" + calendar.getName() + "'); return false;");
        device.print("\"");

        device.print(" onDblClick=\"");
        device.print("javascript:AppCalendar.doubleClick" + dateOrApp + "(this, event, '" + calendar.getName() + "'); return false;");
        device.print("\"");
    }

    protected void writeAppointmentIdAndPopup(Device device, Calendar today, Appointment appointment, AppointmentCalendar appointmentCalendar) throws IOException {
        device.print(" id=\"");
        device.print(getAppointmentId(appointment, today, appointmentCalendar));
        device.print("\"");

        device.print(" onmouseover=\"");
        device.print("javascript:AppCalendar.loadPopup(this, event, '" + appointmentCalendar.getName() + "')\"");
        device.print(" onmouseout=\"javascript:AppCalendar.hidePopup(this)\"");
    }

    protected String getAppointmentId(Appointment appointment, Calendar date, AppointmentCalendar appointmentCalendar) {
        java.sql.Date sqlDate = new Date(date.getTimeInMillis());
        String uniqueID = appointmentCalendar.getName() + "_" + appointmentCalendar.getCalendarModel().getUniqueAppointmentID(sqlDate, appointment);
        return uniqueID;
    }

    protected String getDateCellId(AppointmentCalendar appointmentCalendar, Calendar day) {
        String elementID = appointmentCalendar.getName() + "_" + day.get(Calendar.YEAR) + ":" + day.get(Calendar.DAY_OF_YEAR);
        return elementID;
    }

    public abstract void write(Device device, AppointmentCalendar component) throws IOException;
    public abstract void writeAppointment(Device device, Appointment appointment, AppointmentCalendar appointmentCalendar, Calendar iterator) throws IOException;
    public abstract void writeDateCell(Device device, Calendar iterator, AppointmentCalendar appointmentCalendar) throws IOException;
    public abstract String getDateCellClassname(Calendar iterator, AppointmentCalendar appointmentCalendar) throws IOException;
}
