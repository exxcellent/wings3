package org.wings.macro;

public class LookupSetMacro extends AbstractMacro {

    private String[] params;

    public LookupSetMacro(String instructions) {
        params = instructions.split(",");
    }

    /**
     * Ask the LookupProvider to look up an object specified by the given set of parameters.
     * Store the result in the macro context.
     */
    public void execute(MacroContext ctx) {
        if (params != null && ctx.getComponent() instanceof LookupProvider) {
            Object[] paramsResolved = new Object[params.length];
            for (int ix = 0; ix < params.length; ix++) {
                paramsResolved[ix] = resolveValue(ctx, params[ix]);
            }
            Object key = paramsResolved[0];
            Object value = ((LookupProvider) ctx.getComponent()).lookup(paramsResolved);
            ctx.put(key, value);
        }
    }
}
