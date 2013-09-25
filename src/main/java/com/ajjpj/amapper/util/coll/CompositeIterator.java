package com.ajjpj.amapper.util.coll;

import java.util.Iterator;

/**
 * This iterator concatenates other iterators.
 *
 * @author arno
 */
public class CompositeIterator<T> implements Iterator<T> {
    private final Iterator<Iterator<T>> itit;

    public CompositeIterator(Iterator<Iterator<T>> itit) {
        this.itit = itit;
    }

    public CompositeIterator(Iterable<Iterator<T>> itit) {
        this.itit = itit.iterator();
    }

    @SuppressWarnings("unchecked")
    private Iterator<T> curIterator = (Iterator<T>) EMPTY;

    @Override public boolean hasNext() {
        while(!curIterator.hasNext() && itit.hasNext()) {
            curIterator = itit.next();
        }

        return curIterator.hasNext();
    }

    @Override public T next() {
        return curIterator.next();
    }

    @Override public void remove() {
        curIterator.remove();
    }

    private static final Iterator<Object> EMPTY = new Iterator<Object> () {
        @Override public boolean hasNext() {
            return false;
        }

        @Override public Object next() {
            throw new UnsupportedOperationException();
        }

        @Override public void remove() {
            throw new UnsupportedOperationException();
        }
    };
}
