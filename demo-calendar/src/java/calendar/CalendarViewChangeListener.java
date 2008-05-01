package calendar;

import java.util.EventListener;

/**
 * A Listener for View Changes
 * @author Florian Roks
 *
 */
public interface CalendarViewChangeListener extends EventListener {
	/**
	 * Fires when a value changes (Appointments, Date or CalendarVuew) that affects the view
	 */
	void valueChanged(CalendarViewChangeEvent e);
}
