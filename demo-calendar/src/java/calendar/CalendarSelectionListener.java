package calendar;

import java.util.EventListener;

public interface CalendarSelectionListener extends EventListener {
	/**
	 * Called when the value of the selection changes
	 * @param e
	 */
	void valueChanged(CalendarSelectionEvent e);
}
