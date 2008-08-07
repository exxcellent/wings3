package org.wings.macro;

public class ValueLookupMacro extends AbstractMacro {

    private String lookupKey;

    public ValueLookupMacro(String lookupKey) {
        this.lookupKey = lookupKey;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(MacroContext ctx) {
        if (ctx.getComponent() instanceof ValueLookupProvider) {
            Object o = ((ValueLookupProvider) ctx.getComponent()).lookup(lookupKey);
            if (o != null) {
                new StringInstruction(o.toString()).execute(ctx);
            }
        }
    }
}
