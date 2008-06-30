/*
 * Copyright 2000,2005 wingS development team.
 *
 * This file is part of wingS (http://wingsframework.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package org.wings;

import org.wings.plaf.CmsLayoutCG;
import org.wings.template.TemplateSource;

import java.util.*;
import java.io.IOException;

/**
 * <code>CmsLayout<code>.
 * <p/>
 * User: raedler
 * Date: 08.08.2007
 * Time: 13:08:30
 *
 * @author raedler
 * @version $Id
 */
public class CmsLayout extends STemplateLayout {

    Map<TemplateSource,ComponentSet> componentSets = new HashMap<TemplateSource, ComponentSet>();

    protected void setCG(CmsLayoutCG cg) {
        super.setCG(cg);
    }

    public void addComponent(SComponent c, Object constraint, int index) {
        c.setName((String)constraint);
        componentSets.clear();
        super.addComponent(c, constraint, index);
    }

    public void removeComponent(SComponent comp) {
        componentSets.clear();
        super.removeComponent(comp);
    }

    public void setTemplate(TemplateSource source) throws IOException {
        super.setTemplate(source);
        Set<String> names = org.wings.plaf.css.CmsLayoutCG.getContainedComponents(this);
        final SContainer container = getContainer();

        CmsLayout.ComponentSet componentSet = componentSets.get(source);
        if (componentSet == null) {
            componentSet = new ComponentSet();
            for (SComponent component : container.getComponents()) {
                if (names.contains(component.getName()))
                    componentSet.contained.add(component);
                else
                    componentSet.notContained.add(component);
            }
            componentSets.put(source, componentSet);
        }

        for (SComponent containedComponent : componentSet.contained)
            containedComponent.setVisible(true);
        for (SComponent notContainedComponent : componentSet.notContained)
            notContainedComponent.setVisible(false);
    }

    private class ComponentSet
    {
        Set<SComponent> contained = new HashSet<SComponent>();
        Set<SComponent> notContained = new HashSet<SComponent>();
    }
}
