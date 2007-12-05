package org.wings.conf;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * <code>StringToClassAdapter<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 08:55:49
 *
 * @author rrd
 * @version $Id
 */
public class StringToClassAdapter extends XmlAdapter<String, Class> {

    /**
     * {@inheritDoc}
     */
    public Class unmarshal(String s) throws Exception {
        return getClass().getClassLoader().loadClass(s);
    }

    /**
     * {@inheritDoc}
     */
    public String marshal(Class type) throws Exception {
        return type.getName();
    }
}
