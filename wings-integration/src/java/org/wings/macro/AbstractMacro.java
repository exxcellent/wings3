package org.wings.macro;

import org.mvel2.MVEL;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <code>AbstractMacro<code>.
 * <p/>
 * User: rrd
 * Date: 13.08.2007
 * Time: 15:04:43
 *
 * @author rrd
 * @version $Id
 */
public abstract class AbstractMacro implements Macro, Instruction {

	protected Collection<Instruction> instructions = new ArrayList<Instruction>();

	private static final String CAST_INT = "int";
	private static final String CAST_DOUBLE = "double";
	private static final String CAST_FLOAT = "float";
	private static final String CAST_LONG = "long";
	private static final String CAST_SHORT = "short";
	private static final String CAST_BYTE = "byte";
	
	
	public void addInstruction(Instruction instruction) {
		instructions.add(instruction);
	}

	protected static Object resolveValue(MacroContext ctx, String expr) {
		if (expr != null) {
			if (expr.length() > 1) {
				if (expr.charAt(0) == '$') {
					try {
						return MVEL.eval(expr.substring(1), ctx);
					}
					catch (Exception e) {
						return expr;
					}
				}
			}
			return resolveCast(expr.trim());
		}
		
		return expr;
	}

	/**
	 * @param expr
	 * @return
	 */
	protected static Object resolveCast(String expr) {
    	if (expr.startsWith(CAST_INT)) {
    		return Integer.parseInt(expr.substring(CAST_INT.length()).trim());
    	}
    	else if (expr.startsWith(CAST_DOUBLE)) {
    		return Double.parseDouble(expr.substring(CAST_DOUBLE.length()).trim());
    	}
    	else if (expr.startsWith(CAST_FLOAT)) {
    		return Float.parseFloat(expr.substring(CAST_FLOAT.length()).trim());
    	}
    	else if (expr.startsWith(CAST_LONG)) {
    		return Long.parseLong(expr.substring(CAST_LONG.length()).trim());
    	}
    	else if (expr.startsWith(CAST_SHORT)) {
    		return Short.parseShort(expr.substring(CAST_SHORT.length()).trim());
    	}
    	else if (expr.startsWith(CAST_BYTE)) {
    		return Byte.parseByte(expr.substring(CAST_BYTE.length()).trim());
    	}
    	return expr;
    }
}
