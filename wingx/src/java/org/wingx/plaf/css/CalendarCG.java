/*
 * CalendarCG.java
 *
 * Created on 12. Juni 2006, 09:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wingx.plaf.css;

import java.text.*;

import org.wings.*;
import org.wings.text.SAbstractFormatter;
import org.wings.util.SessionLocal;
import java.util.*;

import org.wings.plaf.css.*;
import org.wings.header.*;
import org.wings.plaf.Update;
import org.wings.plaf.css.dwr.CallableManager;
import org.wings.plaf.css.script.OnHeadersLoadedScript;
import org.wings.script.JavaScriptEvent;
import org.wings.script.JavaScriptListener;
import org.wings.session.ScriptManager;
import org.wings.session.SessionManager;
import org.wings.session.Session;
import org.wingx.XCalendar;

/**
 *
 *  * @author <a href="mailto:e.habicht@thiesen.com">Erik Habicht</a>
 */
public class CalendarCG extends AbstractComponentCG implements org.wingx.plaf.CalendarCG {

    protected final List<Header> headers = new ArrayList<Header>();

    protected final SessionLocal<CallableCalendar> callableCalendar = new SessionLocal<CallableCalendar>();

    public CalendarCG() {
        headers.add(Utils.createExternalizedJSHeader("org/wingx/calendar/calendar.js"));
        headers.add(Utils.createExternalizedJSHeader(getLangScriptURL()));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/calendar/calendar-setup.js"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/calendar/xcalendar.js"));
        headers.add(Utils.createExternalizedCSSHeader("org/wingx/calendar/calendar.css"));
    }

    public void installCG(final SComponent component) {
        super.installCG(component);
        SessionHeaders.getInstance().registerHeaders(headers);

        if (!CallableManager.getInstance().containsCallable("xcalendar")) {
            CallableManager.getInstance().registerCallable("xcalendar", getCallableCalendar(), CallableCalendar.class);
        }
    }

    public void uninstallCG(SComponent component) {
        SessionHeaders.getInstance().deregisterHeaders(headers);
        CallableManager.getInstance().unregisterCallable("xcalendar");
    }

    /**
     * Returns the language file.
     *
     */
    private String getLangScriptURL() {
        String retVal = "org/wingx/calendar/lang/calendar-" + getLocale().getLanguage() + ".js";
        java.net.URL url = org.wings.plaf.MenuCG.class.getClassLoader().getResource( retVal );
        if ( url == null ) {
            retVal = "org/wingx/calendar/lang/calendar-en.js";
        }
        return retVal;
    }

    public Locale getLocale( ) {
        Session session = SessionManager.getSession();
        return session.getLocale() != null ? session.getLocale() : Locale.getDefault();
    }

    public void writeInternal(org.wings.io.Device device, org.wings.SComponent _c )
    throws java.io.IOException {

        final XCalendar component = (org.wingx.XCalendar) _c;

        final String id_hidden = "hidden" + component.getName();
        final String id_button = "button" + component.getName();
        final String id_clear = "clear" + component.getName();

        SFormattedTextField fTextField = component.getFormattedTextField();
        String key = getCallableCalendar().registerFormatter(fTextField.getFormatter());

        SimpleDateFormat dateFormat  = new SimpleDateFormat("yyyy.MM.dd");
        dateFormat.setTimeZone( component.getTimeZone() );

        device.print("<table");
        writeAllAttributes(device, component);
        device.print("><tr><td class=\"tf\"");

        int oversizePadding = Utils.calculateHorizontalOversize(fTextField, true);
        //oversizePadding += RenderHelper.getInstance(component).getHorizontalLayoutPadding();

        if (oversizePadding != 0)
            Utils.optAttribute(device, "oversize", oversizePadding);
        device.print(">");

        fTextField.setEnabled( component.isEnabled() );
        fTextField.write(device);

        device.print("\n</td><td class=\"b\">\n");

        device.print("<input type=\"hidden\" id=\""+id_hidden+"\" name=\""+id_hidden+"\" formatter=\""+key+"\" value=\""+ format(dateFormat, component.getDate() )+"\">\n");
        device.print("<img class=\"XCalendarButton\" id=\""+id_button+"\" src=\""+component.getEditIcon().getURL()+"\" />\n");

        device.print("</td><td class=\"cb\" width=\"0%\">\n");
        if (component.isNullable() && component.getClearIcon() != null) {
            device.print("<img class=\"XCalendarClearButton\" id=\""+id_clear+"\" src=\""+component.getClearIcon().getURL()+"\" />\n");
        }
        
        if (component.isEnabled() ) {
            String script = generateInitScript(id_hidden, fTextField.getName(), id_button, key, id_clear, component.getActionListeners().length > 0 );
            ScriptManager.getInstance().addScriptListener(new OnHeadersLoadedScript(script, true));
        }

        writeTableSuffix(device, component);
    }

    private String format(DateFormat dateFormat, Date date) {
        if (date == null)
            date = new Date();
        return date != null ? dateFormat.format( date ) : "";
    }

    private String generateInitScript( String hiddenInputFieldId, String textFieldId, String editButtonId,
                                       String formatterKey, String clearButtonId, boolean onUpdateCommit ) {
        final String code = "new wingS.XCalendar(\"{0}\", \"{1}\", \"{2}\", \"{3}\", \"{4}\", \"{5}\");";
        return  MessageFormat.format(code, formatterKey, hiddenInputFieldId, textFieldId, editButtonId, clearButtonId, onUpdateCommit );
    }

    protected CallableCalendar getCallableCalendar() {
        CallableCalendar callableCalendar = this.callableCalendar.get();
        if (callableCalendar == null) {
            callableCalendar = new CallableCalendar();
            this.callableCalendar.set(callableCalendar);
        }
        return callableCalendar;
    }

    public final static class CallableCalendar {
        Map<SAbstractFormatter, Boolean> formatters = new WeakHashMap<SAbstractFormatter, Boolean>();

        /* Timestamt to Human readable */
        public List onCalUpdate(String key, String name, String value, String onUpdateCommit) {
            List<String> list = new LinkedList<String>();
            SAbstractFormatter formatter = formatterByKey(key);
            if ( formatter != null ) {
                list.add( name );
                try {
                    Date newDate = new Date( Long.parseLong( value ) );
                    list.add( formatter.valueToString( newDate ) );
                } catch ( ParseException e ) {
                    list.add( "" );
                }
                list.add( onUpdateCommit );
            }
            return list;
        }

        protected SAbstractFormatter formatterByKey(String key) {
            for (Object o : formatters.keySet()) {
                SAbstractFormatter formatter = (SAbstractFormatter) o;
                if (key.equals("" + System.identityHashCode(formatter))) {
                    return formatter;
                }
            }
            return null;
        }

        public String registerFormatter(SAbstractFormatter formatter) {
            formatters.put(formatter, Boolean.TRUE);
            return "" + System.identityHashCode(formatter);
        }
    }
    
    public Update getHiddenUpdate(XCalendar cal, Date date) {
    	return new HiddenUpdate(cal, date);
    }

    protected static class HiddenUpdate extends AbstractUpdate {

        private Date date;

        public HiddenUpdate(XCalendar cal, Date date) {
            super(cal);
            this.date = date;
        }

        public Handler getHandler() {
            UpdateHandler handler = new UpdateHandler("value");
            handler.addParameter("hidden"+component.getName());
            final SimpleDateFormat dateFormatForHidden  = new SimpleDateFormat("yyyy.MM.dd");
            handler.addParameter(date == null ? "" : dateFormatForHidden.format( date ) );
            return handler;
        }

    }
    
}
