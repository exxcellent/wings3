package org.wings.macro;

/**
 * <code>Step<code>.
 * <p/>
 * User: rrd
 * Date: 13.08.2007
 * Time: 10:52:17
 *
 * @author rrd
 * @version $Id
 */
public interface Instruction {

    void execute(MacroContext ctx);
}
