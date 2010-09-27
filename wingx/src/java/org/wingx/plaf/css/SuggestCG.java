package org.wingx.plaf.css;

import java.io.IOException;
import java.util.*;

import org.wings.*;
import org.wings.header.SessionHeaders;
import org.wings.header.Header;
import org.wings.event.SParentFrameEvent;
import org.wings.io.Device;
import org.wings.plaf.css.*;
import org.wings.plaf.css.script.OnHeadersLoadedScript;
import org.wings.plaf.Update;
import org.wings.session.ScriptManager;
import org.wingx.XSuggest;

/**
 * @author Christian Schyma, Stephan Schuster
 */
public class SuggestCG extends TextFieldCG implements org.wingx.plaf.SuggestCG {
    
    private static final long serialVersionUID = 8395625026802022216L;
    
    protected final List<Header> headers = new ArrayList<Header>();

    public SuggestCG() {
        headers.add(Utils.createExternalizedJSHeaderFromProperty(Utils.JS_YUI_DATASOURCE));
        headers.add(Utils.createExternalizedJSHeaderFromProperty(Utils.JS_YUI_AUTOCOMPLETE));
        headers.add(Utils.createExternalizedCSSHeader("org/wingx/suggest/suggest.css"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/suggest/suggest.js"));
    }

    public void installCG(final SComponent comp) {
        super.installCG(comp);
        SessionHeaders.getInstance().registerHeaders(headers);
    }

    public void uninstallCG(SComponent component) {
        super.uninstallCG(component);
        SessionHeaders.getInstance().deregisterHeaders(headers);
    }

    protected void onChangeSubmitListener(STextField textField) {
    }
    
    private boolean appendConfigPropery(StringBuilder sb, String name, Object value, Object defaultValue, boolean isFirst) {
        boolean nextIsFirst = isFirst;
        if (!value.equals(defaultValue)) {
            if (isFirst) {
                nextIsFirst = false;
            } else {
                sb.append(", ");
            }
            sb.append(name).append(":").append(value);
        }
        return nextIsFirst;
    }

    protected void printPostInput(Device device, STextField textField) throws IOException {
        XSuggest xSuggest = (XSuggest) textField;
        
        device.print("<div id=\"" + xSuggest.getName() + "_popup\"></div>");
        StringBuilder builder = STRING_BUILDER.get();
        builder.setLength(0);

        String name = textField.getName();
        builder.append("xs_datasource_").append(name).append(" = new wingS.suggest.DataSource(\"").append(name).append("\", {");
        appendConfigPropery(builder, "maxCacheEntries", xSuggest.getMaxCacheEntries(), 0, true);
        builder.append("});\n");
        builder.append("xs_").append(name).append(" = new wingS.suggest.XSuggest(\"")
               .append(name).append("\", \"").append(name).append("_popup\", xs_datasource_").append(name).append(", {");
        boolean isFirst = true;
        isFirst = appendConfigPropery(builder, "maxResultsDisplayed", xSuggest.getMaxResultsDisplayed(), 10, isFirst);
        isFirst = appendConfigPropery(builder, "minQueryLength", xSuggest.getMinQueryLength(), 1, isFirst);
        isFirst = appendConfigPropery(builder, "queryDelay", xSuggest.getQueryDelay(), 0.2f, isFirst);
        isFirst = appendConfigPropery(builder, "autoHighlight", xSuggest.isAutoHighlight(), true, isFirst);
        isFirst = appendConfigPropery(builder, "useShadow", xSuggest.isUseShadow(), false, isFirst);
        isFirst = appendConfigPropery(builder, "forceSelection", xSuggest.isForceSelection(), false, isFirst);
        isFirst = appendConfigPropery(builder, "typeAhead", xSuggest.isTypeAhead(), false, isFirst);
        isFirst = appendConfigPropery(builder, "allowBrowserAutocomplete", xSuggest.isAllowBrowserAutocomplete(), true, isFirst);
        isFirst = appendConfigPropery(builder, "alwaysShowContainer", xSuggest.isAlwaysShowContainer(), false, isFirst);
        builder.append("});\n");
        
        ScriptManager.getInstance().addScriptListener(new OnHeadersLoadedScript(builder.toString()));
    }

    public void parentFrameAdded(SParentFrameEvent e) {
        SessionHeaders.getInstance().registerHeaders(headers);
    }

    public void parentFrameRemoved(SParentFrameEvent e) {
        SessionHeaders.getInstance().deregisterHeaders(headers);
    }

    public Update getSuggestionsUpdate(XSuggest suggest, String text, List<Map.Entry<String, String>> suggestions) {
        return new SuggestUpdate(suggest, text, suggestions);
    }

    protected class SuggestUpdate extends AbstractUpdate {
        
        private List<Map.Entry<String,String>> suggestions;

        public SuggestUpdate(SComponent component, String text, List<Map.Entry<String, String>> suggestions) {
            super(component);
            this.suggestions = suggestions;
        }

        public int getPriority() {
            return 0;
        }

        public Handler getHandler() {
            List<Renderable> keyValuePairs = new ArrayList<Renderable>(suggestions.size());
            for (Map.Entry<String, String> suggestion : suggestions) {
                List<String> keyValuePair = new ArrayList<String>(2);
                keyValuePair.add(suggestion.getKey());
            keyValuePair.add(suggestion.getValue());
                keyValuePairs.add(Utils.listToJsArray(keyValuePair));
            }
            UpdateHandler handler = new UpdateHandler("suggest");
            handler.addParameter(component.getName());
            handler.addParameter(Utils.listToJsArray(keyValuePairs));
            return handler;
        }
    }
}
