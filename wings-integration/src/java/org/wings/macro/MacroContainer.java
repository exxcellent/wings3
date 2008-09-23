package org.wings.macro;

import java.util.Collection;
import java.util.ArrayList;

/**
 * <code>DefaultMacro<code>.
 * <p/>
 * User: rrd
 * Date: 13.08.2007
 * Time: 11:08:07
 *
 * @author rrd
 * @version $Id
 */
public class MacroContainer implements Macro {

    private MacroContext context;

    private Collection<Instruction> instructions = new ArrayList<Instruction>();

    public MacroContext getContext() {
    	return context;
    }
    
    public void setContext(MacroContext context) {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    /**
     * {@inheritDoc}
     */
    public void execute() {
        for (Instruction instruction : instructions) {
            instruction.execute(context);
        }
    }
}
