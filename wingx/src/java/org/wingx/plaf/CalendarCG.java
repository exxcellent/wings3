package org.wingx.plaf;

import java.util.Date;

import org.wings.plaf.ContainerCG;
import org.wings.plaf.Update;
import org.wingx.XCalendar;

public interface CalendarCG<COMPONENT_TYPE extends XCalendar> extends ContainerCG<COMPONENT_TYPE> {

    public Update getHiddenUpdate(COMPONENT_TYPE cal, Date date);

}
