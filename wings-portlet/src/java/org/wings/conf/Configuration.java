package org.wings.conf;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * <code>Configuration<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 09:00:30
 *
 * @author rrd
 * @version $Id
 */
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.NONE)
public class Configuration {

//    @XmlElement(name = "cmsDetail", required = false)
//    private Collection<CMSConfiguration> cmsDetail = new ArrayList<CMSConfiguration>();
//
//    public Collection<CMSConfiguration> getCmsDetail() {
//        return cmsDetail;
//    }
//
//    public void setCmsDetail(Collection<CMSConfiguration> cmsDetail) {
//        this.cmsDetail = cmsDetail;
//    }

    @XmlElement(name = "cms-detail", required = true)
    private CmsDetail cmsDetail;

    public CmsDetail getCmsDetail() {
        return cmsDetail;
    }

    public void setCmsDetail(CmsDetail cmsDetail) {
        this.cmsDetail = cmsDetail;
    }
}
