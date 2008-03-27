package org.wings.plaf.css.script;

import org.wings.script.ScriptListener;

public class OnHeadersLoadedScript
    implements ScriptListener
{
    String script;
    boolean wrapAsFunction;

    public OnHeadersLoadedScript(String script) {
        this(script, true);
    }

    public OnHeadersLoadedScript(String script, boolean wrapAsFunction) {
        this.script = script;
        this.wrapAsFunction = wrapAsFunction;
    }

    public String getEvent() {
        return null;
    }

    public String getCode() {
        return null;
    }

    public String getScript() {
        final StringBuilder output = new StringBuilder();

        output.append("wingS.global.onHeadersLoaded(");
        if (wrapAsFunction) output.append("function() {");
        output.append(script);
        if (wrapAsFunction) output.append("}");
        output.append(");");

        return output.toString();
    }

    public int getPriority() {
        return DEFAULT_PRIORITY;
    }
}
