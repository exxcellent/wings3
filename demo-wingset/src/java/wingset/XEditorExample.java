package wingset;

import org.wings.*;
import org.wingx.XDivision;
import org.wingx.XEditor;

import java.awt.*;

/**
 * <code>XEditorExample</code>.
 * 
 * <pre>
 * Date: Apr 1, 2008
 * Time: 7:56:29 PM
 * </pre>
 *
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public class XEditorExample extends WingSetPane {

    protected SComponent createControls() {
        return null;
    }

    protected SComponent createExample() {
        XEditor editor = new XEditor();
        
        return editor;
    }
}
