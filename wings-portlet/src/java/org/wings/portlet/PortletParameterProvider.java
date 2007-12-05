package org.wings.portlet;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.SPortletAnchor;
import org.wings.session.Session;
import org.wings.session.SessionManager;

/**
 * 
 * This class provides the parameters as events from the RenderRequest set by a
 * {@link SPortletAnchor} and the parameters set in the portlet. The parameters
 * set in the portlet must be encoded with the {@link PortletParameterCodec}.
 * The SPortletAnchor encodes the parameters automatically
 * 
 * @author <a href="mailto:marc.musch@mercatis.com">Marc Musch</a>
 * 
 */
public class PortletParameterProvider {

	private final transient static Log log = LogFactory
			.getLog(PortletParameterProvider.class);

	/**
	 * list for the registered listeners
	 */
	private EventListenerList listeners = new EventListenerList();

	private PortletParameterProvider() {
	}

	/**
	 * Use this method to get an instance of this class, the constructir is
	 * hidden.
	 * 
	 * @return Instance of this class
	 */
	public static PortletParameterProvider getInstance() {

		Session session = SessionManager.getSession();

		PortletParameterProvider ppp = (PortletParameterProvider) session
				.getProperty(Const.WINGS_SESSION_PROPERTY_PORTLET_PARAMETER_PROVIDER);
		if (ppp != null) {
			return ppp;
		} else {
			PortletParameterProvider newPpp = new PortletParameterProvider();
			session.setProperty(
					Const.WINGS_SESSION_PROPERTY_PORTLET_PARAMETER_PROVIDER,
					newPpp);
			return newPpp;
		}
	}

	/**
	 * Adds a new listner for arriving portlet parameters
	 * 
	 * @param listener -
	 *            the listner for these events
	 */
	public void addPortletParameterListener(PortletParameterListener listener) {
		listeners.add(PortletParameterListener.class, listener);
	}

	/**
	 * Forces the updating of the portlet parameters
	 */
	public void updateParameters() {
		Session session = SessionManager.getSession();
		Map<String, String[]> portletParameters = (Map<String, String[]>) session
				.getProperty(Const.WINGS_SESSION_PROPERTY_PORTLET_PARAMETER_MAP);
		if (log.isDebugEnabled()) {
			if (portletParameters != null) {
				Set names = portletParameters.keySet();
				Iterator iter = names.iterator();
				log.debug("WingS-Portlet-Bridge: Portlet Parameters "
						+ "stored in Session and deliverd as events: ");
				while (iter.hasNext()) {
					String name = (String) iter.next();
					log.debug(name + ", ");
				}
			}
		}
		notifyNewPortletParameters(new PortletParameterEvent(this,
				portletParameters));
	}

	/**
	 * Notifies the listeners
	 * 
	 * @param e -
	 *            the event to deliver
	 */
	private synchronized void notifyNewPortletParameters(PortletParameterEvent e) {
		for (PortletParameterListener l : listeners
				.getListeners(PortletParameterListener.class))
			l.newPortletParameters(e);
	}

}
