package org.wings.macro;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mvel.MVEL;

public class GetMacro extends AbstractMacro {

    private static final Log LOG = LogFactory.getLog(GetMacro.class);

    private String instruction;

    private String reference;

    public GetMacro(String instruction) {
        this.instruction = instruction;
        if (instruction != null && instruction.length() > 0 && instruction.startsWith("$")) {
            reference = instruction.substring(1);
        } else {
            LOG.debug("Invalid reference");
        }
    }

    /**
     * Get a value stored in the MacroContext
     */
    public void execute(MacroContext ctx) {
        Object o = null;
        try {
            o = MVEL.eval(reference, ctx);
        } catch (Exception e) {
            LOG.info("Evaluation failed: " + instruction);
        }

        if (o != null) {
            new StringInstruction(o.toString()).execute(ctx);
        }
    }
}
