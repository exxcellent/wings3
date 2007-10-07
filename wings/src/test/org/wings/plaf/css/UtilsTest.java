package org.wings.plaf.css;

import junit.framework.TestCase;

public class UtilsTest extends TestCase {
	public void test_escapeJS() {
		assertEquals("foo\\\\\\\"bar", Utils.escapeJS("foo\\\"bar"));
		assertEquals("\\nfoo\\\\\\\"bar", Utils.escapeJS("\nfoo\\\"bar"));
		assertEquals("\\nfoo\\\\\\\"bar\\t", Utils.escapeJS("\nfoo\\\"bar\t"));
		// add more here ;)
	}
}
