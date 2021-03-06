package org.wings.macro;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.SComponent;
import org.wings.plaf.ComponentCG;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <code>MethodCallMacro<code>.
 * <p/>
 * User: rrd
 * Date: 13.08.2007
 * Time: 13:56:04
 *
 * @author rrd
 * @version $Id
 */
public class MethodCallMacro extends AbstractMacro {

    private static final Log LOG = LogFactory.getLog(MethodCallMacro.class);

    private boolean storeResultInContext = false;
    private String name;
    private String[] parameters = new String[0];

    public MethodCallMacro(String name, String instructions) {
        if (name != null && name.length() > 1 && name.startsWith("$")) {
            storeResultInContext = true;
            this.name = name.substring(1);
        } else {
            this.name = name;
        }
        if (instructions != null && !"".equals(instructions)) {
            this.parameters = instructions.split(",");
        }
    }

    public void execute(MacroContext ctx) {
        SComponent component = ctx.getComponent();

        // step 1: try to invoke method on the component
        boolean invoked = invokeMethodOnComponent(component, ctx);
        if (!invoked) {
            // step 2: try to invoke method on the component's cg
            invokeMethodOnCG(component.getCG(), ctx);
        }
    }

    private boolean invokeMethodOnComponent(SComponent component, MacroContext ctx) {
        Method[] methods = component.getClass().getMethods();
        for (Method method : methods) {
            if (isTargetMethod(method)) {
                try {
                    Object[] parametersResolved = new Object[parameters.length];
                    for (int ix = 0; ix < parameters.length; ix++) {
                        parametersResolved[ix] = resolveValue(ctx, parameters[ix]);
                    }

                    Object result = method.invoke(component, parametersResolved);
                    if (storeResultInContext) {
                        // store result in context
                        ctx.put(name, result); // null values allowed
                    } else if (result != null) {
                        // write result to output device
                        new StringInstruction(result.toString()).execute(ctx);
                    }
                    return true;
                }
                catch (IllegalAccessException e) {
                    LOG.error(e.getMessage(), e);
                }
                catch (InvocationTargetException e) {
                    LOG.error(e.getMessage(), e);
                }
                break;
            }
        }
        return false;
    }

    private boolean isTargetMethod(Method method) {
        MacroTag macroTag = method.getAnnotation(MacroTag.class);
        if (macroTag != null) {
            String declaredName = macroTag.name();
            if ("#default".equals(declaredName.trim()))
                declaredName = method.getName();
            if (name.equals(declaredName))
                return true;
        }
        return name.equals(method.getName()) && parameters.length == method.getParameterTypes().length;
    }

    private boolean invokeMethodOnCG(ComponentCG cg, MacroContext ctx) {
        Method[] methods = cg.getClass().getMethods();
        for (Method method : methods) {
            if (isTargetMethod(method) || getWriteMethodName(name).equals(method.getName())) {
                try {
                    Object[] parametersResolved = new Object[parameters.length + 1];
                    parametersResolved[0] = ctx;
                    for (int ix = 0; ix < parameters.length; ix++) {
                        parametersResolved[ix + 1] = resolveValue(ctx, parameters[ix]);
                    }

                    method.invoke(cg, parametersResolved);
                    return true;
                }
                catch (IllegalAccessException e) {
                    LOG.error(e.getMessage(), e);
                }
                catch (InvocationTargetException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    private static String getWriteMethodName(String methodName) {
        return "write" + methodName.substring(0, 1) + methodName.substring(1, methodName.length());
    }
}
