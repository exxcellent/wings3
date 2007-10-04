// (c) copyright 2006 by eXXcellent solutions, Ulm. Author: bschmid

package org.wings.resource;

import java.io.IOException;

/**
 * Exception thrown if a Resource could not be retrieved or
 * found (i.e. missing) and should be indicated with a 404!
 *
 * @author Benjamin Schmid <B.Schmid@exxcellent.de>
 */
public class ResourceNotFoundException extends IOException {

    /**
     * Default c'tor for an exception if a resource could not be found.
     * @param message A message describing the missing resource.
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }
}
