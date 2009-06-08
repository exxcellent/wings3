/*
 * $Id: SOURCEHEADER 1313 2007-09-11 13:25:48Z hengels $
 * (c) Copyright 2007 osbl development team.
 *
 * This file is part of the OSBL (http://concern.sf.net).
 *
 * The OSBL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package org.wings.util;

import java.util.*;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class WeakArrayList<T> extends AbstractList<T> {
    private final ReferenceQueue<T> garbageCollected = new ReferenceQueue<T>();
    private final List<Reference<T>> list = new ArrayList<Reference<T>>();

    public WeakArrayList() {
    }

    @Override
    public boolean contains(Object o) {
        try {
            for (int i = 0; i < list.size(); ++i) {
                T t = list.get(i).get();
                if(t == null)
                    continue;
                if(t.equals(o))
                    return true;
            }
        } catch(IndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean add(final T obj) {
        updateList();

        return list.add(new WeakReference<T>(obj, garbageCollected));
    }

    @Override
    public void add(int index, final T obj) {
        updateList();

        list.add(index, new WeakReference<T>(obj, garbageCollected));
    }

    @Override
    public T remove(int index) {
        updateList();

        return list.remove(index).get();
    }

    public int size() {
        updateList();

        return list.size();
    }

    public T get(int index) {
        updateList();

        try {
            T obj = list.get(index).get();
            while (obj == null) {
                updateList();

                obj = list.get(index).get();
            }

            return obj;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public T set(int index, T element) {
        updateList();

        T old = list.get(index).get();
        list.set(index, new WeakReference<T>(element, garbageCollected));

        return old;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        updateList();

        List<T> returnList = new LinkedList<T>();
        for(int i=0; i<list.size(); ++i) {
            T obj = (T)list.get(i).get();
            if(obj == null)
                continue;

            returnList.add(obj);
        }
        return returnList.toArray(a);
    }

    @Override
    public Object[] toArray() {
        updateList();

        List<T> returnList = new LinkedList<T>();
        for(int i=0; i<list.size(); ++i) {
            T obj = list.get(i).get();
            if(obj == null)
                continue;

            returnList.add(obj);
        }
        return returnList.toArray();
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        updateList();

        return super.clone();
    }

    @Override
    public Iterator<T> iterator() {
        throw new RuntimeException("does not work - weaklists can't be iterated");
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new RuntimeException("does not work - weaklists can't be iterated");
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw new RuntimeException("does not work - weaklists can't be iterated");
    }

    private final void updateList() {
        Reference<? extends T> ref;
        while (true) {
            ref = garbageCollected.poll();
            if (ref == null)
                break;

            this.list.remove(ref);
        }
    }
}
