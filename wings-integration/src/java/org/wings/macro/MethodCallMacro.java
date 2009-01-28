package org.wings.macro;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.SComponent;

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

    private String name;
    private String[] methodParameters = new String[0];

    public MethodCallMacro(String name, String instructions) {
        this.name = name;
        if (instructions != null && !"".equals(instructions)) {
            methodParameters = instructions.split(",");
        }
    }

    public void execute(MacroContext ctx) {
        invokeMethodOnComponent(ctx);
    }

    private Object[] resolveMethodParameters(MacroContext ctx) {
        Object[] paramsResolved = new Object[methodParameters.length];
        for (int ix = 0; ix < methodParameters.length; ix++) {
            paramsResolved[ix] = resolveValue(ctx, methodParameters[ix]);
        }
        return paramsResolved;
    }

    private void invokeMethodOnComponent(MacroContext ctx) {
        SComponent component = ctx.getComponent();
        if (component != null) {
            Method[] methods = component.getClass().getMethods();
            for (Method method : methods) {
                if (name.equals(method.getName())) {
                    try {
                        Object result = method.invoke(component, resolveMethodParameters(ctx));
                        ctx.put(name, result); // null values allowed!
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
        }
    }

//    public void execute(MacroContext ctx) {
//        SComponent component = ctx.getComponent();
//
//        // step 1: try to invoke method on the component's cg
//        invokeMethodOnCG(component.getCG(), ctx);
//
//        // step 2: try to invoke method on the component itself
//        invokeMethodOnComponent(component, ctx);
//    }
//
//    private void invokeMethodOnCG(Object target, MacroContext ctx) {
//        Object[] parameters = new Object[methodParameters.length + 1];
//        parameters[0] = ctx;
//        for (int i = 0; i < methodParameters.length; i++) {
//            parameters[i + 1] = ctx.get(methodParameters[i]);
//            if (parameters[i + 1] == null)
//            parameters[i + 1] = resolveValue(ctx, methodParameters[i]);
//        }
//
//        Method[] methods = target.getClass().getMethods();
//        for (Method method : methods) {
//            MacroTag macroTag = method.getAnnotation(MacroTag.class);
//            String methodName = method.getName();
//
//            if ((macroTag != null && name.equals(macroTag.name())) ||
//                    name.equals(methodName) ||
//                    getWriteMethodName(name).equals(methodName)) {
//                try {
//                    method.invoke(target, parameters);
//                }
//                catch (IllegalAccessException e) {
//                    LOG.error(e.getMessage(), e);
//                }
//                catch (InvocationTargetException e) {
//                    LOG.error(e.getMessage(), e);
//                }
//            }
//        }
//    }
//
//    private String getWriteMethodName(String name) {
//        return "write" + name.substring(0, 1) + name.substring(1, name.length());
//    }

}
