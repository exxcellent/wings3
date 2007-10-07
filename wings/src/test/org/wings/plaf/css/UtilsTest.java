package org.wings.plaf.css;

import junit.framework.TestCase;

public class UtilsTest extends TestCase {
	public void test_escapeJS() {
		assertEquals("foo", Utils.escapeJS("foo"));
		assertEquals("\\\"", Utils.escapeJS("\""));
		// add more here ;)
	}
}
