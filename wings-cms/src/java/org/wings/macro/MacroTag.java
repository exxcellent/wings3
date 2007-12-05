package org.wings.macro;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <code>MacroTag<code>.
 * <p/>
 * User: rrd
 * Date: 14.08.2007
 * Time: 15:24:04
 *
 * @author rrd
 * @version $Id
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MacroTag {
    String name() default "#default";
}
