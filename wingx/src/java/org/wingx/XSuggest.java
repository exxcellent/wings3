package org.wingx;

import java.util.List;
import java.util.Map;

import org.wings.STextField;
import org.wingx.plaf.SuggestCG;

/**
 * Enhanced STextField that supports the user input by displaying suggestions.
 * 
 * @author Christian Schyma, Stephan Schuster
 */
public class XSuggest extends STextField implements XSuggestDataSource {

    private static final long serialVersionUID = 8113454533790532315L;

    /**
     * Data source from which suggestions are generated
     */
    private XSuggestDataSource dataSource = null;

    private int maxCacheEntries = 0;
    private int maxResultsDisplayed = 10;
    private int minQueryLength = 1;
    private float queryDelay = 0.2f;
    private boolean autoHighlight = true;
    private boolean useShadow = false;
    private boolean forceSelection = false;
    private boolean typeAhead = false;
    private boolean allowBrowserAutocomplete = true;
    private boolean alwaysShowContainer = false;

    /**
     * Creates a text field that can display suggestions.
     */
    public XSuggest() {
    }

    /**
     * Creates a text field that can display suggestions.
     * 
     * @param text
     *            Initial content of the text field
     */
    public XSuggest(String text) {
        super(text);
    }

    /**
     * Set a new data source from which suggestions are generated
     * 
     * @param source
     */
    public void setDataSource(XSuggestDataSource source) {
        XSuggestDataSource oldVal = this.dataSource;
        this.dataSource = source;
        propertyChangeSupport.firePropertyChange("dataSource", oldVal,
                this.dataSource);
    }

    /**
     * Returns the currently used data source
     * 
     * @see #setDataSource(XSuggestDataSource source)
     */
    public XSuggestDataSource getDataSource() {
        return this.dataSource;
    }

    /**
     * Obtains the list of suggestions from the data source, if there is one.
     * 
     * @param lookupText
     *            The actual query processed against the suggestions set
     * @return suggestions List with matching map entries or null, if there is
     *         no data source
     */
    public List<Map.Entry<String, String>> generateSuggestions(String lookupText) {
        if (getDataSource() != null) {
            return getDataSource().generateSuggestions(lookupText);
        } else {
            return null;
        }
    }

    @Override
    public void processLowLevelEvent(String action, String[] values) {
        String value = values[0];
        if (value.startsWith("asifsaodgj:")) {
            String query = value.substring("asifsaodgj:".length());
            List<Map.Entry<String, String>> suggestions = generateSuggestions(query);
            update(((SuggestCG) getCG()).getSuggestionsUpdate(this, query,
                    suggestions));
        } else {
            super.processLowLevelEvent(action, values);
        }
    }

    public int getMaxCacheEntries() {
        return maxCacheEntries;
    }

    /**
     * The DataSource component can cache data in a local JavaScript array,
     * which is especially helpful for reducing network traffic to remote
     * servers. You can set the maxCacheEntries property to the number of
     * responses you want held in the cache. When this number is exceeded, the
     * oldest cached response will be dropped. The data in the cache is already
     * fully processed, extracted, parsed and ready to be returned to the
     * caller.
     * 
     * @param maxCacheEntries
     *            defaults to 0
     */
    public void setMaxCacheEntries(int maxCacheEntries) {
        int oldVal = this.maxCacheEntries;
        this.maxCacheEntries = maxCacheEntries;
        propertyChangeSupport.firePropertyChange("maxCacheEntries", oldVal,
                this.maxCacheEntries);
    }

    public int getMaxResultsDisplayed() {
        return maxResultsDisplayed;
    }

    /**
     * Sets the maximum number of elements that may populate the result popup.
     * 
     * @param maxResultsDisplayed
     *            defaults to 10
     */
    public void setMaxResultsDisplayed(int maxResultsDisplayed) {
        int oldVal = this.maxResultsDisplayed;
        this.maxResultsDisplayed = maxResultsDisplayed;
        propertyChangeSupport.firePropertyChange("maxResultsDisplayed", oldVal,
                this.maxResultsDisplayed);
    }

    public int getMinQueryLength() {
        return minQueryLength;
    }

    /**
     * By default, as soon as a user starts typing characters into the input
     * element, the AutoComplete control starts batching characters for a query
     * to the DataSource. You may increase how many characters the user must
     * type before triggering this batching, which can help reduce load on a
     * server, especially if the first few characters of the input string will
     * not produce meaningful query results. A value of 0 will enable null or
     * empty string queries, which is particularly useful in conjunction with
     * the Always Show Container feature. A negative number value will
     * effectively turn off the widget.
     * 
     * @param minQueryLength
     *            defaults to 1
     */
    public void setMinQueryLength(int minQueryLength) {
        int oldVal = this.minQueryLength;
        this.minQueryLength = minQueryLength;
        propertyChangeSupport.firePropertyChange("minQueryLength", oldVal,
                this.minQueryLength);
    }

