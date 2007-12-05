package org.wings.conf;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <code>Configuration<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 08:52:06
 *
 * @author rrd
 * @version $Id
 */
@XmlType(name = "cms-detail")
@XmlAccessorType(XmlAccessType.NONE)
public class CmsDetail {

    public static final String DEFAULT_PROTOCOL = "http";

    public static final String DEFAULT_SERVER = "localhost";

    public static final int DEFAULT_PORT = 80;

    @XmlAttribute(name = "adapter", required = true)
    @XmlJavaTypeAdapter(StringToClassAdapter.class)
    private Class adapter;

    @XmlElement(name = "protocol", required = false, defaultValue = "http")
    private String protocol = DEFAULT_PROTOCOL;

    @XmlElement(name = "server", required = false, defaultValue = "localhost")
    private String server = DEFAULT_SERVER;

    @XmlElement(name = "port", required = false, defaultValue = "80")
    private int port = DEFAULT_PORT;

    @XmlElement(name = "base-path", required = true)
    private String basePath;

    public Class getAdapter() {
        return adapter;
    }

    public void setAdapter(Class adapter) {
        this.adapter = adapter;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getServerPath() {
        StringBuilder serverPath = new StringBuilder();
        serverPath.append(protocol)
                .append("://")
                .append(server);
        if (port != 80) {
            serverPath.append(":")
                    .append(port);
        }
        serverPath.append("/")
                .append(basePath);
        
        return serverPath.toString();
    }

    /*
    public String getServerPath() {
        StringBuilder serverPath = new StringBuilder();
        serverPath.append(protocol)
                .append("://")
                .append(server);
        if (port != 80) {
            serverPath.append(":")
                    .append(port);
        }

        return serverPath.toString();
    }
    */
}
