package org.wings.macro;

public class CtxPutMacro extends AbstractMacro {

    private String key;
    private String value;

    public CtxPutMacro(String instruction) {
        String[] tokens = instruction.split(",");
        key = tokens[0];
        if (tokens.length > 1) {
            value = tokens[1];
        }
    }

    /**
     * Store a value in the MacroContext
     */
    public void execute(MacroContext ctx) {
        ctx.put(key, value);
    }
}
