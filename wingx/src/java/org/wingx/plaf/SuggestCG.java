package org.wingx.plaf;

import org.wings.plaf.ContainerCG;
import org.wings.plaf.Update;
import org.wingx.XSuggest;

import java.util.Map;
import java.util.List;

public interface SuggestCG extends ContainerCG {
    
    public Update getSuggestionsUpdate(XSuggest suggest, String query, List<Map.Entry<String,String>> suggestions);
    
}
