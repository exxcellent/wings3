package org.wings.macro.impl;

import java.io.Reader;
import java.io.StringReader;

import org.wings.macro.*;

/**
 * <code>VelocityMacroHandler<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 16:03:34
 *
 * @author rrd
 * @version $Id
 */
public class VelocityMacroProcessor implements MacroProcessor {

    private static VelocityMacroProcessor sharedInstance;

    protected VelocityMacroProcessor() {

    }

    public static VelocityMacroProcessor getInstance() {
        if (sharedInstance == null) {
            synchronized (VelocityMacroProcessor.class) {
                if (sharedInstance == null)
                    sharedInstance = new VelocityMacroProcessor();
            }
        }

        return sharedInstance;
    }

    public MacroContainer buildMacro(String macroTemplate) throws Exception {

        Macro macro = new MacroContainer();

        StringReader reader = new StringReader(macroTemplate.trim());

        return (MacroContainer) createMacro(reader, macro);
    }

    private Macro createMacro(Reader reader, Macro parent) throws Exception {

        StringBuilder characters = new StringBuilder();
        int prev = -1;
        int c;
        while ((c = reader.read()) != -1) {
            // begin macro
            if ('#' == c && prev != '\\') {
                addStringInstruction(parent, characters.toString());
                characters = new StringBuilder();

                Macro macro = getMacro(reader);
                macro = createMacro(reader, macro);
                if (macro instanceof Instruction) {
                    parent.addInstruction((Instruction) macro);
                }

                continue;
            }
            // end macro
            else if ('}' == c) {
                addStringInstruction(parent, characters.toString());
                return parent;
            }
            characters.append((char) c);
            
            prev = c;
        }

        addStringInstruction(parent, characters.toString());
        return parent;
    }

    private Macro getMacro(Reader reader) throws Exception {

        StringBuilder type = new StringBuilder();
        StringBuilder instructions = new StringBuilder();

        boolean switchInstruction = false;
        int c;
        while ((c = reader.read()) != -1) {

            // delimiter between macro type and macro instruction
            if (' ' == c || '(' == c) {
                switchInstruction = true;
                continue;
            }

            if (!switchInstruction) {
                type.append((char) c);
            } else {
                // ignore '{' and ')' brackets
                if ('{' == c) {
                    break;
                } else if (')' != c) {
                    instructions.append((char) c);
                }
            }
        }

        return initializeMacro(type.toString(), instructions.toString());
    }

    private Macro initializeMacro(String type, String instructions) {

        if ("if".equals(type)) {
        	return new IfMacro(instructions);
        } else if ("else if".equals(type)) {
            // todo:...
        } else if ("else".equals(type)) {
            // todo:...
        } else if ("while".equals(type)) {
            return new WhileMacro(instructions);
        } else if ("for".equals(type)) {
            return new ForMacro(instructions);
        } else if ("set".equals(type)) {
            return new SetMacro(instructions);
        } else if ("get".equals(type)) {
            return new GetMacro(instructions);
        } else {
            return new MethodCallMacro(type, instructions);
        }
        return null;
    }

    /**
     * Adds a <code>StringInstruction</code> to the macro. If the trimmed characters
     * string equals "" then this procedure will be skipped by default.
     * Skip prohibits empty strings. -> SHOULD RAISE PERFORMANCE
     *
     * @param macro      The macro that gets the string instruction or nothing if the
     *                   trimmed string equals "".
     * @param characters The character string.
     */
    private void addStringInstruction(Macro macro, String characters) {
        characters = removeUnnecessaryLineBreaks(characters);

        if (!"".equals(characters.trim())) {
            macro.addInstruction(new StringInstruction(characters));
        }
    }

    /**
     * Removes unnecessary line breaks at the end of the character string. This will
     * be performed to reduce the html output and also shows a pretty html format.
     *
     * @param characters The characters that may contain unnecessary line breaks.
     * @return A character string that doesn't contain line breaks at the end. The result
     *         can be the same as the paramter characters.
     */
    private String removeUnnecessaryLineBreaks(String characters) {
        int index = characters.lastIndexOf("\r\n");

        if (index != -1) {
            String tmp = characters.substring(index, characters.length());
            if ("".equals(tmp.trim())) {
                characters = characters.substring(0, characters.lastIndexOf("\r\n"));
            }
        }
        return characters;
    }
}
