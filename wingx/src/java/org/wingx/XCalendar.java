/*
 * XCalendar.java
 *
 * Created on 9. Juni 2006, 12:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.wingx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.wings.LowLevelEventListener;
import org.wings.SContainer;
import org.wings.SDimension;
import org.wings.SFormattedTextField;
import org.wings.SIcon;
import org.wings.SResourceIcon;
import org.wings.event.SDocumentEvent;
import org.wings.event.SDocumentListener;
import org.wings.text.SDefaultFormatterFactory;
import org.wings.text.SAbstractFormatter;
import org.wings.text.SDateFormatter;
import org.wings.text.SInternationalFormatter;
import org.wingx.plaf.css.CalendarCG;

/**
 * @author <a href="mailto:e.habich@@thiesen.com">Erik Habicht</a>
 */
public class XCalendar extends SContainer implements LowLevelEventListener, SDocumentListener {
    
    /**
     * The <code>XCalendar</code> default <code>SIcon</code> for editing.
     */
    public static final SIcon DEFAULT_EDIT_ICON = new SResourceIcon("org/wingx/calendar/calendar_edit.gif");
    
    /**
     * The current <code>XCalendar</code>'s icon to choos a new date.
     */
    private SIcon editIcon = DEFAULT_EDIT_ICON;

    /**
     * The current <code>SFormattedTextField</code>.
     */
    private SFormattedTextField fTextField;

    /**
     * The current <code>TimeZone</code>
     */
    private TimeZone timeZone = TimeZone.getDefault();

    private ActionListener actionListener = null;

    private ActionListener getActionListener() {
        if ( actionListener == null ) {
            actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireActionEvents();
                }
            };
        }
        return actionListener;
    }
    
    /**
     * @see LowLevelEventListener#isEpochCheckEnabled()
     */
    protected boolean epochCheckEnabled = true;

    /**
     * Set the look and feel delegate for this component
     * 
     * @see org.wings.SComponent#setCG
     * @param cg the component's cg
     */
    public void setCG(org.wingx.plaf.CalendarCG cg) {
        super.setCG( cg );
    }
    
    /**
     * Creates a <code>XCalendar</code> with the current Date and a default <code>SDateFormatter</code>.
     */
    public XCalendar() {
        this( new SDateFormatter() );
    }
    
    /**
     * Creates a <code>XCalendar</code> with the current Date and the specified <code>SDateFormatter</code>
     * instance.
     * @param formatter <code>SDateFormatter</code> to use for formatting.
     */
    public XCalendar( SDateFormatter formatter ) {
        this( new GregorianCalendar().getTime(), formatter );
    }
    
    /**
     * Creates a <code>XCalendar</code> with the given Date and the specified <code>SDateFormatter</code> 
     * instance.
     * @param date Date
     * @param formatter <code>SDateFormatter</code> to use for formatting.
     */
    public XCalendar( Date date, SDateFormatter formatter ) {
        add(getFormattedTextField());
        setFormatter( formatter );
        setDate( date );
    }



    /**
     * Sets the <code>XCalendar</code>'s default <code>SIcon</code>.
     * @param icon the icon used as the default image.
     * @deprecated Use <code>editIcon</code> instead
     */
    public void setIcon( SIcon icon ) {
        this.editIcon = icon;
    }
    
    /**
     * Returns the default <code>SIcon</code>.
     * @return the default <code>SIcon</code>
     * @deprecated Use <code>editIcon</code> instead
     */
    public SIcon getIcon() {
        return this.editIcon;
    }


    /**
     * Sets the icon to use for calling the date picker.
     * @param icon the icon used as the trigger icon to start the date picker.
     */
    public void setEditIcon( SIcon icon ) {
        this.editIcon = icon;
    }

    /**
     * Returns the current <code>SIcon</code> to start the date picker.
     * @return the default <code>SIcon</code>
     */
    public SIcon getEditIcon() {
        return this.editIcon;
    }
    
    /**
     * The current icon to use for clearing the input/ date.
     * @return The current icon to use for clearing the input/ date.
     * @deprecated Returns now the DEFAULT_EDIT_ICON
     */
    public SIcon getClearIcon() {
        return DEFAULT_EDIT_ICON;
    }

    /**
     * The icon to use for clearing the input/ date.
     * @param clearIcon The new icon to use for clearing the input/ date.
     * @deprecated 
     */
    public void setClearIcon(SIcon clearIcon) {
    }

    /**
     * Defines if the user should be able to select also null values.
     * <p><b>NOTE:</b> He can always do this manually. Default is <code>false</code>
     * @return <code>true</code> if the user has an clear icon to define a null date
     * @deprecated 
     */
    public boolean isNullable() {
        return false;
    }

    /**
     * Defines if the user has an clear icon to reset the picked date to <code>null</code>.
     * @param nullable <code>true</code> if the user should be able to clear the date.
     * @deprecated 
     */
    public void setNullable(boolean nullable) {
    }

    /**
     * Sets the current <code>SDateFormatter</code>.
     * @param formatter <code>SDateFormatter</code> to use for formatting
     */
    public void setFormatter( SDateFormatter formatter ) {
        getFormattedTextField().setFormatterFactory( new SDefaultFormatterFactory( formatter ) );
    }
    
    /**
     * Sets the current <code>TimeZone</code>.
     * @param timeZone <code>TimeZone</code>
     */
    public void setTimeZone( TimeZone timeZone ) {
        this.timeZone = timeZone;
        if ( timeZone != null ) {
            SAbstractFormatter aFormatter = getFormattedTextField().getFormatter();
            if ( aFormatter != null && aFormatter instanceof SInternationalFormatter ) {
                SInternationalFormatter iFormatter = (SInternationalFormatter)aFormatter;
                Format format = iFormatter.getFormat();
                if ( format != null && format instanceof DateFormat ) {
                    ((DateFormat)format).setTimeZone( timeZone );
                }
            }
        }
    }
    
    /**
     * Returns the current <code>TimeZone</code>.
     * @return <code>TimeZone</code>
     */
    public TimeZone getTimeZone () {
        return this.timeZone;
    }
    
    /**
     * Returns the current <code>Date</code>.
     * @return the current <code>Date</code>
     */
    public Date getDate() {
        return (Date)getFormattedTextField().getValue();
    }
    
    /**
     * Sets the current <code>Date</code>.
     * @param date the current <code>Date</code>
     */
    public void setDate( Date date ) {
        getFormattedTextField().removeDocumentListener(this);
        getFormattedTextField().setValue( date );
        getFormattedTextField().addDocumentListener(this);
        if (isUpdatePossible() && date != null )
            update(((CalendarCG)getCG()).getHiddenUpdate(this, date));
        else
            reload();
    }

    /**
     * Set the preferred size of the component
     * @param dimension <code>SDimension</code>
     */
    public void setPreferredSize( SDimension dimension ) {
        super.setPreferredSize(dimension);
        getFormattedTextField().setPreferredSize(dimension != null && dimension.getWidth() != null ? SDimension.FULLWIDTH : null);
    }

    /**
     * Fire an <code>ActionEvent</code> at each registered listener.
     *
     * @param event supplied <code>ActionEvent</code>
     */
    protected void fireActionPerformed(ActionEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = getListenerList();
        ActionEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                if (e == null) {
                    e = new ActionEvent(XCalendar.this,
                            ActionEvent.ACTION_PERFORMED,
                            "",
                            event.getWhen(),
                            event.getModifiers());
                }
                ((ActionListener) listeners[i + 1]).actionPerformed(e);
            }
        }
    }
    
    private void fireActionEvents() {
        fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
    }
    
    /**
     * Adds an action listener to the <code>XCalendar</code>
     *
     * @param listener the ActionListener to be added
     */
    public void addActionListener(ActionListener listener) {
        addEventListener(ActionListener.class, listener);
        if ( getActionListeners().length > 0 && getFormattedTextField().getActionListeners().length == 0 ) {
            getFormattedTextField().addActionListener( getActionListener() );
        }
    }
    
    /**
     * Removes the supplied Listener from the listener list
     * @param listener ActionListener
     */
    public void removeActionListener(ActionListener listener) {
        removeEventListener(ActionListener.class, listener);
        if ( getActionListeners().length == 0 ) {
            getFormattedTextField().removeActionListener( getActionListener() );
        }
    }
    
    /**
     * Returns an array of all the <code>ActionListener</code>s added
     * to this AbstractButton with addActionListener().
     *
     * @return all of the <code>ActionListener</code>s added or an empty
     *         array if no listeners have been added
     */
    public ActionListener[] getActionListeners() {
        return (ActionListener[]) (getListeners(ActionListener.class));
    }
    
    /**
     * Returns the current <code>SFormattedTextField</code>.
     * @return the current <code>SFormattedTextField</code>
     */
    public SFormattedTextField getFormattedTextField() {
        if (fTextField == null) {
            fTextField = new SFormattedTextField();
            fTextField.getDocument().addDocumentListener( this );
        }
        return fTextField;
    }

    public void setFocusTraversalIndex(int index) {
        super.setFocusTraversalIndex(index);
        getFormattedTextField().setFocusTraversalIndex(index);
    }
    
    
