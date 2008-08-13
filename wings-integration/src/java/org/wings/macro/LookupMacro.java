package org.wings.macro;

public class LookupMacro extends AbstractMacro {

    private String[] params;

    public LookupMacro(String instructions) {
        params = instructions.split(",");
    }

    /**
     * Ask the LookupProvider to look up an object specified by the given set of parameters.
     * It is the LookupProvider's job to return a reasonable value, for example:
     * - if the object is a Date: use a fitting DateFormat
     * - if the object is a BigDecimal: use a fitting NumberFormat
     * - if the object is a SComponent: return the result of ComponentCG.write() (for example)
     */
    public void execute(MacroContext ctx) {
        if (params != null && ctx.getComponent() instanceof LookupProvider) {
            Object[] paramsResolved = new Object[params.length];
            for (int ix = 0; ix < params.length; ix++) {
                paramsResolved[ix] = resolveValue(ctx, params[ix]);
            }
            Object s = ((LookupProvider) ctx.getComponent()).lookup(paramsResolved);
            if (s != null) {
                new StringInstruction(s.toString()).execute(ctx);
            }
        }
    }
}
