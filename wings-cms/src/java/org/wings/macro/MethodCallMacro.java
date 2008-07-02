package org.wings.macro;

import org.wings.SComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mvel.MVEL;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * <code>SimpleMacro<code>.
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
    private String[] instr = new String[0];

    public MethodCallMacro(String name, String instructions) {
        this.name = name;

        if (instructions != null && !"".equals(instructions)) {
            instr = instructions.split(",");
        }
    }

    public void execute(MacroContext ctx) {
        SComponent component = ctx.getComponent();
//        if (component instanceof CmsForm) {
//            component = ((CmsForm) component).getComponent();
//        }

        invokeMethod(component.getCG(), ctx);
    }

    private void invokeMethod(Object target, MacroContext ctx) {
        Object[] parameters = new Object[instr.length + 1];
        parameters[0] = ctx;
        for (int i = 0; i < instr.length; i++) {
            parameters[i + 1] = ctx.get(instr[i]);
            if (parameters[i + 1] == null)
            parameters[i + 1] = MVEL.eval(instr[i], ctx);
        }

        Method[] methods = target.getClass().getMethods();
        for (Method method : methods) {
            MacroTag macroTag = method.getAnnotation(MacroTag.class);
            String methodName = method.getName();

            if ((macroTag != null && name.equals(macroTag.name())) ||
                    name.equals(methodName) ||
                    getWriteMethodName(name).equals(methodName)) {
                try {
                    method.invoke(target, parameters);
                }
                catch (IllegalAccessException e) {
                    LOG.error(e.getMessage(), e);
                }
                catch (InvocationTargetException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    private String getWriteMethodName(String name) {
        return "write" + name.substring(0, 1) + name.substring(1, name.length());
    }
}
