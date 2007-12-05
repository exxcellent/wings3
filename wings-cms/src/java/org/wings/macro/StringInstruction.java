package org.wings.macro;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * <code>CharacterStep<code>.
 * <p/>
 * User: rrd
 * Date: 13.08.2007
 * Time: 10:55:01
 *
 * @author rrd
 * @version $Id
 */
public class StringInstruction implements Instruction {

    private static final Log LOG = LogFactory.getLog(StringInstruction.class);

    // Contains the characters which get printed to the device.
    private String characters;

    /**
     * Creates a new character step.
     *
     * @param characters The characters which will be print to the
     *                   device when this step will be executed.
     */
    public StringInstruction(String characters) {
        this.characters = characters;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(MacroContext ctx) {
        try {
            ctx.getDevice().print(characters);
        }
        catch (IOException e) {
            LOG.error("Couldn't print characters to device.", e);
        }
    }
}
