package org.wings.macro;

/**
 * <code>MacroHandler<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 16:03:34
 *
 * @author rrd
 * @version $Id
 */
public interface MacroProcessor {
    public MacroContainer buildMacro(String macroTemplate) throws Exception;
}
