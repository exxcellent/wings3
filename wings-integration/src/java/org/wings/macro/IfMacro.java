package org.wings.macro;

import org.mvel.MVEL;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <code>IfMacro<code>.
 * <p/>
 * User: rrd
 * Date: 13.08.2007
 * Time: 16:56:42
 *
 * @author rrd
 * @version $Id
 */
public class IfMacro extends AbstractMacro {

    private String condition;

    private Collection<IfMacro> elseIfMacros = new ArrayList<IfMacro>();
    private AbstractMacro elseMacro;

    public IfMacro(String instruction) {
        if (instruction != null) {
            condition = instruction.replace('$', ' ').trim();
        }
    }

    private boolean condition(MacroContext ctx) {
        return MVEL.evalToBoolean(condition, ctx);
    }

    public void execute(MacroContext ctx) {

        if (condition(ctx)) {
            for (Instruction instruction : instructions) {
                instruction.execute(ctx);
            }
            return;
        }

        for (IfMacro macro : elseIfMacros) {
            if (macro.condition(ctx)) {
                macro.execute(ctx);
                return;
            }
        }

        if (elseMacro != null) {
            elseMacro.execute(ctx);
        }
    }
}
