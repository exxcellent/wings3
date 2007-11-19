package org.wings.macro;

import java.util.Collection;
import java.util.ArrayList;

/**
 * <code>AbstractMacro<code>.
 * <p/>
 * User: rrd
 * Date: 13.08.2007
 * Time: 15:04:43
 *
 * @author rrd
 * @version $Id
 */
public abstract class AbstractMacro implements Macro, Instruction {

    protected Collection<Instruction> instructions = new ArrayList<Instruction>();

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }
}
