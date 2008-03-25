package org.wings.test;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.wings.LowLevelEventListener;
import org.wings.RequestURL;
import org.wings.SComponent;
import org.wings.session.LowLevelEventDispatcher;
import org.wings.session.Session;
import org.wings.session.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;


/**
 * Generates and initialize a Wings session with a dummy request to allow unit testing
 *
 * @author Rene Doering
 */
public class WingsTestCase extends Session{
    private final static Log log = LogFactory.getLog(WingsTestCase.class);

    Session session;

    /**
     * Retrofitting the test case with a suite() method is necessary
     * to run JUnit4 tests on a version of Ant prior to 1.7.
     * If you are using such a version, you must place the suite() method in your test class,
     * returning a JUnit4TestAdapter with you test class name!
     */
    /*public static Test suite(){
        return new JUnit4TestAdapter(TestComponents.class);
    } */

    /**
     * Initializes a Wings session with a dummy request for each test.
     * @see DummyHttpServletRequest
     * This mehtod will run before all @Before methods of the subclasses.
     */
    @Before
    public final void init(){
        session = new Session();
        SessionManager.setSession(session);
        HttpServletRequest request = new DummyHttpServletRequest();
        try {
            session.init(request);
        } catch (ServletException e) {
            log.debug("ServletException occured during session initialization. " +
                    "This is possibly due to an error in the dummy DummyHttpServletRequest class.");
            e.printStackTrace();
        }

        /*
         * property required to initialize a SFrame correctly
         */
        RequestURL requestURL = new RequestURL();
        session.setProperty("request.url", requestURL);

    }
    

    /**
     * Destroys the session after each test.
     */
    @After
    public final void finalize(){
        SessionManager.removeSession();
        session = null;
    }                           

    
    /**
     * Starts the dispatching process for a given component like a http request.
     * @param component component that is affected
     * @param values new value(s) for the components attribute. Which attribute is affected is defined in the according processLowLevelEvent method
     */
    public void sendEvent(SComponent component, String[] values){
        LowLevelEventDispatcher dispatcher = session.getDispatcher();

        String name = component.getName();

        /*
         * A LowLevelEventListener is required for the dispatch method
         */
        LowLevelEventListener componentThatImplementsLowLevelEventListener;
        if(component instanceof LowLevelEventListener)
            componentThatImplementsLowLevelEventListener = (LowLevelEventListener) component;
        else
            throw new IllegalArgumentException(component.getClass().getName() + " is not a valid argument. " +
                    "Please use a class that implements the LowLevelEventListener interface.");
        dispatcher.addLowLevelEventListener(componentThatImplementsLowLevelEventListener, name);

        dispatcher.dispatch(name, values);

        componentThatImplementsLowLevelEventListener.fireIntermediateEvents();

        componentThatImplementsLowLevelEventListener.fireFinalEvents();

    }
}