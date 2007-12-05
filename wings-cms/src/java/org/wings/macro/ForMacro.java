package org.wings.macro;

import org.mvel.MVEL;

/**
 * <code>ForMacro<code>.
 * <p/>
 * User: rrd
 * Date: 13.08.2007
 * Time: 11:00:33
 *
 * @author rrd
 * @version $Id
 */
public class ForMacro extends AbstractMacro {

    private String initialize;
    private String condition;
    private String statement;
    private String variable;

    public ForMacro(String instructions) {
        String[] instr = instructions.split(";");

        initialize = instr[0];
        condition = instr[1];
        statement = instr[2].replaceAll("\\+\\+", "+1").replaceAll("--", "-1");
    }

    private void initialize(MacroContext ctx) {
        String[] init = initialize.split("=");

        variable = init[0].trim();
        Object value = init[1].trim();

        try {
            value = Integer.parseInt((String) value);
        }
        catch (NumberFormatException e) {
            // do nothing
        }

        // noinspection unchecked
        ctx.put(variable, value);
    }

    private boolean condition(MacroContext ctx) {
        return MVEL.evalToBoolean(condition, ctx);
    }

    private void statement(MacroContext ctx) {
        Object result = MVEL.eval(statement, ctx);

        // noinspection unchecked
        ctx.put(variable, result);
    }

    /**
     * {@inheritDoc}
     */
    public void execute(MacroContext ctx) {

        for (initialize(ctx); condition(ctx); statement(ctx)) {
            for (Instruction instruction : instructions) {
                instruction.execute(ctx);
            }
        }
    }
}
