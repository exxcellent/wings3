package org.wings.plaf;

import org.wings.macro.MacroContainer;

/**
 * <code>CmsCG<code>.
 * <p/>
 * User: rrd
 * Date: 13.08.2007
 * Time: 08:48:30
 *
 * @author rrd
 * @version $Id
 */
public interface IntegrationCG {

    /**
     * Set the macro to render the <code>SComponent</code>.
     *
     * @param macros The macro to render the component.
     */
    void setMacros(MacroContainer macros);
}
