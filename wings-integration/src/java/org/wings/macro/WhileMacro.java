package org.wings.macro;

import org.mvel.MVEL;

/**
 * <code>WhileMacro<code>.
 * <p/>
 * User: rrd
 * Date: 14.08.2007
 * Time: 08:52:44
 *
 * @author rrd
 * @version $Id
 */
public class WhileMacro extends AbstractMacro {

    private String condition;

    public WhileMacro(String condition) {
        this.condition = condition;
    }

    private boolean condition(MacroContext ctx) {
        return MVEL.evalToBoolean(condition, ctx);
    }

    public void execute(MacroContext ctx) {
        while (condition(ctx)) {
            for (Instruction instruction : instructions) {
                instruction.execute(ctx);
            }
        }
    }
}
