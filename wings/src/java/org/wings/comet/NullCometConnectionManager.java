package org.wings.comet;

/**
 * A CometConnectionManager which does nothing to restrict the number of open
 * HangingGet requests per browser. It follows the Null Object Pattern.
 */
public class NullCometConnectionManager extends CometConnectionManager {

	@Override
	boolean addHangingGet() {
		return true;
	}

	@Override
	boolean canAddHangingGet() {
		return true;
	}

	@Override
	void removeHangingGet() {
	}
}
