/*
 * CalendarCG.java
 *
 * Created on 12. Juni 2006, 09:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wingx.plaf;

import java.util.Date;
import org.wings.plaf.Update;
import org.wingx.XCalendar;

/**
 *
 *  * @author <a href="mailto:e.habicht@thiesen.com">Erik Habicht</a>
 */
public interface CalendarCG extends org.wings.plaf.ContainerCG {
    
    public Update getHiddenUpdate(XCalendar cal, Date date);
    
}