    public float getQueryDelay() {
        return queryDelay;
    }

    /**
     * By default, AutoComplete batches user input and sends queries 0.1 seconds
     * from the last key input event. You may adjust this delay for optimum user
     * experience and/or server load. Keep in mind that this value only reflects
     * the delay before sending queries, and any delays in receiving query
     * results that may be caused by server or computational latency will not be
     * reflected in this value. This value must be a Number greater than 0.
     * 
     * @param queryDelay
     *            defaults to 0.2
     */
    public void setQueryDelay(float queryDelay) {
        float oldVal = this.queryDelay;
        this.queryDelay = queryDelay;
        propertyChangeSupport.firePropertyChange("queryDelay", oldVal,
                this.queryDelay);
    }

    public boolean isAutoHighlight() {
        return autoHighlight;
    }

    /**
     * By default, when the container populates with query results, the first
     * item in the list will be automatically highlighted for the user. Use the
     * following this method to disable this feature.
     * 
     * @param autoHighlight
     *            defaults to true
     */
    public void setAutoHighlight(boolean autoHighlight) {
        boolean oldVal = this.autoHighlight;
        this.autoHighlight = autoHighlight;
        propertyChangeSupport.firePropertyChange("autoHighlight", oldVal,
                this.autoHighlight);
    }

    public boolean isUseShadow() {
        return useShadow;
    }

    /**
     * Use this method if you would like the container element to have a
     * drop-shadow.
     * 
     * @param useShadow
     *            default to false
     */
    public void setUseShadow(boolean useShadow) {
        boolean oldVal = this.useShadow;
        this.useShadow = useShadow;
        propertyChangeSupport.firePropertyChange("useShadow", oldVal,
                this.useShadow);
    }

    public boolean isForceSelection() {
        return forceSelection;
    }

    /**
     * You can enable the force-selection feature to require your users to
     * select a result from the container, or else the input field is cleared of
     * whatever they have typed. If the user types a string that does not match
     * any query result, the input will be deleted from the field.
     * 
     * @param forceSelection
     *            defaults to false
     */
    public void setForceSelection(boolean forceSelection) {
        boolean oldVal = this.forceSelection;
        this.forceSelection = forceSelection;
        propertyChangeSupport.firePropertyChange("forceSelection", oldVal,
                this.forceSelection);
    }

    public boolean isTypeAhead() {
        return typeAhead;
    }

    /**
     * Enabling the type-ahead feature causes the user's input to be
     * automatically completed with the first query result in the container
     * list. The string in the input field is also pre-selected, so it can be
     * deleted as the user continues typing.
     * 
     * @param typeAhead
     *            defaults to false
     */
    public void setTypeAhead(boolean typeAhead) {
        boolean oldVal = this.typeAhead;
        this.typeAhead = typeAhead;
        propertyChangeSupport.firePropertyChange("typeAhead", oldVal,
                this.typeAhead);
    }

    public boolean isAllowBrowserAutocomplete() {
        return allowBrowserAutocomplete;
    }

    /**
     * Some browsers support a non-standard attribute on form elements called
     * "autocomplete". When "autocomplete" is set to "on", the browser provides
     * a built-in automatic completion mechanism � and it will cache the user
     * input for automatic display if the user uses the "Back" button to
     * navigate back to the page. In order for the YUI AutoComplete control to
     * perform properly, the built-in browser completion mechanism needs to be
     * suppressed. Therefore, the control sets the autocomplete attribute to
     * "off" when the user starts typing in in an AutoComplete input field.
     * However, the control will set the autocomplete attribute back to "on" if
     * the form is submitted, so that a user's input can be displayed, should
     * the "Back" button get clicked after form submission. This caching of user
     * input may not be desired for sensitive data such as credit card numbers.
     * Where you are dealing with sensitive user data, you should disable this
     * feature with this method.
     * 
     * @param allowBrowserAutocomplete
     *            defaults to true
     */
    public void setAllowBrowserAutocomplete(boolean allowBrowserAutocomplete) {
        boolean oldVal = this.allowBrowserAutocomplete;
        this.allowBrowserAutocomplete = allowBrowserAutocomplete;
        propertyChangeSupport.firePropertyChange("allowBrowserAutocomplete",
                oldVal, this.allowBrowserAutocomplete);
    }

    public boolean isAlwaysShowContainer() {
        return alwaysShowContainer;
    }

}
