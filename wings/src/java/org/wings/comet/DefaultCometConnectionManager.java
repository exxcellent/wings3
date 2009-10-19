package org.wings.comet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manages the number of open HangingGet requests per browser via a shared object in JNDI.
 */
class DefaultCometConnectionManager extends CometConnectionManager {

    private Context jndiContext;
    
    public DefaultCometConnectionManager() {
        try {
            jndiContext = new InitialContext();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public boolean canAddHangingGet() {
		int hGetCount = readHGetCount();
		log.debug("canAddHangingGet: hGetCount=" + hGetCount);
		return hGetCount < getMaxHangingGetCount();
	}

	public boolean addHangingGet() {
		int hGetCount = readHGetCount();
		log.debug("addHangingGet: hGetCount before=" + hGetCount);
		return hGetCount < getMaxHangingGetCount() && saveHGetCount(++hGetCount);
	}

	public void removeHangingGet() {
		int hGetCount = readHGetCount();
		log.debug("removeHangingGet: hGetCount before=" + hGetCount);
		if (hGetCount > 0)
			saveHGetCount(--hGetCount);
	}

   int readHGetCount() {
        try {
            return (Integer) jndiContext.lookup(NAME + getBrowserId());
        } catch (NamingException e) {
        	return 0;
        }
    }
   
   private boolean saveHGetCount(int count) {
		try {
			jndiContext.rebind(NAME + getBrowserId(), count);
			return true;
		} catch (NamingException e) {
			return false;
		}
	}
}