/**
 * #####################
 * # SDocumentListener #
 * #####################    
 */
    /**
     * @see SDocumentListener#insertUpdate()
     */
    public void insertUpdate(SDocumentEvent e) {}
    
    /**
     * @see SDocumentListener#removeUpdate()
     */
    public void removeUpdate(SDocumentEvent e){}
    
    /**
     * @see SDocumentListener#changedUpdate()
     */
    public void changedUpdate(SDocumentEvent e){
        setDate( (Date)getFormattedTextField().getValue() );
    }

/**
 * #########################
 * # LowLevelEventListener #  
 * #########################
 */      
    
    /**
     * @see LowLevelEventListener#isEpochCheckEnabled()
     */
    public boolean isEpochCheckEnabled() {
        return epochCheckEnabled;
    }

    /**
     * @see LowLevelEventListener#setEpochCheckEnabled()
     */
    public void setEpochCheckEnabled(boolean epochCheckEnabled) {
        this.epochCheckEnabled = epochCheckEnabled;
    }
    
    /**
     * @see LowLevelEventListener#fireIntermediateEvents()
     */    
    public void fireIntermediateEvents() {     
    }
    
    /**
     * @see LowLevelEventListener#processLowLevelEvent(String action, String[] values)
     */
    public void processLowLevelEvent(String action, String[] values) {     
        processKeyEvents(values);
        if (action.endsWith("_keystroke"))
            return;

        SimpleDateFormat dateFormat  = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setTimeZone( getTimeZone() );
        
        try {
            setDate( dateFormat.parse( values[0] ) );
        } catch ( Exception e ) {
            getFormattedTextField().processLowLevelEvent(action, values );
        }
        
        fireActionEvents();
        
    }
    
}