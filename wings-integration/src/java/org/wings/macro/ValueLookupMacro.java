package org.wings.macro;

public class ValueLookupMacro extends AbstractMacro {

    private String key;

    public ValueLookupMacro(String key) {
        this.key = key;
    }

    /**
     * Ask the ValueLookupProvider to look up the object specified by the given key.
     * It is the ValueLookupProvider's job to return a reasonable value, for example:
     * - if the object is a Date: use a fitting DateFormat
     * - if the object is a BigDecimal: use a fitting NumberFormat
     * - if the object is a SComponent: return the result of ComponentCG.write() (for example)
     */
    public void execute(MacroContext ctx) {
        if (ctx.getComponent() instanceof ValueLookupProvider) {
            String s = ((ValueLookupProvider) ctx.getComponent()).lookup(key);
            if (s != null) {
                new StringInstruction(s).execute(ctx);
            }
        }
    }
}
