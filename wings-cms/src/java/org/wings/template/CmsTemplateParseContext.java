package org.wings.template;

import org.wings.STemplateLayout;
import org.wings.SComponent;
import org.wings.io.Device;
import org.wings.io.DeviceOutputStream;
import org.wings.template.parser.ParseContext;

import java.io.OutputStream;
import java.util.*;

/**
 * <code>CmsTemplateParseContext<code>.
 * <p/>
 * User: raedler
 * Date: 03.12.2007
 * Time: 13:23:45
 *
 * @author raedler
 * @version $Id
 */
public class CmsTemplateParseContext implements ParseContext {

    private final OutputStream myOut;
    private final Device sink;
    private final STemplateLayout layout;
    private final Set<String> containedComponents = new HashSet<String>();
    private final Map<String,Map<String,String>> componentProperties = new HashMap<String, Map<String, String>>();

    public CmsTemplateParseContext(final Device sink, STemplateLayout layout) {
        this.sink = sink;
        this.layout = layout;
        myOut = new DeviceOutputStream(sink);
    }

    public OutputStream getOutputStream() {
        return myOut;
    }

    public void startTag(int number) {
    }

    public void doneTag(int number) {
    }

    /*
     * important for the template: the components write to this sink
     */
    public Device getDevice() {
        return sink;
    }

    public SComponent getComponent(String name) {
        return layout.getComponent(name);
    }

    public SComponent[] getComponents() {
        return layout.getContainer().getComponents();
    }

    public void addContainedComponent(String component) {
        containedComponents.add(component);
    }

    public Set<String> getContainedComponents() {
        return containedComponents;
    }

    public void setProperties(String name, Map<String,String> properties) {
        componentProperties.put(name, properties);
    }

    public Map<String, Map<String, String>> getComponentProperties() {
        return componentProperties;
    }
}
