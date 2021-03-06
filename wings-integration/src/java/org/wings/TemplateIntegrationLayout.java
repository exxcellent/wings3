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

import org.wings.plaf.TemplateIntegrationLayoutCG;
import org.wings.template.TemplateSource;
import org.wings.template.PropertyManager;

import java.util.*;
import java.io.IOException;

/**
 * @author hengels
 * @version $Id
 */
public class TemplateIntegrationLayout extends STemplateLayout {

    private static final long serialVersionUID = 1L;
    Map<String, ComponentSet> componentSets = new HashMap<String, ComponentSet>();

    protected void setCG(TemplateIntegrationLayoutCG cg) {
        super.setCG(cg);
    }

    public void addComponent(SComponent component, Object constraint, int index) {
        component.setName((String)constraint);
        super.addComponent(component, constraint, index);

        TemplateSource source = getTemplateSource();
        if (source != null) {
            TemplateIntegrationLayout.ComponentSet componentSet = componentSets.get(source.getCanonicalName());
            boolean contained = componentSet.names.contains(component.getName());
            component.setVisible(contained);
            if (contained)
                componentSet.contained.add(component);
            else
                componentSet.notContained.add(component);
        }
    }

    public void removeComponent(SComponent component) {
        super.removeComponent(component);

        TemplateSource source = getTemplateSource();
        if (source != null) {
            TemplateIntegrationLayout.ComponentSet componentSet = componentSets.get(source.getCanonicalName());
            componentSet.contained.remove(component);
            componentSet.notContained.remove(component);
        }
    }

    public void setTemplate(TemplateSource source) throws IOException {
        super.setTemplate(source);
        Set<String> names = org.wings.plaf.css.TemplateIntegrationLayoutCG.getContainedComponents(this);
        final SContainer container = getContainer();

        TemplateIntegrationLayout.ComponentSet componentSet = componentSets.get(source.getCanonicalName());
        if (componentSet == null) {
            componentSet = new ComponentSet();
            componentSet.names = names;
            componentSet.componentProperties = org.wings.plaf.css.TemplateIntegrationLayoutCG.getComponentProperties(this);
            for (SComponent component : container.getComponents()) {
                if (names.contains(component.getName()))
                    componentSet.contained.add(component);
                else
                    componentSet.notContained.add(component);
            }
            componentSets.put(source.getCanonicalName(), componentSet);
        }

        for (SComponent containedComponent : componentSet.contained)
            containedComponent.setVisible(true);
        for (SComponent notContainedComponent : componentSet.notContained)
            notContainedComponent.setVisible(false);

        for (SComponent component : componentSet.contained) {
            Map<String, String> properties = componentSet.componentProperties.get(component.getName());
            if (properties.size() > 0) {
                PropertyManager propManager = TemplateIntegrationLayout.getPropertyManager(component.getClass());

                if (propManager != null) {
                    Iterator<String> iter = properties.keySet().iterator();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        String value = properties.get(key);
                        propManager.setProperty(component, key, value);
                    }
                }
            }
        }
    }

    public TemplateIntegrationLayoutCG getCG() {
        return (TemplateIntegrationLayoutCG) super.getCG();
    }
    
    private class ComponentSet {
        Set<String> names;
        Set<SComponent> contained = new HashSet<SComponent>();
        Set<SComponent> notContained = new HashSet<SComponent>();
        Map<String, Map<String, String>> componentProperties;
    }
}
