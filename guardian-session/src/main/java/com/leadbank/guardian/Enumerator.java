package com.leadbank.guardian;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class Enumerator implements Enumeration {

    public Enumerator(Iterator iterator) {
        super();
        this.iterator = iterator;
    }

    public Enumerator(Iterator iterator, boolean clone) {
        super();
        if (!clone) {
            this.iterator = iterator;
        } else {
            List list = new ArrayList();
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
            this.iterator = list.iterator();
        }
    }

    private Iterator iterator = null;

    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public Object nextElement() {
        return iterator.next();
    }
}
