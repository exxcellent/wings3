package org.wings.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.wings.SComponent;
import org.wings.SFrame;
import org.wings.SMenu;
import org.wings.SPopupMenu;

public class MenuManager {

    private Map<SComponent, Set<SComponent>> menuLinkMap = new HashMap<SComponent, Set<SComponent>>();

    public void registerMenuLink(SMenu menu, SComponent component) {
        register(menu, component);
    }

    public void registerMenuLink(SPopupMenu menu, SComponent component) {
        register(menu, component);
    }

    private void register(SComponent menu, SComponent component) {
        if (menu == null)
            throw new IllegalArgumentException("Menu must not be null!");

        Set<SComponent> components = getComponents(menu);
        components.add(component);
        menuLinkMap.put(menu, components);
    }

    public void deregisterMenuLink(SMenu menu, SComponent component) {
        deregister(menu, component);
    }

    public void deregisterMenuLink(SPopupMenu menu, SComponent component) {
        deregister(menu, component);
    }

    private void deregister(SComponent menu, SComponent component) {
        if (menu == null)
            throw new IllegalArgumentException("Menu must not be null!");

        Set<SComponent> components = getComponents(menu);
        components.remove(component);

        if (components.isEmpty()) {
            menuLinkMap.remove(menu);
        } else {
            menuLinkMap.put(menu, components);
        }
    }

    public boolean isMenuLinked(SMenu menu) {
        return !getComponents(menu).isEmpty();
    }

    public boolean isMenuLinked(SPopupMenu menu) {
        return !getComponents(menu).isEmpty();
    }

    public Set<SComponent> getMenueLinks(SMenu menu) {
        return getComponents(menu);
    }

    public Set<SComponent> getMenueLinks(SPopupMenu menu) {
        return getComponents(menu);
    }

    public Set<SComponent> getMenues() {
        return menuLinkMap.keySet();
    }

    public Set<SComponent> getMenues(SFrame frame) {
        Set<SComponent> menuesUsedInFrame = new HashSet<SComponent>(menuLinkMap.keySet());
        for (Iterator<SComponent> i = menuesUsedInFrame.iterator(); i.hasNext();) {
            if (i.next().getParentFrame() != frame) {
                i.remove();
            }
        }
        return menuesUsedInFrame;
    }

    private Set<SComponent> getComponents(SComponent menu) {
        Set<SComponent> links = menuLinkMap.get(menu);
        if (links == null) {
            links = new HashSet<SComponent>(2);
        }
        return links;
    }

}