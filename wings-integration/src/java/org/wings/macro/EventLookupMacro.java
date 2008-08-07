package org.wings.macro;

public class EventLookupMacro extends AbstractMacro {

    private String componentKey;
    private String eventValue;

    public EventLookupMacro(String instruction) {
        String[] tokens = instruction.split(",");
        componentKey = tokens[0];
        if (tokens.length > 1) {
            eventValue = tokens[1];
        } else {
            eventValue = "1"; // default value
        }
    }

    /**
     * Ask the EventLookupProvider to look up the component specified by the given componentKey.
     * If the found component has an AJAX request 'attached', the EventLookupProvider will return
     * the corresponding javascript code and use the given eventValue.
     */
    public void execute(MacroContext ctx) {
        if (ctx.getComponent() instanceof EventLookupProvider) {
            String ajaxRequestAsString = ((EventLookupProvider) ctx.getComponent()).lookup(componentKey, eventValue);
            if (ajaxRequestAsString != null) {
                new StringInstruction(ajaxRequestAsString).execute(ctx);
            }
        }
    }
}
