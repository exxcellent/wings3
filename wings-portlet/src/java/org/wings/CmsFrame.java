package org.wings;

import org.wings.session.SessionManager;
import org.wings.conf.CmsDetail;
import org.wings.conf.Configuration;
import org.wings.adapter.AbstractCmsAdapter;
import org.wings.adapter.CmsAdapter;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * <code>CMSFrame<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 09:27:35
 *
 * @author rrd
 * @version $Id
 */
public class CmsFrame extends SFrame {

    CmsLayout layout = new CmsLayout();

    /**
     * Default constructor
     */
    public CmsFrame() {
        setContentPane(new CmsForm());
        getContentPane().setLayout(layout);

        Configuration cfg = getConfiguration();

        CmsDetail cmsConfig = cfg.getCmsDetail();

        CmsAdapter adapter = createAdapter(cmsConfig);
        adapter.setFrame(this);
        adapter.setConfiguration(cmsConfig);

        SessionManager.getSession().setResourceMapper(adapter);
    }

    private Configuration getConfiguration() {
        try {
            JAXBContext ctx = JAXBContext.newInstance(Configuration.class);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            return (Configuration) unmarshaller.unmarshal(getClass().getClassLoader().getResourceAsStream("wings-2-cms.xml"));
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CmsAdapter createAdapter(CmsDetail cfg) {
        Class type = cfg.getAdapter();

        assert type != null : "Adapter class cannot be null.";

        try {
            if (AbstractCmsAdapter.class.isAssignableFrom(type)) {
                Constructor constructor = type.getConstructor(SFrame.class, STemplateLayout.class, CmsDetail.class);
                return (CmsAdapter) constructor.newInstance(this, layout, cfg);
            }
            else {
                return (CmsAdapter) type.newInstance();
            }
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public final SComponent add(SComponent c) {
        throw new UnsupportedOperationException("This method won't be supported by CmsFrame. Use add(SComponent c, Object constraint) instead.");
    }

    @Override
    public void add(SComponent c, Object constraint) {
        getContentPane().add(c, constraint);
    }

    @Override
    public SComponent add(SComponent c, int index) {
        throw new UnsupportedOperationException("This method won't be supported by CmsFrame. Use add(SComponent c, Object constraint) instead.");
    }

    @Override
    public void add(SComponent c, Object constraint, int index) {
        throw new UnsupportedOperationException("This method won't be supported by CmsFrame. Use add(SComponent c, Object constraint) instead.");
    }
}
