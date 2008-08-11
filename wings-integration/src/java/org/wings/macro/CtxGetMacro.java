package org.wings.macro;

public class CtxGetMacro extends AbstractMacro {

    private String key;

    public CtxGetMacro(String instruction) {
        key = instruction;
    }

    /**
     * Get a value stored in the MacroContext
     */
    public void execute(MacroContext ctx) {
        Object o = ctx.get(key);
        if (o != null) {
            new StringInstruction(o.toString()).execute(ctx);
        }
    }
}
