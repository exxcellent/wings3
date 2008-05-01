package calendar;

import org.wings.io.Device;
import java.util.Calendar;

public interface CustomCellRenderer {
	/**
	 * Must be implemented for customized Cell Rendering - must write &lttd&gt&lt/td&gt and content of the cell.   
	 * @param device The device the cell must be written to
	 * @param cellCalendar A Calendar containing the current day to be rendered in the cell
	 * @param calendar The AppointmentCalendar to be used 
	 */
	public abstract void writeCell(final Device device, final Calendar cellCalendar, final AppointmentCalendar calendar);
}
